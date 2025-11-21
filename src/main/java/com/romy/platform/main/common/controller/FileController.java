package com.romy.platform.main.common.controller;

import static com.romy.platform.main.common.dto.FileDto.*;

import com.romy.platform.common.constants.PlatformConstant;

import com.romy.platform.main.common.converter.FileConverter;

import com.romy.platform.main.common.dvo.FileDvo;
import com.romy.platform.main.common.dvo.FileInfoDvo;
import com.romy.platform.main.common.service.FileService;
import com.romy.platform.main.community.service.CommunityAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "파일 관련")
@RequestMapping("/common/file")
public class FileController {

    private final FileService fileService;
    private final FileConverter converter;

    private final CommunityAuthService commuAuthService;


    @Operation(summary = "파일 업로드")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadRes> createFileUpload(@Parameter(description = "파일 리스트", required = true) @RequestPart("files") List<MultipartFile> files
            , @ParameterObject UploadReq dto) throws IOException {

        List<FileInfoDvo> dvos = this.fileService.createFileUpload(files, dto);

        return this.converter.fileInfoDvoToUploadResList(dvos);
    }

    @Operation(summary = "파일 버전 업로드")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    @PostMapping(value = "/version-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VersionUploadRes createFileUploadForVersion(@Parameter(description = "파일", required = true) @RequestPart("file") MultipartFile file
            , @ParameterObject VersionUploadReq dto) throws IOException {

        FileDvo dvo = this.fileService.createFileUploadForVersion(file, dto);

        return this.converter.fileDvoToVersionUploadRes(dvo);
    }

    @DeleteMapping("/{fileAttCd}")
    @Operation(summary = "파일삭제 (단건)")
    public int removeFile(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd) {
        return this.fileService.removeFile(fileAttCd);
    }

    @DeleteMapping("/multiple")
    @Operation(summary = "파일삭제 (다건)")
    public int removeFiles(@Parameter(description = "파일첨부코드 리스트") @RequestBody @NotEmpty List<String> fileAttCds) {
        return this.fileService.removeFiles(fileAttCds);
    }

    @Operation(summary = "파일 다운로드")
    @GetMapping("/{fileAttCd}/download")
    public void getFileDownload(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd
            , HttpServletRequest request, HttpServletResponse response) {

        this.fileService.getFileDownload(fileAttCd, request, response);
    }

    @GetMapping("/items")
    @Operation(summary = "하위 폴더/파일 리스트 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<SearchRes> getFolderFilesByFolderCd(@Valid @ParameterObject SearchReq dto) {

        // 권한체크
        this.commuAuthService.checkCommuFolderFileAuth(dto.folderGrp(), null, dto.folderCd());

        // 파일명 중복 업데이트
        this.fileService.updateDupFileName(dto);

        return this.fileService.getFolderFilesByFolderCd(dto);
    }

    @GetMapping("/{fileAttCd}/thumbnail")
    @Operation(summary = "파일 썸네일 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE))
    public byte[] getFileThumbnail(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd) throws IOException {
        return this.fileService.getFileThumbnail(fileAttCd);
    }

    @GetMapping("{fileAttCd}/properties")
    @Operation(summary = "파일 속성 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public PropRes getFileProperties(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd
            , @Parameter(description = "메뉴타입", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PRJ", "CAB"}))
                                      @RequestParam(name = "menuType") String menuType) {

        return this.fileService.getFileProperties(fileAttCd, menuType);
    }

    @GetMapping("/{fileAttCd}/streamable")
    @Operation(summary = "동영상 스트리밍 가능여부 체크")
    public String getVideoStreamable(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd) throws IOException {
        boolean isStreamable = this.fileService.getVideoStreamable(fileAttCd);

        return isStreamable ? PlatformConstant.YN_Y : PlatformConstant.YN_N;
    }

    @GetMapping("/stream/{fileAttCd}")
    @Operation(summary = "동영상 스트리밍")
    public void getVideoFileStream(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd
            , HttpServletRequest request, HttpServletResponse response) throws IOException {

        this.fileService.getVideoFileStream(fileAttCd, request, response);
    }

    @GetMapping("/{fileAttCd}/versions")
    @Operation(summary = "파일 버전 리스트 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<VersionRes> getFileVersionsForMng(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd) {
        return this.fileService.getFileVersionsForMng(fileAttCd);
    }

    @GetMapping("/{fileAttCd}")
    @Operation(summary = "파일 정보 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public FileInfoRes getFileInfoForAgent(@PathVariable @Parameter(description = "파일첨부코드") String fileAttCd) {
        return this.fileService.getFileInfoForAgent(fileAttCd);
    }



}
