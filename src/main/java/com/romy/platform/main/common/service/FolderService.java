package com.romy.platform.main.common.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.common.converter.FolderConverter;
import static com.romy.platform.main.common.dto.FileDto.VersionRes;

import com.romy.platform.main.common.dvo.*;
import com.romy.platform.main.common.mapper.FolderMapper;
import com.romy.platform.main.community.service.CommunityAuthService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.romy.platform.main.common.dto.FolderDto.*;
import static com.romy.platform.main.community.dto.CommunityAuthDto.AuthRes;


@Service
@RequiredArgsConstructor
public class FolderService {

    @Value("${file.server-root-file-dir}")
    private String rootDir;

    @Value("${file.virtual-save-path}")
    private String savePath;

    private final FolderMapper mapper;
    private final FolderConverter converter;

    private final FileService fileService;
    private final CopyService copyService;

    private final TrashService trashService;
    private final FileGrpService fileGrpService;
    private final IdGenerateService idGenService;
    private final CommunityAuthService commuAuthService;


    /**
     * 폴더 하위구조 조회
     */
    public List<FolderChildDvo> getFolderChilds(String folderCd, String menuType) {

        String tableNm = "PLT_FOLDER";
        if ("PRJ".equals(menuType)) {
            tableNm = "PLT_FILE_ATT_FOLDER";
        }
        return this.mapper.selectFolderChilds(folderCd, tableNm);
    }

    /**
     * 폴더 다운로드 이력생성
     */
    @PlatformTransactional
    public int createFolderDownloadHistory(CreateDownReq dto) {
        List<String> folderCds = dto.folderCds();
        List<String> fileCds = dto.fileCds();

        String folderGrp = dto.folderGrp();
        String folDownGrp = dto.folDownGrp();

        int count = 0;

        if (CollectionUtils.isNotEmpty(folderCds)) {
            for (String folderCd : folderCds) {
                FolderHistoryDvo dvo = this.converter.dataToFolderHistoryDvo(folderCd, folderGrp, folDownGrp);

                String historyCd = this.idGenService.getNextStringId();
                dvo.setHistoryCd(historyCd);

                count += this.mapper.insertFolderDownloadHistory(dvo);
            }
        }

        if (CollectionUtils.isNotEmpty(folderCds) || CollectionUtils.isNotEmpty(fileCds)) {
            FolderUpDownHistDvo histDvo = this.converter.dataToFolderUpDownHistDvo(folDownGrp, "D", folderGrp, "X");

            String fafUdCd = this.idGenService.getNextStringId();
            histDvo.setFafUdCd(fafUdCd);

            count += this.mapper.insertFolderFileUpDownHistory(histDvo);
        }

        return count;
    }

    /**
     * 폴더 업로드 이력 생성
     */
    @PlatformTransactional
    public int createFolderUploadHistory(CreateUpReq dto) {
        String folderGrp = dto.folderGrp();
        String folUpGrp = dto.folUpGrp();

        FolderUpDownHistDvo dvo = this.converter.dataToFolderUpDownHistDvo(folUpGrp, "U", folderGrp, "R");

        String fafUdCd = this.mapper.selectFolderUpDownCode(folUpGrp, "U", folderGrp);
        if (StringUtils.isEmpty(fafUdCd)) {
            fafUdCd = this.idGenService.getNextStringId();
            dvo.setEndType("X");
        }
        dvo.setFafUdCd(fafUdCd);

        return this.mapper.mergeFolderFileUpDownHistory(dvo);
    }

    /**
     * 업/다운로드 이력 결과 업데이트
     */
    @PlatformTransactional
    public int updateFolderUpDownHistory(UpdateHistReq dto) {

        String upType = dto.udType();
        String folderGrp = dto.folderGrp();
        String folGrp = dto.folGrp();
        String endType = dto.endType();

        String fafUdCd = this.mapper.selectFolderUpDownCode(folGrp, upType, folderGrp);
        if (StringUtils.isEmpty(fafUdCd)) {
            // 요청에 실패했습니다.
            throw new PlatformException(PlatformConstant.COMMON_REQUEST_FAIL);
        }

        return this.mapper.updateFolderUpDownEndType(fafUdCd, endType);
    }

    /**
     * 폴더 전체 트리구조 조회
     */
    public List<FolderTreeDvo> getFolderAllTrees(String folderGrp, String sectorCd) {
        return this.mapper.selectFolderAllTrees(folderGrp, sectorCd);
    }

    /**
     * 중복 폴더명 처리
     */
    @PlatformTransactional
    public void updateDupFolderName(String folderGrp, String sectorCd) {
        // 업데이트는 권한체크 없음
        List<FolderTreeDvo> dvos = this.mapper.selectFolderAllTreesForUpdate(folderGrp, sectorCd);

        if (CollectionUtils.isEmpty(dvos)) return;

        Map<String, Map<String, List<FolderTreeDvo>>> grouped =
                dvos.stream()
                    .collect(Collectors.groupingBy(FolderTreeDvo::getParentCd,
                            Collectors.groupingBy(FolderTreeDvo::getFolderNm)));

        for (Map<String, List<FolderTreeDvo>> nameMap : grouped.values()) {
            // 충돌 방지
            Set<String> usedNames = new HashSet<>(nameMap.keySet());

            for (List<FolderTreeDvo> duplicates : nameMap.values()) {
                if (duplicates.size() <= 1) continue;

                List<FolderTreeDvo> sorted = duplicates.stream()
                                                       .sorted(Comparator.comparing(FolderTreeDvo::getFolderCd))
                                                       .toList();

                boolean first = true;
                for (FolderTreeDvo item : sorted) {
                    if (first) { // 첫 항목은 유지
                        first = false;
                        continue;
                    }
                    String orgName = item.getFolderNm();
                    String newName = PlatformUtil.generateUniqueName(orgName, usedNames);
                    if (!orgName.equals(newName)) {
                        item.setFolderNm(newName);
                        this.mapper.updateFolderName(item.getFolderCd(), newName);
                    }
                    usedNames.add(newName);
                }
            }
        }
    }

    /**
     * 폴더 수정
     */
    @PlatformTransactional
    public void updateFolder(FolderDvo dvo) {
        this.mapper.updateFolder(dvo);
    }

    /**
     * 폴더정보 조회
     */
    public FolderDvo getFolderInfo(String folderCd) {
        return this.mapper.selectFolderInfo(folderCd);
    }

    /**
     * 하위 폴더 생성
     */
    @PlatformTransactional
    public String createChildFolder(FolderDvo dvo) {
        String parentCd = dvo.getParentCd();
        // 부모폴더 조회
        FolderDvo parentDvo = this.getFolderInfo(parentCd);
        if (parentDvo == null && !"ROOT".equals(parentCd)) return "";

        String folderGrp = dvo.getFolderGrp();
        String sectorCd = dvo.getSectorCd();
        // 커뮤니티 폴더인 경우 권한 체크
        if ("DEPT".equals(folderGrp)) {
            String authYn = this.commuAuthService.getFolderCreatePermission(sectorCd, dvo.getRegUsrCd());
            if (!PlatformConstant.YN_Y.equals(authYn)) {
                // 폴더 생성 권한이 없습니다.
                throw new PlatformException(PlatformConstant.COMMON_FOLDER_NOT_PERMISSION);
            }
        }

        String folderCd = dvo.getFolderCd();
        if (StringUtils.isEmpty(folderCd)) {
            folderCd = this.idGenService.getNextStringId();
        }
        dvo.setFolderCd(folderCd);

        int maxOrdNum = this.mapper.selectOrdNumMaxByParentCd(parentCd);
        dvo.setOrdNum(maxOrdNum + 1);

        this.mapper.insertFolder(dvo);

        return folderCd;
    }

    /**
     * 프로젝트 폴더 생성
     */
    @PlatformTransactional
    public int createFileAttFolder(FileAttFolderDvo dvo) {
        return this.mapper.insertFileAttFolder(dvo);
    }

    /**
     * 하위 폴더 유효성 체크
     */
    public void checkFolderValidation(ValidReq dto) {
        String folderGrp = dto.folderGrp();

        String action = dto.action();
        // 복사인 경우 읽기 권한만 체크
        String writeYn = "COPY".equals(action) ? PlatformConstant.YN_N : PlatformConstant.YN_Y;

        List<String> folders = dto.folderCds();
        List<String> files = dto.fileCds();

        if (CollectionUtils.isNotEmpty(folders)) {
            for (String folderCd : folders) {
                // 폴더 권한체크
                this.checkFolderChildAuth(folderCd, folderGrp, writeYn);
            }
        }

        if (CollectionUtils.isNotEmpty(files)) {
            // 편집 권한이 없는 파일 조회
            FilePermissionCondDvo condDvo = this.converter.dataToFilePermissionCondDvo(folderGrp, writeYn);
            condDvo.setFileCds(files);

            List<String> notPermissions = this.fileService.getNoPermissionFiles(condDvo);
            if (CollectionUtils.isNotEmpty(notPermissions)) {
                // 권한 없는 파일이 존재합니다.
                throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FILE);
            }
        }
    }

    /**
     * 폴더 권한체크
     */
    private void checkFolderChildAuth(String folderCd, String folderGrp, String writeYn) {
        List<String> notPermissions = this.mapper.selectNoPermissionFolders(folderCd, folderGrp);
        if (CollectionUtils.isNotEmpty(notPermissions)) {
            // 권한 없는 폴더가 존재합니다.
            throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FOLDER);
        }

        // 편집 권한이 없는 파일 조회
        FilePermissionCondDvo condDvo = this.converter.dataToFilePermissionCondDvo(folderGrp, writeYn);
        condDvo.setFolderCd(folderCd);

        notPermissions = this.fileService.getNoPermissionFiles(condDvo);
        if (CollectionUtils.isNotEmpty(notPermissions)) {
            // 권한 없는 파일이 존재합니다.
            throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FILE);
        }
    }

    /**
     * 단일 폴더 권한체크
     */
    private void checkFolderAuth(String folderCd) {
        String noPermission = this.mapper.selectNoPermissionFolder(folderCd);
        if (StringUtils.isNotEmpty(noPermission)) {
            // 권한 없는 폴더가 존재합니다.
            throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FOLDER);
        }
    }

    /**
     * 폴더/파일 삭제
     */
    @PlatformTransactional
    public int removeFolderAndChild(RemoveReq dto) {
        ValidReq validDto = this.converter.removeReqToValidReq(dto);

        // 유효성검사 체크
        this.checkFolderValidation(validDto);

        String folderGrp = dto.folderGrp();

        List<String> folders = dto.folderCds();
        List<String> files = dto.fileCds();

        String menuPath = this.fileGrpService.getMenuPathByFileGrp(folderGrp);

        // 폴더코드에 대한 삭제처리
        List<TrashDvo> trashs = this.removeFileByFolderCds(folders, menuPath);
        // 파일코드에 대한 삭제처리
        trashs.addAll(this.removeFileByFileCds(files, menuPath));

        if (CollectionUtils.isNotEmpty(trashs)) {
            this.trashService.insertTrashs(trashs);
        }

        return trashs.size();
    }

    /**
     * 폴더 경로 조회
     */
    public String getFolderPathFolderCd(String folderCd) {
        return this.mapper.selectFolderPath(folderCd);
    }

    /**
     * 파일 리스트 삭제
     */
    private List<TrashDvo> removeFileByFileCds(List<String> fileCds, String menuPath) {
        List<TrashDvo> datas = new ArrayList<>();

        if (CollectionUtils.isEmpty(fileCds)) return datas;

        for (String fileCd : fileCds) {
            FileDvo fileDvo = this.fileService.getFileInfo(fileCd);
            if (fileDvo == null) continue;

            String trashGrp = PlatformUtil.getRandomString(20);

            TrashDvo trashDvo = this.converter.fileDvoToTrashDvo(fileDvo);
            trashDvo.setTrashCd(this.idGenService.getNextStringId());
            trashDvo.setTrashGrp(trashGrp);

            String folderPath = this.getFolderPathFolderCd(fileDvo.getFileRefCd());
            trashDvo.setLocationDetail(menuPath + "\n" + folderPath);
            trashDvo.setRootYn(PlatformConstant.YN_Y);
            datas.add(trashDvo);

            this.fileService.removeFile(fileCd);
        }

        return datas;
    }

    /**
     * 폴더 리스트 삭제
     */
    private List<TrashDvo> removeFileByFolderCds(List<String> folderCds, String menuPath) {

        List<TrashDvo> datas = new ArrayList<>();

        if (CollectionUtils.isEmpty(folderCds)) return datas;

        for (String folderCd : folderCds) {
            int count = 0;

            // 하위폴더 조회
            List<FolderChildDvo> childs = this.getFolderChilds(folderCd, "CAB");
            String trashGrp = PlatformUtil.getRandomString(20);

            for (FolderChildDvo dvo : childs) {
                TrashDvo folderTrash = this.converter.folderChildDvoToTrashDvo(dvo);
                folderTrash.setTrashCd(this.idGenService.getNextStringId());
                folderTrash.setTrashGrp(trashGrp);

                String folderPath = this.getFolderPathFolderCd(dvo.getParentCd());
                if (StringUtils.isEmpty(folderPath)) {
                    folderPath = this.mapper.selectFolderRootPath(dvo.getFolderCd());

                    if (folderPath == null) folderPath = "";
                }

                folderTrash.setLocationDetail(menuPath + "\n" + folderPath);
                folderTrash.setRootYn(count == 0 ? PlatformConstant.YN_Y : PlatformConstant.YN_N);

                datas.add(folderTrash);

                // 폴더코드
                String fileRefCd = dvo.getFolderCd();
                // 폴더그룹
                String fileGrp = dvo.getFolderGrp();

                List<FileDvo> files = this.fileService.getFilesByFileRefCd(fileGrp, fileRefCd, true);

                for (FileDvo file : files) {
                    TrashDvo trashDvo = this.converter.fileDvoToTrashDvo(file);

                    trashDvo.setTrashCd(this.idGenService.getNextStringId());
                    trashDvo.setTrashGrp(trashGrp);
                    trashDvo.setLocationDetail(menuPath + "\n" + folderPath);

                    datas.add(trashDvo);
                }

                // 파일 삭제
                this.fileService.removeFilesByFileRefCd(fileGrp, fileRefCd);
                // 폴더 삭제
                this.removeFolder(fileRefCd);

                count++;
            }
        }

        return datas;
    }

    /**
     * 복사/잘라내기 - 붙여넣기
     */
    @PlatformTransactional
    public int createCopyCutPaste(ClipboardReq dto, String action) throws IOException {
        ValidReq validDto = this.converter.clipboardReqToValidReq(dto, action);
        // 유효성 체크
        this.checkFolderValidation(validDto);

        // target 폴더 권한체크
        String targetFolderCd = dto.folderCd();
        this.checkFolderAuth(targetFolderCd);

        if ("CUT".equals(action)) {
            return this.updateCutPaste(dto);
        } else if ("COPY".equals(action)) {
            return this.createCopyPaste(dto);
        }

        return 1;
    }

    /**
     * 잘라내기 - 붙여넣기
     */
    private int updateCutPaste(ClipboardReq dto) {
        int count = 0;

        List<String> folderCds = dto.folderCds();
        List<String> fileCds = dto.fileCds();
        String fileRefCd = dto.folderCd();
        String folderGrp = dto.folderGrp();
        String targetPrjCd = dto.prjCd();
        String targetProcCd = dto.procCd();

        // 파일 참조코드 변경
        if (CollectionUtils.isNotEmpty(fileCds)) {
            List<FileInfoDvo> dvos = new ArrayList<>(fileCds.size());
            for (String fileCd : fileCds) {
                FileInfoDvo infoDvo = new FileInfoDvo();
                infoDvo.setFileAttCd(fileCd);
                infoDvo.setPrjCd(targetPrjCd);
                infoDvo.setProcCd(targetProcCd);

                dvos.add(infoDvo);
            }

            count += this.fileService.updateFileRefCd(dvos, fileRefCd, folderGrp);

            // 이력 생성
            this.copyService.createCopyHistoryForFileCut(fileCds, folderGrp);
        }

        // 폴더 부모코드 변경
        if (CollectionUtils.isNotEmpty(folderCds)) {
            FolderDvo targetDvo = this.getFolderInfo(fileRefCd);
            if (targetDvo == null) {
                // 폴더 정보를 찾을 수 없습니다.
                throw new PlatformException(PlatformConstant.COMMON_FOLDER_INFO_NOT_EXIST);
            }

            for (String folderCd : folderCds) {
                this.createCopyHistoryForCut(folderCd, folderGrp, targetDvo);
            }

            count += this.mapper.updateParentFolderCd(targetDvo, folderCds);
        }

        return count == 0 ? 1 : count;
    }

    /**
     * 잘라내기 이력 생성
     */
    private void createCopyHistoryForCut(String folderCd, String folderGrp, FolderDvo targetDvo) {
        // 하위폴더 조회
        List<FolderChildDvo> childs = this.mapper.selectFolderChildsForCut(folderCd, "PLT_FOLDER");

        String sectorCd = targetDvo.getSectorCd();

        for (FolderChildDvo dvo : childs) {
            String oldFolderCd = dvo.getFolderCd();
            String oldFolderGrp = dvo.getFolderGrp();

            // 폴더그룹코드, 부문코드 업데이트
            FolderDvo oldFolderDvo = this.converter.dataToFolderDvo(oldFolderCd, folderGrp, sectorCd);
            this.updateFolder(oldFolderDvo);

            // 파일 조회 후 잘라내기 이력 생성
            List<FileDvo> files = this.fileService.getFilesByFileRefCd(oldFolderGrp, oldFolderCd, false);
            List<String> fileAttCds = files.stream().map(FileDvo::getFileAttCd).toList();

            if (!oldFolderGrp.equals(folderGrp)) {
                List<FileInfoDvo> updateDvos = new ArrayList<>(files.size());
                String prjCd = targetDvo.getPrjCd();
                String procCd = targetDvo.getProcCd();

                for (FileDvo file : files) {
                    FileInfoDvo fileDvo = new FileInfoDvo();
                    fileDvo.setFileAttCd(file.getFileAttCd());
                    fileDvo.setPrjCd(prjCd);
                    fileDvo.setProcCd(procCd);
                    updateDvos.add(fileDvo);
                }

                this.fileService.updateFileRefCd(updateDvos, oldFolderCd, folderGrp);
            }

            // 이력 생성
            this.copyService.createCopyHistoryForFileCut(fileAttCds, folderGrp);
        }
    }

    /**
     * 복사-붙여넣기
     */
    private int createCopyPaste(ClipboardReq dto) throws IOException {
        String targetFolderCd = dto.folderCd();

        List<String> fileCds = dto.fileCds();
        List<String> folderCds = dto.folderCds();
        List<Path> phyFiles = new ArrayList<>();

        String newFilePhyPath = this.savePath + PlatformUtil.getPhyPath();
        FolderDvo targetDvo = this.getFolderInfo(targetFolderCd);
        if (targetDvo == null) {
            // 폴더 정보를 찾을 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FOLDER_INFO_NOT_EXIST);
        }
        targetDvo.setPrjCd(dto.prjCd());
        targetDvo.setProcCd(dto.procCd());

        try {
            // 파일 복사
            if (CollectionUtils.isNotEmpty(fileCds)) {
                for (String fileCd : fileCds) {
                    Path phyFile = this.createFileForCopy(targetDvo, newFilePhyPath, fileCd);
                    phyFiles.add(phyFile);
                }
            }

            // 폴더 복사
            if (CollectionUtils.isNotEmpty(folderCds)) {
                for (String folderCd : folderCds) {
                    List<Path> folderFiles = this.createFolderForCopy(folderCd, targetDvo, newFilePhyPath);
                    phyFiles.addAll(folderFiles);
                }
            }
        } catch (Exception e) {
            for (Path path : phyFiles) {
                Files.deleteIfExists(path);
            }
            throw e;
        }

        return 1;
    }

    /**
     * 파일 복사
     */
    private Path createFileForCopy(FolderDvo targetDvo, String newFilePhyPath, String orgFileCd) throws IOException {

        FileDownloadDvo downDvo = this.fileService.getFileInfoForDownload(orgFileCd);
        String orgFileNm = downDvo.getFileNm();
        String filePhyPath = downDvo.getFilePhyPath();
        String filePhyNm = downDvo.getFilePhyNm();
        String oldFileRefCd = downDvo.getFileRefCd();
        String oldFileGrp = downDvo.getFileGrp();

        Path source = Paths.get(this.rootDir, filePhyPath, filePhyNm);
        if (Files.notExists(source)) {
            // 파일 메시지 처리
            this.fileService.handleMissingFileByGroup(oldFileGrp, oldFileRefCd, orgFileNm);
        }

        String newFileAttCd = this.idGenService.getNextStringId();
        String fileExt = PlatformUtil.getExtension(filePhyNm);
        String newPhyFileNm = newFileAttCd + (StringUtils.isBlank(fileExt) ? "" : "." + fileExt);

        Path target = Paths.get(this.rootDir, newFilePhyPath, newPhyFileNm);
        Path tgtPath = target.getParent();
        if (Files.notExists(tgtPath)) {
            Files.createDirectories(tgtPath); // 상위 폴더까지 전부 생성
        }

        // 파일 복사
        try (InputStream in = Files.newInputStream(source);
             OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE
                     , StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[16384];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // 파일 데이터 생성
        FileCopyDvo dvo = this.converter.dataToFileCopyDvo(orgFileCd, newFileAttCd, newFilePhyPath
                , targetDvo.getFolderGrp(), targetDvo.getFolderCd(), targetDvo.getPrjCd(), targetDvo.getProcCd());

        // 복사 이력 생성
        CopyHistoryDvo histDvo = this.converter.fileCopyDvoToCopyHistoryDvo(dvo);
        histDvo.setFName(downDvo.getFileNm());
        histDvo.setFRefCd(oldFileRefCd);
        histDvo.setFGrp(downDvo.getFileGrp());
        this.copyService.createCopyHistory(histDvo);

        this.fileService.createFileByCopy(dvo);

        return target;
    }

    /**
     * 폴더 복사
     */
    private List<Path> createFolderForCopy(String folderCd, FolderDvo rootDvo, String newFilePhyPath) throws IOException {

        String folderGrp = rootDvo.getFolderGrp();
        String sectorCd = rootDvo.getSectorCd();
        String targetFolderCd = rootDvo.getFolderCd();
        List<Path> phyFiles = new ArrayList<>();

        // 하위폴더 조회
        List<FolderChildDvo> childs = this.getFolderChilds(folderCd, "CAB");

        Map<String, String> folderMapping = new HashMap<>(childs.size());

        for (int i=0, size=childs.size(); i < size; i++) {
            FolderChildDvo dvo = childs.get(i);

            String oldFolderCd = dvo.getFolderCd();
            String oldParentCd = dvo.getParentCd();
            String oldFolderNm = dvo.getFolderNm();
            String oldFolderGrp = dvo.getFolderGrp();

            String newFolderCd = this.idGenService.getNextStringId();
            folderMapping.put(oldFolderCd, newFolderCd);

            // 1레벨의 폴더는 target 폴더코드로 매핑해야 함.
            if (i > 0) {
                targetFolderCd = folderMapping.get(oldParentCd);
            }

            // 폴더생성
            FolderDvo folderDvo = this.converter.dataToFolderDvo(newFolderCd, oldFolderNm, targetFolderCd, folderGrp, sectorCd);
            folderDvo.setPrjCd(rootDvo.getPrjCd());
            folderDvo.setProcCd(rootDvo.getProcCd());

            int maxOrdNum = this.mapper.selectOrdNumMaxByParentCd(targetFolderCd);
            folderDvo.setOrdNum(maxOrdNum + 1);

            this.mapper.insertFolder(folderDvo);

            List<FileDvo> files = this.fileService.getFilesByFileRefCd(oldFolderGrp, oldFolderCd, true);
            // 파일 복사
            for (FileDvo file : files) {
                phyFiles.add(this.createFileForCopy(folderDvo, newFilePhyPath, file.getFileAttCd()));
            }
        }

        return phyFiles;
    }

    /**
     * 복사/붙여넣기
     */
    @PlatformTransactional
    public int createCopyDatas(CopyCutReq dto, String div) {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        String usrCd = userDvo.getUsrCd();

        String writeYn = "COPY".equals(div) ? PlatformConstant.YN_N : PlatformConstant.YN_Y;

        // 데이터 복구
        this.updateCutDatas();

        List<CopyTemporaryDvo> dvos = new ArrayList<>();
        List<CopyTemp> temps = dto.datas();

        List<String> delFileCds = new ArrayList<>();
        List<String> authFileCds = new ArrayList<>();

        String fileGrp = temps.getFirst().fileGrp();

        for (CopyTemp temp : temps) {
            String dataType = temp.dataType();
            String folderCd = temp.folderCd();
            String fileAttCd = temp.fileAttCd();
            String parentFolderCd = temp.parentFolderCd();

            if ("FOLDER".equals(dataType)) {
                MessageProvider.checkNotNullData(folderCd, PlatformConstant.FOLDER_CD);
            } else if ("FILE".equals(dataType)) {
                MessageProvider.checkNotNullData(fileAttCd, PlatformConstant.FILE_ATT_CD);
            }

            String flag = this.getClipboardFlag(fileGrp, parentFolderCd);

            // 잘라내기의 경우 삭제
            if ("CUT".equals(div)) {
                if ("FOLDER".equals(dataType)) {
                    if (flag.contains("p")) {
                        this.mapper.deleteProjectFolder(folderCd);
                    } else {
                        this.removeFolder(folderCd);
                    }

                } else if ("FILE".equals(dataType)) {
                    List<VersionRes> versions = this.fileService.getFileVersionsForMng(fileAttCd);
                    List<String> fileVerCds = versions.stream().map(VersionRes::fileAttCd).toList();
                    delFileCds.addAll(fileVerCds);
                }
            }

            // 권한 체크 시작
            if ("FOLDER".equals(dataType)) {
                if (flag.contains("p")) {
                    // PLT_FILE_ATT_FOLDER 권한
                    this.checkProjectFolderAuth(folderCd, fileGrp, writeYn);
                } else {
                    // PLT_FOLDER 권한
                    this.checkFolderChildAuth(folderCd, fileGrp, writeYn);
                }
            } else if ("FILE".equals(dataType)) {
                authFileCds.add(fileAttCd);
            }

            CopyTemporaryDvo dvo = this.converter.copyTempToCopyTemporaryDvo(temp);
            dvo.setCtCd(this.idGenService.getNextStringId());
            dvo.setDiv(div);
            dvos.add(dvo);
        }

        // 권한이 없는 파일 조회
        if (CollectionUtils.isNotEmpty(authFileCds)) {
            FilePermissionCondDvo condDvo = this.converter.dataToFilePermissionCondDvo(fileGrp, writeYn);
            condDvo.setFileCds(authFileCds);
            List<String> notPermissions = this.fileService.getNoPermissionFiles(condDvo);
            if (CollectionUtils.isNotEmpty(notPermissions)) {
                // 권한 없는 파일이 존재합니다.
                throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FILE);
            }
        }

        this.fileService.removeFilesOnlyStatus(delFileCds);

        // 복사개체대상 삭제 및 생성
        this.copyService.removeCopyTemporary(usrCd);

        this.copyService.createCopyTemporarys(dvos);

        return 1;
    }

    /**
     * 잘라내기 데이터 복구
     */
    private void updateCutDatas() {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        String usrCd = userDvo.getUsrCd();

        List<CopyTemporaryDvo> cutDatas = this.copyService.getCopyTemporary(usrCd, "CUT", null);

        for (CopyTemporaryDvo cutData : cutDatas) {
            String type = cutData.getDataType();
            String folderCd = cutData.getFolderCd();
            String fileGrp = cutData.getBeforeFileGrp();
            String parentFolderCd = cutData.getBeforeParentFolderCd();

            String flag = this.getClipboardFlag(fileGrp, parentFolderCd);

            if ("FOLDER".equals(type)) {
                if (flag.contains("p")) {
                    FileAttFolderDvo dvo = this.converter.folderCdToFileAttFolderDvo(folderCd);

                    if (!"pp".equals(flag)) {
                        dvo.setParentCd(parentFolderCd);
                    }

                    this.mapper.updateProjectFolderOnlyField(dvo);

                } else {
                    FolderDvo dvo = this.converter.folderCdToFolderDvo(folderCd);
                    this.mapper.updateFolderOnlyField(dvo);
                }
            } else if ("FILE".equals(type)) {
                String fileAttCd = cutData.getFileAttCd();
                String prjCd = cutData.getBeforePrjCd();
                String procCd = cutData.getBeforeProcCd();

                FileDvo dvo = this.fileService.getFileInfo(fileAttCd);

                String fileVerGrpCd = dvo.getFileVerGrpCd();
                if (StringUtils.isNotEmpty(fileVerGrpCd)) {
                    List<FileDvo> versions = this.fileService.getFileVersions(dvo);
                    for (FileDvo version : versions) {
                        this.fileService.updateFileAttOnlyField(version);
                    }
                }
                dvo = this.converter.copyTemporaryDvoToFileDvo(cutData);
                dvo.setPrjCd(StringUtils.isEmpty(prjCd) ? "" : prjCd);
                dvo.setProcCd(StringUtils.isEmpty(procCd) ? "" : procCd);

                this.fileService.updateFileAttOnlyField(dvo);
            }
        }
    }

    /**
     * clipboard flag 조회
     */
    private String getClipboardFlag(String fileGrp, String parentFolderCd) {
        return switch (fileGrp) {
            case "PROJECT_PROCESS_DOC" -> StringUtils.isEmpty(parentFolderCd) ? "pp" : "pf";
            default -> "cf";
        };
    }

    /**
     * 프로젝트 폴더 권한체크
     */
    private void checkProjectFolderAuth(String folderCd, String fileGrp, String writeYn) {
        List<String> notPermissions = this.mapper.selectNoPermissionProjectFolders(folderCd);
        if (CollectionUtils.isNotEmpty(notPermissions)) {
            // 권한 없는 폴더가 존재합니다.
            throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FOLDER);
        }

        // 폴더 하위 조회
        List<FolderChildDvo> childs = this.getFolderChilds(folderCd, "PRJ");
        if (CollectionUtils.isNotEmpty(childs)) {
            List<String> folderCds = childs.stream().map(FolderChildDvo::getFolderCd).toList();
            // 편집 권한이 없는 파일 조회
            FilePermissionCondDvo condDvo = this.converter.dataToFilePermissionCondDvo(fileGrp, writeYn);
            condDvo.setRefCds(folderCds);

            notPermissions = this.fileService.getNoPermissionFiles(condDvo);
            if (CollectionUtils.isNotEmpty(notPermissions)) {
                // 권한 없는 파일이 존재합니다.
                throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FILE);
            }
        }
    }

    /**
     * 붙여넣기
     */
    @PlatformTransactional
    public int createPaste(PasteReq dto) throws IOException {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        String usrCd = userDvo.getUsrCd();

        List<CopyTemporaryDvo> dvos = this.copyService.getCopyTemporary(usrCd, null, null);
        if (CollectionUtils.isEmpty(dvos)) {
            // 클립보드가 비어 있습니다.
            throw new PlatformException(PlatformConstant.COMMON_CLIPBOARD_EMPTY);
        }

        String folderGrp = dto.folderGrp();
        String targetFolderCd = dto.folderCd();
        String targetPrjCd = dto.prjCd();
        String targetProcCd = dto.procCd();

        // target 폴더 권한체크
        this.checkFolderAuth(targetFolderCd);

        String newFilePhyPath = this.savePath + PlatformUtil.getPhyPath();

        FolderDvo targetDvo = this.getFolderInfo(targetFolderCd);
        if (targetDvo == null) {
            // 폴더 정보를 찾을 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FOLDER_INFO_NOT_EXIST);
        }
        targetDvo.setPrjCd(targetPrjCd);
        targetDvo.setProcCd(targetProcCd);

        List<String> cutFileCds = new ArrayList<>(dvos.size());
        List<String> cutFolderCds = new ArrayList<>(dvos.size());
        List<Path> phyFiles = new ArrayList<>();

        try {
            for (CopyTemporaryDvo dvo : dvos) {
                String div = dvo.getDiv();
                String dataType = dvo.getDataType();
                String fileAttCd = dvo.getFileAttCd();
                String folderCd = dvo.getFolderCd();

                if ("COPY".equals(div)) {

                    if ("FILE".equals(dataType)) {
                        Path phyPath = this.createFileForCopy(targetDvo, newFilePhyPath, fileAttCd);
                        phyFiles.add(phyPath);

                    } else if ("FOLDER".equals(dataType)) {
                        List<Path> phyPaths = this.createFolderForCopy(folderCd, targetDvo, newFilePhyPath);
                        phyFiles.addAll(phyPaths);
                    }
                } else if ("CUT".equals(div)) {
                    if ("FILE".equals(dataType)) {
                        List<VersionRes> versionFiles = this.fileService.getFileVersionsForMng(fileAttCd);
                        cutFileCds.addAll(versionFiles.stream().map(VersionRes::fileAttCd).toList());
                    } else if ("FOLDER".equals(dataType)) {

                        cutFolderCds.add(folderCd);
                        this.createCopyHistoryForCut(folderCd, folderGrp, targetDvo);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(cutFileCds)) {
                List<FileInfoDvo> updateDvos = new ArrayList<>(cutFileCds.size());
                for (String fileAttCd : cutFileCds) {
                    FileInfoDvo dvo = new FileInfoDvo();
                    dvo.setFileAttCd(fileAttCd);
                    dvo.setPrjCd(targetPrjCd);
                    dvo.setProcCd(targetProcCd);
                    updateDvos.add(dvo);
                }

                this.fileService.updateFileRefCd(updateDvos, targetFolderCd, folderGrp);

                // 이력 생성
                this.copyService.createCopyHistoryForFileCut(cutFileCds, folderGrp);
            }

            if (CollectionUtils.isNotEmpty(cutFolderCds)) {
                this.mapper.updateParentFolderCd(targetDvo, cutFolderCds);
            }

            // 클립보드 삭제
            this.copyService.removeCopyTemporary(usrCd);

        } catch (Exception e) {
            for (Path path : phyFiles) {
                Files.deleteIfExists(path);
            }
            throw e;
        }

        return 1;
    }

    /**
     * 폴더 사이즈 조회
     */
    public long getFolderSize(String folderCd) {
        return this.getFolderSize(folderCd, null, PlatformConstant.YN_Y);
    }


    /**
     * 폴더속성 조회
     */
    public PropRes getFolderProperties(String folderCd, String menuType) {
        if ("CAB".equals(menuType)) {
            long fileSize = this.getFolderSize(folderCd, null, PlatformConstant.YN_Y);

            // 폴더정보 조회
            FolderDvo dvo = this.getFolderInfo(folderCd);
            if (dvo == null) {
                // 폴더 정보를 찾을 수 없습니다.
                throw new PlatformException(PlatformConstant.COMMON_FOLDER_INFO_NOT_EXIST);
            }
            String folderGrp = dvo.getFolderGrp();
            // 메뉴정보
            String menuPath = this.fileGrpService.getMenuPathByFileGrp(folderGrp);
            // 폴더경로
            String folderPath = this.getFolderPathFolderCd(folderCd);
            List<AuthRes> auths = null;
            // 커뮤니티 파일 권한 체크
            if ("DEPT".equals(folderGrp) || "GROUP".equals(folderGrp)) {
                auths = this.commuAuthService.getCommunityAuthorizations(folderCd);
            }

            return this.converter.dataToPropRes(dvo, menuPath, folderPath, fileSize, auths);

        }

        return null;
    }

    /**
     * 폴더 용량 조회
     */
    public long getFolderSize(String folderCd, String usrCd, String finalYn) {
        return this.mapper.selectFolderSize(folderCd, usrCd, finalYn);
    }

    /**
     * 프로젝트 폴더 정보 조회
     */
    public FileAttFolderDvo getProjectFolderInfo(String folderCd) {
        return this.mapper.selectProjectFolderInfo(folderCd);
    }

    /**
     * 폴더 삭제
     */
    @PlatformTransactional
    public void removeFolder(String folderCd) {
        this.mapper.deleteFolder(folderCd);
    }


}
