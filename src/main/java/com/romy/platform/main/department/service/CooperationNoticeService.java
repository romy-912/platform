package com.romy.platform.main.department.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.common.dvo.FileCopyDvo;
import com.romy.platform.main.common.dvo.FileDownloadDvo;
import com.romy.platform.main.common.dvo.FileInfoDvo;
import com.romy.platform.main.common.service.FileService;
import com.romy.platform.main.common.service.IdGenerateService;
import com.romy.platform.main.department.converter.CooperationNoticeConverter;
import com.romy.platform.main.department.mapper.CooperationNoticeMapper;
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

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CooperationNoticeService {

    @Value("${file.server-root-file-dir}")
    private String rootDir;

    @Value("${file.virtual-save-path}")
    private String savePath;

    private final CooperationNoticeMapper mapper;
    private final CooperationNoticeConverter converter;

    private final FileService fileService;
    private final IdGenerateService idGenService;



    /**
     * 부서운영현황 공지사항 삭제
     */
    @PlatformTransactional
    public void removeCoopNoticeByBrdCd(String brdCd) {

        String noticeCd = this.mapper.selectCoopNoticeCode(brdCd);
        if (StringUtils.isNotEmpty(noticeCd)) {
            this.mapper.deleteCoopNoticeByBrdCd(brdCd);
            // 첨부파일 삭제
            this.fileService.removeFilesByFileRefCd("DOC_NOTICE", noticeCd);
        }
    }

    /**
     * 부서운영현황 공지사항 저장
     */
    @PlatformTransactional
    public void saveCoopNoticeForCommuNotice(String brdCd, List<FileInfoDvo> files) throws IOException {
        String noticeCd = this.mapper.selectCoopNoticeCode(brdCd);
        if (StringUtils.isEmpty(noticeCd)) {
            noticeCd = this.idGenService.getNextStringId();
        } else {
            // 첨부파일 삭제
            this.fileService.removeFilesByFileRefCd("DOC_NOTICE", noticeCd);
        }

        this.mapper.mergeCoopNoticeForCommuNotice(noticeCd, brdCd);

        // 첨부파일 복사
        if (CollectionUtils.isNotEmpty(files)) {
            List<Path> createdFiles = new ArrayList<>();

            String newFilePhyPath = this.savePath + PlatformUtil.getPhyPath();

            try {
                for (FileInfoDvo file : files) {
                    String fileAttCd = file.getFileAttCd();
                    FileDownloadDvo downDvo = this.fileService.getFileInfoForDownload(fileAttCd);
                    String filePhyPath = downDvo.getFilePhyPath();
                    String filePhyNm = downDvo.getFilePhyNm();

                    Path source = Paths.get(this.rootDir, filePhyPath, filePhyNm);
                    if (Files.notExists(source)) {
                        // 파일 정보를 찾을 수 없습니다.
                        throw new PlatformException(PlatformConstant.COMMON_FILE_INFO_NOT_EXIST);
                    }

                    String newFileAttCd = this.idGenService.getNextStringId();
                    String fileExt = file.getFileExt();
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

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }

                    createdFiles.add(target);

                    // 파일 데이터 생성
                    FileCopyDvo dvo = this.converter.dataToFileCopyDvo(fileAttCd, newFileAttCd, newFilePhyPath
                            , "DOC_NOTICE", noticeCd);
                    this.fileService.createFileByCopy(dvo);
                }
            } catch (Exception e) {
                // 오류 발생 시 파일 삭제
                for (Path path : createdFiles) {
                    Files.deleteIfExists(path);
                }
                throw e;
            }
        }

    }



}
