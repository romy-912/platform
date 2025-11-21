package com.romy.platform.main.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;


public class CompressDto {

    @Schema(name = "CompressDto.CompressReq", description = "압축하기 조건")
    public record CompressReq(
            @NotBlank
            @Schema(description = "압축파일명")
            String compressFileNm,
            @NotBlank
            @Schema(description = "폴더 코드")
            String folderCd,
            @NotBlank
            @Schema(description = "프로젝트 폴더 여부", allowableValues = {"Y", "N"})
            String prjFolderYn,
            @Schema(description = "압축 대상 파일 리스트")
            List<TargetFile> files
    ) {}

    @Schema(name = "CompressDto.TargetFile", description = "압축 대상 파일정보")
    public record TargetFile(
            @NotBlank
            @Schema(description = "폴더여부", allowableValues = {"Y", "N"})
            String folderYn,
            @NotBlank
            @Schema(description = "파일명/폴더명")
            String fileNm,
            @NotBlank
            @Schema(description = "파일코드/폴더코드")
            String fileAttCd,
            @NotBlank
            @Schema(description = "파일그룹코드")
            String fileGrp,
            @Schema(description = "프로젝트 코드")
            String prjCd,
            @Schema(description = "공정코드")
            String procCd
    ) {}

    @Schema(name = "CompressDto.DecompressReq", description = "압축 해제 파일정보")
    public record DecompressReq(
            @NotBlank
            @Schema(description = "압축 파일코드")
            String fileAttCd,
            @NotBlank
            @Schema(description = "폴더 코드")
            String folderCd,
            @NotBlank
            @Schema(description = "프로젝트 폴더 여부", allowableValues = {"Y", "N"})
            String prjFolderYn
    ) {}

}
