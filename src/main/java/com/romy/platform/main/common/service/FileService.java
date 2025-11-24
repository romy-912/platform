package com.romy.platform.main.common.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.common.utils.PlatformUtil;

import com.romy.platform.main.common.converter.FileConverter;

import com.romy.platform.main.common.dvo.*;
import com.romy.platform.main.common.mapper.FileMapper;
import com.romy.platform.main.community.dto.CommunityAuthDto;
import com.romy.platform.main.community.service.CommunityAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tika.Tika;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import org.mp4parser.Box;
import org.mp4parser.IsoFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.romy.platform.main.common.dto.FileDto.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.server-root-file-dir}")
    private String rootDir;

    @Value("${file.virtual-save-path}")
    private String savePath;

    @Value("${file.temp-excel-file.path}")
    private String tempPath;

    private final FileMapper fileMapper;
    private final FileConverter converter;

    private final FileGrpService fileGrpService;
    private final IdGenerateService idGenService;
    private final CommunityAuthService commuAuthService;


    /**
     * 파일그룹, 파일참조코드에 해당하는 파일리스트 조회
     */
    public List<FileDvo> getFilesByFileRefCd(String fileGrp, String fileRefCd, boolean isFinal) {
        // 파일그룹, 파일참조코드 필수체크
        MessageProvider.checkNotNullData(fileGrp, PlatformConstant.FILE_GRP);
        MessageProvider.checkNotNullData(fileRefCd, PlatformConstant.FILE_REF_CD);

        String finalYn = isFinal ? PlatformConstant.YN_Y : PlatformConstant.YN_N;

        return this.fileMapper.selectFilesByFileRefCd(fileGrp, fileRefCd, finalYn);
    }

    /**
     * 파일정보 조회
     */
    public FileDvo getFileInfo(String fileAttCd) {
        return this.fileMapper.selectFileInfo(fileAttCd);
    }

    /**
     * 파일그룹, 파일참조코드에 해당하는 파일 삭제
     */
    @PlatformTransactional
    public void removeFilesByFileRefCd(String fileGrp, String fileRefCd) {
        // 파일그룹, 파일참조코드 필수체크
        MessageProvider.checkNotNullData(fileGrp, PlatformConstant.FILE_GRP);
        MessageProvider.checkNotNullData(fileRefCd, PlatformConstant.FILE_REF_CD);

        this.fileMapper.deleteFilesByFileRefCd(fileGrp, fileRefCd);
    }

    /**
     * 파일 업로드
     */
    @PlatformTransactional
    public List<FileInfoDvo> createFileUpload(List<MultipartFile> files, UploadReq dto) throws IOException {

        List<FileInfoDvo> dvos = new ArrayList<>();
        List<Path> createdFiles = new ArrayList<>();
        String folderCd = dto.folderCd();

        Path dirPath = Paths.get(this.rootDir, this.savePath, PlatformUtil.getPhyPath());
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath); // 상위 폴더까지 전부 생성
        }

        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                FileDvo dvo = this.multipartToFileDvo(file);
                dvo.setFileGrp(dto.fileGrp());
                dvo.setFileRefCd(dto.fileRefCd());
                dvo.setPrjCd(dto.prjCd());
                dvo.setProcCd(dto.procCd());

                String fileAttCd = dvo.getFileAttCd();

                // 물리파일명
                String phyFileNm = this.getPhyFileNm(dvo);
                // 물리경로
                String phyFilePath = this.rootDir + dvo.getFilePhyPath();
                Path target = Paths.get(phyFilePath, phyFileNm);

                try (InputStream in = file.getInputStream();
                     OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                    byte[] buffer = new byte[16384];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                createdFiles.add(target);

                // 데이터 생성
                this.createFile(dvo);

                // 폴더코드가 존재할 경우 FOLDER_LOC 데이터 생성
                if (StringUtils.isNotEmpty(folderCd)) {
                    this.fileMapper.insertFileAttFolderLoc(fileAttCd, folderCd);
                }

                FileInfoDvo infoDvo = this.converter.fileDvoToFileInfoDvo(dvo);
                dvos.add(infoDvo);
            }
        } catch (Exception e) {
            // 오류 발생 시 파일 삭제
            for (Path path : createdFiles) {
                Files.deleteIfExists(path);
            }
            throw e;
        }

        return dvos;
    }


    /**
     * 파일 버전 업로드
     */
    @PlatformTransactional
    public FileDvo createFileUploadForVersion(MultipartFile file, VersionUploadReq dto) throws IOException {

        String folderCd = dto.folderCd();
        String fileVerGrpCd = dto.fileVerGrpCd();
        String beforeFileAttCd = dto.beforeFileAttCd();

        FileDvo fileInfo = this.getFileInfo(beforeFileAttCd);
        if (fileInfo == null) {
            // 파일 정보를 찾을 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FILE_INFO_NOT_EXIST);
        }
        String orgFileExt = fileInfo.getFileExt();

        if (StringUtils.isBlank(fileVerGrpCd) || "null".equals(fileVerGrpCd)) {
            fileVerGrpCd = this.idGenService.getNextStringId();
        }

        // 영구지정 여부 조회
        String permanent = this.fileMapper.selectFilePermanent(beforeFileAttCd);

        FileDvo dvo = this.multipartToFileDvo(file);
        // 파일 확장자가 다를 경우
        if (!orgFileExt.equals(dvo.getFileExt())) {
            // 확장자가 다른 경우 업로드할 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FILE_EXTENSION);
        }

        dvo.setFileGrp(dto.fileGrp());
        dvo.setFileRefCd(dto.fileRefCd());
        dvo.setFileVerGrpCd(fileVerGrpCd);
        dvo.setPrjCd(dto.prjCd());
        dvo.setProcCd(dto.procCd());
        dvo.setPermanent(permanent);

        // 기존 파일 버전그룹, 최종버전 업데이트
        FileDvo befDvo = new FileDvo();
        befDvo.setFileAttCd(beforeFileAttCd);
        befDvo.setFileVerGrpCd(fileVerGrpCd);
        befDvo.setFileFinalVerYn(PlatformConstant.YN_N);
        this.updateFileAttInfo(befDvo);

        String fileAttCd = dvo.getFileAttCd();

        // 물리파일명
        String phyFileNm = this.getPhyFileNm(dvo);
        // 물리경로
        String phyFilePath = this.rootDir + dvo.getFilePhyPath();
        Path target = Paths.get(phyFilePath, phyFileNm);
        Path tgtPath = target.getParent();
        if (Files.notExists(tgtPath)) {
            Files.createDirectories(tgtPath); // 상위 폴더까지 전부 생성
        }

        try {

            try (InputStream in = file.getInputStream();
                 OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                byte[] buffer = new byte[16384];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // 데이터 생성
            this.createFile(dvo);

            // 폴더코드가 존재할 경우 FOLDER_LOC 데이터 생성
            if (StringUtils.isNotEmpty(folderCd)) {
                this.fileMapper.insertFileAttFolderLoc(fileAttCd, folderCd);
            }

            // 이력등록
            this.createFileAttFwRequest(fileAttCd, "version", null);
        } catch (Exception e) {
            Files.deleteIfExists(target);
            throw e;
        }

        return dvo;
    }

    /**
     * 파일생성
     */
    @PlatformTransactional
    public int createFile(FileDvo dvo) {
        return this.fileMapper.insertFile(dvo);
    }

    /**
     * 파일 참조코드 업데이트
     */
    @PlatformTransactional
    public int updateFileRefCd(List<FileInfoDvo> dvos, String fileRefCd, String fileGrp) {
        if(CollectionUtils.isEmpty(dvos)) return 0;

        // 파일참조 코드 필수 체크
        MessageProvider.checkNotNullData(fileRefCd, PlatformConstant.FILE_REF_CD);

        int count = 0;

        for (FileInfoDvo dvo : dvos) {
            count += this.fileMapper.updateFileRefCd(dvo.getFileAttCd(), fileRefCd, fileGrp, dvo.getPrjCd(), dvo.getProcCd());
        }

        return count;
    }

    /**
     * 파일 삭제 (단건)
     */
    @PlatformTransactional
    public int removeFile(String fileAttCd) {
        return this.fileMapper.deleteFile(fileAttCd);
    }

    /**
     * 파일 삭제 (다건)
     */
    @PlatformTransactional
    public int removeFiles(List<String> fileAttCds) {
        int count = 0;

        for (String fileAttCd : fileAttCds) {
            count += this.fileMapper.deleteFile(fileAttCd);
        }

        return count;
    }

    /**
     * 파일 정보 조회 (다운로드 용)
     */
    public FileDownloadDvo getFileInfoForDownload(String fileAttCd) {
        FileDownloadDvo dvo = this.fileMapper.selectFileInfoForDownload(fileAttCd);
        if (dvo == null) {
            // 파일 정보를 찾을 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FILE_INFO_NOT_EXIST);
        }
        return dvo;
    }

    /**
     * 파일 다운로드
     */
    @PlatformTransactional
    public void getFileDownload(String fileAttCd, HttpServletRequest request, HttpServletResponse response) {

        FileDownloadDvo dvo = this.getFileInfoForDownload(fileAttCd);

        String fileNm = dvo.getFileNm();
        String filePhyPath = dvo.getFilePhyPath();
        String filePhyNm = dvo.getFilePhyNm();
        String fileGrp = dvo.getFileGrp();
        String fileRefCd = dvo.getFileRefCd();

        Path target = Paths.get(this.rootDir, filePhyPath, filePhyNm);
        if (Files.notExists(target)) {
            // 파일 메시지 처리
            this.handleMissingFileByGroup(fileGrp, fileRefCd, fileNm);
        }

        if (PlatformUtil.hasSession()) {
            // 다운로드 이력생성
            this.fileMapper.insertFileDownloadHistory(fileAttCd, "DOWNLOAD");
        }

        try {
            // Content-Type 설정
            response.setContentType("application/octet-stream");

            // Content-Disposition (브라우저별 한글 파일명 인코딩)
            String userAgent = request.getHeader("User-Agent");
            String contentDisposition = PlatformUtil.buildContentDisposition(fileNm, userAgent);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

            // Content-Length 설정
            response.setContentLengthLong(Files.size(target));

            // 대용량 파일 스트리밍 전송
            try (InputStream in = Files.newInputStream(target);
                 OutputStream out = response.getOutputStream()) {

                byte[] buffer = new byte[16384];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }

        } catch (IOException e) {
            log.error("파일 다운로드 실패 - fileAttCd: {}", fileAttCd, e);
            throw new RuntimeException("파일 다운로드 중 오류 발생", e);
        }
    }

    /**
     * 파일 요청 이력 생성
     */
    @PlatformTransactional
    public void createFileAttFwRequest(String fileAttCd, String fwRequest, String usrCd) {
        this.fileMapper.insertFileAttFwRequest(fileAttCd, fwRequest, usrCd);
    }

    /**
     * 파일정보 업데이트
     */
    @PlatformTransactional
    public void updateFileAttInfo(FileDvo dvo) {
        this.fileMapper.updateFileAttInfo(dvo);
    }

    /**
     * 파일정보 업데이트 (Audit 제외)
     */
    @PlatformTransactional
    public void updateFileAttOnlyField(FileDvo dvo) {
        this.fileMapper.updateFileAttOnlyField(dvo);
    }


    /**
     * 파일 복사에 따른 파일 생성
     */
    @PlatformTransactional
    public void createFileByCopy(FileCopyDvo dvo) {
        this.fileMapper.insertFileByCopy(dvo);
    }

    /**
     * 프로젝트 폴더 위치 생성
     */
    @PlatformTransactional
    public void createFileAttFolderLoc(FileAttFolderLocDvo dvo) {
        this.fileMapper.insertFileAttFolderLoc(dvo);
    }


    /**
     * 권한 없는 파일 조회
     */
    public List<String> getNoPermissionFiles(FilePermissionCondDvo dvo) {
        List<String> refCds = dvo.getRefCds();
        List<String> fileCds = dvo.getFileCds();
        String folderCd = dvo.getFolderCd();

        if (CollectionUtils.isEmpty(refCds) && CollectionUtils.isEmpty(fileCds) && StringUtils.isBlank(folderCd)) return null;

        return this.fileMapper.selectNoPermissionFiles(dvo);
    }

    /**
     * Multipart convert FileDvo
     */
    private FileDvo multipartToFileDvo(MultipartFile file) {
        FileDvo dvo = new FileDvo();

        String fileAttCd = this.idGenService.getNextStringId();
        String fileNm = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename();
        String fileExt = PlatformUtil.getExtension(fileNm);
        String filePhyPath = this.savePath + PlatformUtil.getPhyPath();
        long fileSize = file.getSize();
        int fileDlCnt = 0;

        dvo.setFileAttCd(fileAttCd);
        dvo.setFileNm(fileNm);
        dvo.setFileExt(fileExt);
        dvo.setFilePhyPath(filePhyPath);
        dvo.setFileSize(fileSize);
        dvo.setFileDlCnt(fileDlCnt);
        dvo.setFileFinalVerYn(PlatformConstant.YN_Y);
        dvo.setDelYn(PlatformConstant.YN_N);
        dvo.setFileSecurityYn(PlatformConstant.YN_N);

        return dvo;
    }

    /**
     * 물리파일명 조회
     */
    private String getPhyFileNm(FileDvo dvo) {
        String fileExt = dvo.getFileExt();
        return dvo.getFileAttCd() + (StringUtils.isBlank(fileExt) ? "" : "." + fileExt);
    }

    /**
     * 파일 버전 목록 조회
     */
    public List<FileDvo> getFileVersions(FileDvo dvo) {
        return this.fileMapper.selectFileVersions(dvo);
    }

    /**
     * 삭제 상태 업데이트
     */
    @PlatformTransactional
    public void removeFilesOnlyStatus(List<String> fileCds) {
        if (CollectionUtils.isEmpty(fileCds)) return;

        this.fileMapper.deleteFilesOnlyStatus(fileCds);
    }

    /**
     * 폴더 코드에 해당하는 파일/폴더 조회
     */
    public List<SearchRes> getFolderFilesByFolderCd(SearchReq dto) {
        return this.fileMapper.selectFolderFilesByFolderCd(dto);
    }

    /**
     * 폴더 코드에 해당하는 파일/폴더 조회
     */
    public List<SearchRes> getFolderFilesByFolderCd(String folderGrp, String folderCd) {
        return this.fileMapper.selectFolderFilesByFolderCd(folderGrp, folderCd);
    }

    /**
     * 중복 파일명 처리
     */
    @PlatformTransactional
    public void updateDupFileName(SearchReq dto) {
        String folderGrp = dto.folderGrp();
        String folderCd = dto.folderCd();

        List<FileDvo> files = this.getFilesByFileRefCd(folderGrp, folderCd, true);
        if (CollectionUtils.isEmpty(files)) return;

        // 파일명+확장자 기준으로 그룹핑 (대소문자 구분 없이, 확장자 포함)
        Map<String, List<FileDvo>> grouped = files.stream()
                                                  .collect(Collectors.groupingBy(file -> {
                                                      String fileName = file.getFileNm();
                                                      String ext = PlatformUtil.getExtension(fileName);
                                                      String baseName = PlatformUtil.replaceLast(fileName, "." + ext, "");
                                                      return baseName.toLowerCase() + "." + ext.toLowerCase(); // 소문자 비교용 key
                                                  }));

        // 이미 존재하는 이름 세트 (실제 파일명 그대로)
        Set<String> usedNames = files.stream()
                                     .map(FileDvo::getFileNm)
                                     .collect(Collectors.toSet());

        for (List<FileDvo> duplicates : grouped.values()) {
            if (duplicates.size() <= 1) continue;

            // 수정일 기준 정렬
            duplicates.sort(Comparator.comparing(FileDvo::getModDtt));

            boolean first = true;
            for (FileDvo item : duplicates) {
                if (first) { // 첫 파일은 그대로 두기
                    first = false;
                    continue;
                }

                String fileName = item.getFileNm();
                String ext = PlatformUtil.getExtension(fileName);
                String baseName = PlatformUtil.replaceLast(fileName, "." + ext, "");

                String newName = fileName;
                int counter = 1;
                while (containsIgnoreCase(usedNames, newName)) {
                    newName = baseName + " (" + counter++ + ")." + ext;
                }

                if (!fileName.equals(newName)) {
                    usedNames.add(newName);
                    FileDvo fileDvo = this.converter.dataToFileDvo(item.getFileAttCd(), newName);
                    this.fileMapper.updateFileAttInfo(fileDvo);
                }
            }
        }
    }

    private boolean containsIgnoreCase(Set<String> set, String target) {
        for (String s : set) {
            if (s.equalsIgnoreCase(target)) return true;
        }
        return false;
    }

    /**
     * 파일 썸네일 조회
     */
    public byte[] getFileThumbnail(String fileAttCd) throws IOException {
        FileDownloadDvo dvo = this.getFileInfoForDownload(fileAttCd);

        String filePhyNm = dvo.getFilePhyNm();
        String ext = PlatformUtil.getExtension(filePhyNm).toLowerCase();
        String filePhyPath = dvo.getFilePhyPath();

        Path target = Paths.get(this.rootDir, filePhyPath, filePhyNm);
        if (Files.notExists(target)) {
            // 파일 정보를 찾을 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FILE_INFO_NOT_EXIST);
        }

        // 문서
        if ("docx".equals(ext) || "pptx".equals(ext) || "xlsx".equals(ext)) {
            return this.extractDocumentThumbnail(target);

            // 이미지
        } else if (ext.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return this.resizeImage(target);

            // 동영상
        } else if (ext.matches("mp4|mov|avi|mkv")) {
            return this.extractVideoThumbnail(target.toFile());

            // 도면
        } else if ("dwg".equals(ext)) {
            return this.extractDwgThumbnail(target);

        } else if ("pdf".equals(ext)) {
            return this.extractPdfThumbnail(target);
        }

        return new byte[0];

    }

    /**
     * Document 썸네일 추출
     */
    private byte[] extractDocumentThumbnail(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().startsWith("docprops/thumbnail")) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[16384];
                    int read;
                    while ((read = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    return baos.toByteArray();
                }
            }
        }
        return new byte[0];
    }

    /**
     * 이미지 썸네일
     */
    private byte[] resizeImage(Path filePath) throws IOException {
        BufferedImage original = ImageIO.read(Files.newInputStream(filePath));
        if (original == null) return null;

        int maxSize = 200;
        int width = original.getWidth();
        int height = original.getHeight();

        double scale = Math.min((double) maxSize / width, (double) maxSize / height);
        if (scale < 1) { // 원본보다 큰 경우만 축소
            width = (int) (width * scale);
            height = (int) (height * scale);
        }

        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = thumbnail.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "png", baos);
        return baos.toByteArray();
    }

    /**
     * 동영상 썸네일
     */
    private byte[] extractVideoThumbnail(File videoFile) throws IOException {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
        try {
            // 초기화 속도 최적화
            grabber.setOption("probesize", "32");        // 최소 probe size
            grabber.setOption("analyzeduration", "0");   // 분석 시간 최소화
            grabber.setOption("skip_frame", "nokey");
            grabber.start();

            try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                Frame frame = grabber.grabImage();
                if (frame == null || frame.image == null) return new byte[0];

                BufferedImage bufferedImage = converter.convert(frame);

                // 필요 시 바로 리사이즈 (썸네일 크기 조절)
                int targetWidth = 320;
                int targetHeight = (int) (bufferedImage.getHeight() * (320.0 / bufferedImage.getWidth()));

                Image scaled = bufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g = thumbnail.createGraphics();
                g.drawImage(scaled, 0, 0, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(thumbnail, "jpeg", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            log.error("썸네일 추출 실패: {}", e.getMessage());
            return new byte[0];
        } finally {
            grabber.stop();
            grabber.release(); // 네이티브 리소스 완전 해제
        }
    }

    /**
     * dwg 썸네일 추출
     */
    private byte[] extractDwgThumbnail(Path filePath) throws IOException {
        byte[] fileBuffer = Files.readAllBytes(filePath);
        int fileSize = fileBuffer.length;

        ByteBuffer buffer = ByteBuffer.wrap(fileBuffer).order(ByteOrder.LITTLE_ENDIAN);

        // DWG 썸네일 오프셋 (13~16바이트 little endian)
        int thumbnailOffset = buffer.getInt(13);
        if (thumbnailOffset == 0 || thumbnailOffset >= fileSize) {
            log.error("잘못된 썸네일 오프셋");
            return new byte[0];
        }

        // Sentinel 확인
        byte[] startSentinel = {
                (byte)0x1f, (byte)0x25, (byte)0x6d, (byte)0x07,
                (byte)0xd4, (byte)0x36, (byte)0x28, (byte)0x28,
                (byte)0x9d, (byte)0x57, (byte)0xca, (byte)0x3f,
                (byte)0x9d, (byte)0x44, (byte)0x10, (byte)0x2b
        };
        byte[] sentinel = Arrays.copyOfRange(fileBuffer, thumbnailOffset, thumbnailOffset + 16);
        if (!Arrays.equals(sentinel, startSentinel)) {
            log.error("센티넬 불일치");
            return new byte[0];
        }

        int numObjects = Byte.toUnsignedInt(fileBuffer[thumbnailOffset + 20]);

        int offset = thumbnailOffset + 21;
        for (int i = 0; i < numObjects; i++) {
            int objType = Byte.toUnsignedInt(fileBuffer[offset]);
            int objOffset = ByteBuffer.wrap(fileBuffer, offset + 1, 4)
                                      .order(ByteOrder.LITTLE_ENDIAN).getInt();
            int objSize = ByteBuffer.wrap(fileBuffer, offset + 5, 4)
                                    .order(ByteOrder.LITTLE_ENDIAN).getInt();
            offset += 9;

            if ((objType == 2 || objType == 3 || objType == 6) && objOffset + objSize <= fileSize) {
                byte[] imageData = Arrays.copyOfRange(fileBuffer, objOffset, objOffset + objSize);
                byte[] header = Arrays.copyOfRange(imageData, 0, 16);

                // PNG
                if (startsWith(header, new byte[]{(byte)0x89, 0x50, 0x4E, 0x47})) {
                    return imageData;
                }
                // DIB (BMP 헤더 없음)
                else if (startsWith(header, new byte[]{0x28, 0x00, 0x00, 0x00})) {
                    return buildBMPFromDIB(imageData);
                }
                // BMP
                else if (startsWith(header, new byte[]{0x42, 0x4D})) {
                    return imageData;
                }
                // JPG
                else if (startsWith(header, new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF})) {
                    return imageData;
                }
                // 알 수 없는 경우
                else {
                    return imageData;
                }
            }
        }

        System.err.println("유효한 썸네일 객체를 찾을 수 없습니다.");
        return new byte[0];
    }

    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }

    /**
     * DIB 데이터를 BMP로 변환
     */
    private byte[] buildBMPFromDIB(byte[] dibData) {
        int fileSize = 14 + dibData.length;
        int dibHeaderSize = ByteBuffer.wrap(dibData, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int bitsPerPixel = ByteBuffer.wrap(dibData, 14, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
        int paletteSize = (bitsPerPixel == 8) ? 256 * 4 : 0;
        int pixelDataOffset = 14 + dibHeaderSize + paletteSize;

        byte[] bmpHeader = new byte[14];
        bmpHeader[0] = 0x42; // 'B'
        bmpHeader[1] = 0x4D; // 'M'

        ByteBuffer headerBuf = ByteBuffer.wrap(bmpHeader).order(ByteOrder.LITTLE_ENDIAN);
        headerBuf.putInt(2, fileSize);
        headerBuf.putInt(10, pixelDataOffset);

        byte[] completeBMP = new byte[fileSize];
        System.arraycopy(bmpHeader, 0, completeBMP, 0, 14);
        System.arraycopy(dibData, 0, completeBMP, 14, dibData.length);

        return completeBMP;
    }

    /**
     * pdf 썸네일
     */
    private byte[] extractPdfThumbnail(Path pdfPath) {
        long start = System.nanoTime();

        try (PDDocument document = Loader.loadPDF(pdfPath.toFile());
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDFRenderer renderer = new PDFRenderer(document);

            // scale 기반 렌더링
            BufferedImage image = renderer.renderImage(0, 0.5f);
            if (image == null) return new byte[0];

            // 리사이즈
            int targetHeight = (int) (image.getHeight() * ((double) 320 / image.getWidth()));
            Image scaled = image.getScaledInstance(320, targetHeight, Image.SCALE_FAST);

            BufferedImage thumbnail = new BufferedImage(320, targetHeight, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(scaled, 0, 0, null);
            g2d.dispose();

            ImageIO.write(thumbnail, "jpeg", out);

            long elapsed = System.nanoTime() - start;
            log.debug("PDF 썸네일 생성 완료 ({} ms): {}", elapsed / 1_000_000, pdfPath.getFileName());

            return out.toByteArray();

        } catch (Exception e) {
            log.error("PDF 썸네일 추출 실패: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * 파일속성 조회
     */
    public PropRes getFileProperties(String fileAttCd, String menuType) {

        if ("CAB".equals(menuType)) {
            FilePropDvo propDvo = this.fileMapper.selectFileProperties(fileAttCd);
            if (propDvo == null) {
                // 파일 정보를 찾을 수 없습니다.
                throw new PlatformException(PlatformConstant.COMMON_FILE_INFO_NOT_EXIST);
            }
            String fileGrp = propDvo.getFileGrp();
            String menuPath = this.fileGrpService.getMenuPathByFileGrp(fileGrp);

            List<CommunityAuthDto.AuthRes> auths = null;
            // 커뮤니티 파일 권한 체크
            if ("DEPT".equals(fileGrp) || "GROUP".equals(fileGrp)) {
                auths = this.commuAuthService.getCommunityAuthorizations(fileAttCd);
            }

            return this.converter.dataToPropRes(propDvo, menuPath, auths);
        }

        return null;
    }

    /**
     * 스트리밍 가능 동영상 체크
     */
    public boolean getVideoStreamable(String fileAttCd) throws IOException {

        FileDownloadDvo dvo = this.getFileInfoForDownload(fileAttCd);

        String fileNm = dvo.getFileNm();
        String filePhyPath = dvo.getFilePhyPath();
        String filePhyNm = dvo.getFilePhyNm();
        String fileGrp = dvo.getFileGrp();
        String fileRefCd = dvo.getFileRefCd();

        Path target = Paths.get(this.rootDir, filePhyPath, filePhyNm);
        if (Files.notExists(target)) {
            // 파일 메시지 처리
            this.handleMissingFileByGroup(fileGrp, fileRefCd, fileNm);
        }

        File file = target.toFile();
        Tika tika = new Tika();
        String contentType = tika.detect(file).toLowerCase();

        if (contentType.contains("mp4")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                IsoFile isoFile = new IsoFile(fis.getChannel());

                // moov가 mdat보다 먼저 나오면 스트리밍 가능
                for (Box box : isoFile.getBoxes()) {
                    String type = box.getType();
                    if ("moov".equals(type)) {
                        return true;  // 스트리밍 가능
                    } else if ("mdat".equals(type)) {
                        return false; // 스트리밍 불가능
                    }
                }
            }
        } else if (contentType.contains("ogg")) {

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] header = fis.readNBytes(64);
                if (header.length < 4) return false;

                // "OggS" 매직 넘버
                if (!(header[0] == 'O' && header[1] == 'g' && header[2] == 'g' && header[3] == 'S')) {
                    return false;
                }

                String hdr = new String(header);
                return hdr.contains("vorbis") || hdr.contains("OpusHead") || hdr.contains("theora");
            }

        } else if (contentType.contains("webm")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = fis.readNBytes(4096); // 앞부분만 검사
                String hex = this.bytesToHex(buffer);

                if (!hex.contains("1A45DFA3")) return false; // EBML 헤더 없음
                int cuesPos = hex.indexOf("1C53BB6B");
                return (cuesPos > 0 && cuesPos < 2048);
            }
        }

        return true;
    }

    /**
     * 파일 메시지 처리
     */
    public void handleMissingFileByGroup(String fileGrp, String fileRefCd, String fileNm) {
        // folder 경로 찾기
        if (fileGrp.startsWith("PROJECT")) {
            // 파일 정보를 찾을 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_FILE_INFO_NOT_EXIST);
        } else {
            String menuPath = this.fileGrpService.getMenuPathByFileGrp(fileGrp);
            String path = this.fileMapper.selectFolderPathByFile(fileRefCd);
            // 파일이 존재하지 않습니다.
            throw new PlatformException(PlatformConstant.COMMON_FILE_NOT_EXIST, menuPath + " > " + path + " > " + fileNm);
        }
    }

    /**
     * byte array convert hex
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    /**
     * 동영상 스트리밍
     */
    public void getVideoFileStream(String fileAttCd, HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            FileDownloadDvo dvo = this.getFileInfoForDownload(fileAttCd);

            Path target = Paths.get(this.rootDir, dvo.getFilePhyPath(), dvo.getFilePhyNm());
            if (Files.notExists(target)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Tika tika = new Tika();
            String contentType = tika.detect(target).toLowerCase();

            // 동영상 파일이 아닌경우 skip
            if (!contentType.contains("mp4") && !contentType.contains("ogg") && !contentType.contains("webm")) return;

            response.setContentType(contentType);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Connection", "close");

            long fileSize = Files.size(target);
            long[] range = this.parseRangeHeader(request.getHeader("Range"), fileSize);
            long start = range[0];
            long end = range[1];
            long contentLength = end - start + 1;

            if (request.getHeader("Range") != null) {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            }
            response.setHeader("Content-Length", String.valueOf(contentLength));

            final int BUFFER_SIZE = 16 * 1024;
            try (java.nio.channels.SeekableByteChannel channel = Files.newByteChannel(target, StandardOpenOption.READ);
                 OutputStream out = response.getOutputStream()) {

                channel.position(start);
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                long remaining = contentLength;

                while (remaining > 0 && channel.read(buffer) != -1) {
                    buffer.flip();
                    int bytesToWrite = (int) Math.min(buffer.remaining(), remaining);
                    out.write(buffer.array(), buffer.position(), bytesToWrite);
                    remaining -= bytesToWrite;
                    buffer.clear();
                }

                out.flush();
            }

        } catch (PlatformException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (AsyncRequestNotUsableException e) {
            log.info("스트리밍 경고 : {}", e.getMessage());
        }
    }

    /**
     * range 처리
     */
    private long[] parseRangeHeader(String rangeHeader, long fileLength) {
        Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(rangeHeader);
        if (matcher.find()) {
            long start = Long.parseLong(matcher.group(1));
            long end = matcher.group(2).isEmpty() ? fileLength - 1 : Long.parseLong(matcher.group(2));
            return new long[]{start, end};
        }
        return new long[]{0, fileLength - 1};
    }

    /**
     * 파일 조회 (그룹웨어 발송용)
     */
    public List<GroupwareMailFileDvo> getFilesForGwMail(List<String> fileCds) {
        if (CollectionUtils.isEmpty(fileCds)) return null;

        return this.fileMapper.selectFilesForGwMail(fileCds);
    }

    /**
     * 파일버전 리스트 조회 (버전관리용)
     */
    public List<VersionRes> getFileVersionsForMng(String fileAttCd) {
        return this.fileMapper.selectFileVersionsForMng(fileAttCd);
    }

    /**
     * 파일정보 조회
     */
    public FileInfoRes getFileInfoForAgent(String fileAttCd) {
        return this.fileMapper.selectFileInfoForAgent(fileAttCd);
    }

}
