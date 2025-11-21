package com.romy.platform.main.common.service;

import com.romy.platform.annotation.PlatformTransactional;
import static com.romy.platform.main.common.dto.CompressDto.*;
import static com.romy.platform.main.common.dto.FileDto.SearchRes;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.common.converter.CompressConverter;
import static com.romy.platform.main.common.dto.FolderDto.ValidReq;

import static com.romy.platform.main.common.dvo.FolderFileSetDvo.FileInfo;
import static com.romy.platform.main.common.dvo.FolderFileSetDvo.FolderInfo;

import com.romy.platform.main.common.dvo.*;
import com.romy.platform.main.community.service.CommunityAuthService;

import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CompressService {

    @Value("${file.server-root-file-dir}")
    private String rootDir;

    @Value("${file.virtual-save-path}")
    private String savePath;

    private final CompressConverter converter;

    private final FileService fileService;
    private final FolderService folderService;
    private final IdGenerateService idGenService;
    private final CommunityAuthService commuAuthService;


    /**
     * 권한체크
     */
    private void checkTargetFileAuth(CompressReq dto) {
        // 대상 폴더/파일
        List<TargetFile> targets = dto.files();
        String folderGrp = targets.getFirst().fileGrp();
        String folderCd = dto.folderCd();

        // 소속부서 폴더인 경우만 권한 체크
        if ("DEPT".equals(folderGrp)) {
            this.commuAuthService.checkCommunityFolderAuth(folderCd);
        }

        Map<Boolean, List<String>> partitioned = targets.stream()
                                                        .collect(Collectors.partitioningBy(
                                                                file -> PlatformConstant.YN_Y.equals(file.folderYn()),
                                                                Collectors.mapping(TargetFile::fileAttCd, Collectors.toList())
                                                        ));

        List<String> folderCds = partitioned.get(true);
        List<String> fileCds = partitioned.get(false);

        ValidReq validDto = this.converter.dataToValidReq(folderGrp, fileCds, folderCds);
        this.folderService.checkFolderValidation(validDto);
    }

    /**
     * 압축하기
     */
    @PlatformTransactional
    public FileDvo createCompressFile(CompressReq dto) throws IOException {
        // 권한체크, 폴더권한, 파일 읽기 권한 체크
        this.checkTargetFileAuth(dto);

        String compressFileNm = dto.compressFileNm();
        String folderCd = dto.folderCd();
        String prjFolderYn = dto.prjFolderYn();
        // 대상 리스트
        List<TargetFile> targets = dto.files();
        TargetFile tgtFile = targets.getFirst();

        List<CompressDvo> compressTargets = new ArrayList<>();
        CompressDvo rootDvo = this.converter.dataToCompressDvo(PlatformConstant.YN_Y, compressFileNm + "/");
        rootDvo.setFileGrp(tgtFile.fileGrp());
        rootDvo.setPrjCd(tgtFile.prjCd());
        rootDvo.setProcCd(tgtFile.procCd());
        compressTargets.add(rootDvo);

        // 압축대상 파일 조회
        this.buildCompressTargetFiles(compressTargets, targets, compressFileNm);

        // zip파일 생성
        String zipFileAttCd = this.idGenService.getNextStringId();
        String dirPath = PlatformUtil.getPhyPath();
        Path zipFile = Paths.get(this.rootDir, this.savePath, dirPath, zipFileAttCd + ".zip");
        Path tgtPath = zipFile.getParent();
        if (Files.notExists(tgtPath)) {
            Files.createDirectories(tgtPath); // 상위 폴더까지 전부 생성
        }

        // 데이터 처리
        FileDvo fileDvo = new FileDvo();

        try {
            // 압축 파일 생성
            try (OutputStream out = Files.newOutputStream(zipFile);
                 ZipArchiveOutputStream archive = new ZipArchiveOutputStream(out)) {

                for (CompressDvo dvo : compressTargets) {
                    String folderPath = dvo.getFolderPath();

                    if (PlatformConstant.YN_Y.equals(dvo.getFolderYn())) {
                        ZipArchiveEntry entry = new ZipArchiveEntry(folderPath);
                        archive.putArchiveEntry(entry);
                        archive.closeArchiveEntry();
                    } else if (dvo.getTargetFile() != null && Files.exists(dvo.getTargetFile())) {
                        String fileNm = dvo.getFileNm();
                        ZipArchiveEntry entry = new ZipArchiveEntry(folderPath + fileNm);
                        archive.putArchiveEntry(entry);

                        try (InputStream in = Files.newInputStream(dvo.getTargetFile());
                             BufferedInputStream bin = new BufferedInputStream(in)) {
                            byte[] buffer = new byte[16384];
                            int len;
                            while ((len = bin.read(buffer)) != -1) {
                                archive.write(buffer, 0, len);
                            }
                        }

                        archive.closeArchiveEntry();
                    }
                }
                archive.finish();
            }

            fileDvo.setFileAttCd(zipFileAttCd);
            fileDvo.setFileNm(compressFileNm + ".zip");
            fileDvo.setFileExt("zip");
            fileDvo.setFilePhyPath(this.savePath + dirPath);
            fileDvo.setFileSize(Files.size(zipFile));
            fileDvo.setFileGrp(tgtFile.fileGrp());
            if (PlatformConstant.YN_N.equals(prjFolderYn)) {
                fileDvo.setFileRefCd(folderCd);
            } else {
                String fileRefCd;
                List<CompressDvo> filters = compressTargets.stream()
                                                           .filter(tgt -> PlatformConstant.YN_N.equals(tgt.getFolderYn()))
                                                           .toList();
                if (CollectionUtils.isNotEmpty(filters)) {
                    fileRefCd = filters.getFirst().getFileRefCd();
                } else {
                    FileAttFolderDvo folderDvo = this.folderService.getProjectFolderInfo(folderCd);
                    fileRefCd = folderDvo.getFolderRefCd();
                }
                fileDvo.setFileRefCd(fileRefCd);
            }
            fileDvo.setPrjCd(tgtFile.prjCd());
            fileDvo.setProcCd(tgtFile.procCd());
            fileDvo.setFileDlCnt(0);
            fileDvo.setFileFinalVerYn(PlatformConstant.YN_Y);
            fileDvo.setDelYn(PlatformConstant.YN_N);
            fileDvo.setFileSecurityYn(PlatformConstant.YN_N);
            // 파일 데이터 생성
            this.fileService.createFile(fileDvo);

            // 프로젝트 폴더-파일 데이터 생성
            if (PlatformConstant.YN_Y.equals(prjFolderYn)) {
                FileAttFolderLocDvo locDvo = this.converter.dataToFileAttFolderLocDvo(zipFileAttCd, folderCd);
                this.fileService.createFileAttFolderLoc(locDvo);
            }

        } catch (Exception e) {
            Files.deleteIfExists(zipFile);
            throw e;
        }

        return fileDvo;
    }

    /**
     * 압축대상 파일 만들기
     */
    private void buildCompressTargetFiles(List<CompressDvo> dvos, List<TargetFile> files, String folderPath) {
        if (CollectionUtils.isEmpty(files)) return;

        String path = folderPath.endsWith("/") ? folderPath : folderPath + "/" ;

        for (TargetFile file : files) {
            String folderYn = file.folderYn();
            String fileGrp = file.fileGrp();
            CompressDvo compDvo = this.converter.targetFileToCompressDvo(file);

            // 폴더인 경우
            if (PlatformConstant.YN_Y.equals(folderYn)) {
                String curPath = path + file.fileNm();
                String folderCd = file.fileAttCd();

                compDvo.setFolderPath(curPath + "/");
                dvos.add(compDvo);

                List<SearchRes> childFiles = this.fileService.getFolderFilesByFolderCd(fileGrp, folderCd);

                List<TargetFile> tgtChilds = this.converter.searchResToTargetFileList(childFiles, file);

                // 재귀
                this.buildCompressTargetFiles(dvos, tgtChilds, compDvo.getFolderPath());

            } else {
                String fileAttCd = file.fileAttCd();
                FileDownloadDvo downloadDvo = this.fileService.getFileInfoForDownload(fileAttCd);

                String phyFilePath = downloadDvo.getFilePhyPath();
                String phyFileNm = downloadDvo.getFilePhyNm();

                Path tgtFile = Paths.get(this.rootDir, phyFilePath, phyFileNm);
                if (Files.exists(tgtFile)) {
                    compDvo.setFolderPath(path);
                    compDvo.setTargetFile(tgtFile);
                    compDvo.setFileRefCd(downloadDvo.getFileRefCd());

                    dvos.add(compDvo);
                }
            }
        }
    }

    /**
     * 압축해제
     */
    @PlatformTransactional
    public int createDecompressFile(DecompressReq dto) throws IOException {
        int count = 0;

        String prjFolderYn = dto.prjFolderYn();
        String folderCd = dto.folderCd();
        String fileAttCd = dto.fileAttCd();

        // 파일정보
        FileDownloadDvo downloadDvo = this.fileService.getFileInfoForDownload(fileAttCd);
        String fileGrp = downloadDvo.getFileGrp();

        Integer folderLevel = null;
        FolderDvo folderDvo = null;
        FileAttFolderDvo prjFolderDvo = null;

        // 프로젝트 폴더가 아닌 경우
        if (PlatformConstant.YN_N.equals(prjFolderYn)) {
            folderDvo = this.folderService.getFolderInfo(folderCd);
            if (folderDvo == null) {
                // 폴더 정보를 찾을 수 없습니다.
                throw new PlatformException(PlatformConstant.COMMON_FOLDER_INFO_NOT_EXIST);
            }

        } else {
            prjFolderDvo = this.folderService.getProjectFolderInfo(folderCd);
            if (prjFolderDvo == null) {
                // 폴더 정보를 찾을 수 없습니다.
                throw new PlatformException(PlatformConstant.COMMON_FOLDER_INFO_NOT_EXIST);
            }

            folderLevel = prjFolderDvo.getLevel();
        }

        int level = folderLevel == null ? 1 : folderLevel;
        FolderFileSetDvo setDvo = this.execDecompress(downloadDvo, folderCd, level);

        try {
            List<FolderInfo> sortedFolders = setDvo.getFolders().stream()
                                                   .sorted(Comparator.comparingInt(FolderInfo::getLevel))
                                                   .toList();

            for (FolderInfo folder : sortedFolders) {
                if (PlatformConstant.YN_N.equals(prjFolderYn)) {

                    FolderDvo dvo = this.converter.folderInfoToFolderDvo(folder);
                    dvo.setFolderGrp(fileGrp);
                    dvo.setSectorCd(folderDvo.getSectorCd());

                    String newFolderCd = this.folderService.createChildFolder(dvo);
                    count += StringUtils.isNotBlank(newFolderCd) ? 1 : 0;

                } else {
                    FileAttFolderDvo dvo = this.converter.folderInfoToFileAttFolderDvo(folder);
                    dvo.setLevel(folder.getLevel() + 1);
                    dvo.setFolderGrp(fileGrp);
                    dvo.setFolderRefCd(prjFolderDvo.getFolderRefCd());

                    count += this.folderService.createFileAttFolder(dvo);
                }
            }

            FileDvo fileDvo = this.fileService.getFileInfo(fileAttCd);

            for (FileInfo file : setDvo.getFiles()) {
                String fileRefCd = file.getParentFolderCd();
                FileDvo dvo = this.converter.fileInfoToFileDvo(file);
                dvo.setFileGrp(fileGrp);
                dvo.setPrjCd(fileDvo.getPrjCd());
                dvo.setProcCd(fileDvo.getProcCd());

                if (PlatformConstant.YN_N.equals(prjFolderYn)) {
                    dvo.setFileRefCd(fileRefCd);
                } else {
                    dvo.setFileRefCd(fileDvo.getFileRefCd());
                }

                count += this.fileService.createFile(dvo);

                if (PlatformConstant.YN_Y.equals(prjFolderYn)) {
                    FileAttFolderLocDvo locDvo = this.converter.dataToFileAttFolderLocDvo(dvo.getFileAttCd(), fileRefCd);
                    this.fileService.createFileAttFolderLoc(locDvo);
                }
            }
        } catch (Exception e) {
            for (Path path : setDvo.getPhyFiles()) {
                Files.deleteIfExists(path);
            }
            throw e;
        }

        return count;
    }

    /**
     * 압축해제
     */
    private FolderFileSetDvo execDecompress(FileDownloadDvo fileDvo, String parentCd, int level) throws IOException {
        Set<FolderInfo> folders = new HashSet<>();
        List<FileInfo> files = new ArrayList<>();
        List<Path> phyFiles = new ArrayList<>();

        // 압축파일
        Path tgtFile = Paths.get(this.rootDir, fileDvo.getFilePhyPath(), fileDvo.getFilePhyNm());
        Path parent = tgtFile.getParent();
        String parentPath = parent.toString().replace(File.separatorChar, '/');
        String relativePath = parentPath.replaceFirst("^" + Pattern.quote(this.rootDir), "");

        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }

        if (!relativePath.endsWith("/")) {
            relativePath += "/";
        }

        try (ZipFile zipFile = ZipFile.builder().setFile(tgtFile.toFile()).setCharset("EUC-KR").get()) {

            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String fileAttCd = this.idGenService.getNextStringId();

                String name = entry.getName();
                String ext = PlatformUtil.getExtension(name);
                String fileName = Paths.get(name).getFileName().toString();
                String fileNm = PlatformUtil.replaceLast(fileName, "." + ext, "");

                Path outputPath = Paths.get(parent.toString(), fileAttCd + "." + ext);

                // 폴더 구조 처리
                String folderPath = name.contains("/") ? name.substring(0, name.lastIndexOf('/') + 1) : "";

                if (StringUtils.isNotBlank(folderPath) && StringUtils.isNotBlank(ext)) {
                    StringBuilder pathBuilder = new StringBuilder();
                    List<String> parts = Arrays.asList(folderPath.split("/"));
                    for (int i = 0; i < parts.size(); i++) {
                        if (parts.get(i).isBlank()) continue;

                        pathBuilder.append(parts.get(i)).append("/");
                        int folderDept = level + i + 1;

                        FolderInfo folder = new FolderInfo();

                        folder.setFolderName(parts.get(i));
                        folder.setLevel(folderDept);
                        folder.setFolderCd(this.idGenService.getNextStringId());
                        folder.setFolderPath(pathBuilder.toString());

                        if (i == 0) {
                            folder.setParentCd(parentCd);
                        } else {
                            String parentName = parts.get(i - 1);
                            folders.stream().filter(v -> v.getFolderName().equals(parentName))
                                   .findFirst().ifPresent(v -> folder.setParentCd(v.getFolderCd()));
                        }
                        folders.add(folder);
                    }
                }

                // 파일 처리
                if (!entry.isDirectory()) {
                    try (InputStream is = zipFile.getInputStream(entry);
                         OutputStream os = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                        byte[] buffer = new byte[16384]; // 16KB
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }

                    phyFiles.add(outputPath);

                    FileInfo dvo = new FileInfo();

                    dvo.setFileAttCd(fileAttCd);
                    dvo.setFileName(fileNm + "." + ext);
                    dvo.setFileExt(ext);
                    dvo.setFilePhyPath(relativePath);
                    dvo.setFileSize(Files.size(outputPath));
                    dvo.setFolderName(folderPath.isBlank() ? null : folderPath);

                    files.add(dvo);
                }
            }

            // 파일-폴더 매핑
            boolean allRoot = files.stream().map(FileInfo::getFolderName).allMatch(Objects::isNull);

            if (allRoot) {
                files.forEach(v -> v.setParentFolderCd(parentCd));
            } else {
                files.forEach(v -> {
                    String folderName = v.getFolderName();
                    folders.stream()
                           .filter(f -> Objects.equals(f.getFolderPath(), folderName))
                           .findFirst()
                           .ifPresentOrElse(
                                   f -> v.setParentFolderCd(f.getFolderCd()),
                                   () -> v.setParentFolderCd(parentCd)
                           );
                });
            }

            FolderFileSetDvo result = new FolderFileSetDvo();

            result.setFiles(files);
            result.setFolders(folders);
            result.setPhyFiles(phyFiles);

            return result;
        }
    }
}
