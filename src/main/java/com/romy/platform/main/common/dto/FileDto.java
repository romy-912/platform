package com.romy.platform.main.common.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;

import static com.romy.platform.main.community.dto.CommunityAuthDto.AuthRes;


public class FileDto {

    @Schema(name = "FileDto.UploadReq", description = "파일 업로드 조건")
    public record UploadReq(
            @NotBlank
            @Size(max = 20)
            @RequestPart("fileGrp")
            @Parameter(description = "파일그룹", required = true)
            String fileGrp,
            @Size(max = 40)
            @RequestPart(value = "fileRefCd", required = false)
            @Parameter(description = "파일참조코드")
            String fileRefCd,
            @Size(max = 50)
            @RequestPart(value = "prjCd", required = false)
            @Parameter(description = "프로젝트코드")
            String prjCd,
            @Size(max = 30)
            @RequestPart(value = "procCd", required = false)
            @Parameter(description = "공정코드")
            String procCd,
            @Size(max = 20)
            @RequestPart(value = "folderCd", required = false)
            @Parameter(description = "폴더코드 (프로젝트 용)")
            String folderCd
    ) {}

    @Schema(name = "FileDto.VersionUploadReq", description = "파일 버전 업로드 조건")
    public record VersionUploadReq(
            @NotBlank
            @Size(max = 20)
            @RequestPart("beforeFileAttCd")
            @Parameter(description = "이전 버전 파일첨부코드", required = true)
            String beforeFileAttCd,
            @NotBlank
            @Size(max = 20)
            @RequestPart("fileGrp")
            @Parameter(description = "파일그룹", required = true)
            String fileGrp,
            @NotBlank
            @Size(max = 40)
            @RequestPart(value = "fileRefCd")
            @Parameter(description = "파일참조코드", required = true)
            String fileRefCd,
            @Size(max = 20)
            @RequestPart(value = "fileVerGrpCd", required = false)
            @Parameter(description = "파일 버전그룹 코드")
            String fileVerGrpCd,
            @Size(max = 50)
            @RequestPart(value = "prjCd", required = false)
            @Parameter(description = "프로젝트코드")
            String prjCd,
            @Size(max = 30)
            @RequestPart(value = "procCd", required = false)
            @Parameter(description = "공정코드")
            String procCd,
            @Size(max = 20)
            @RequestPart(value = "folderCd", required = false)
            @Parameter(description = "폴더코드 (프로젝트 용)")
            String folderCd
    ) {}

    @Schema(name = "FileDto.UploadRes", description = "파일 업로드 결과")
    public record UploadRes(
            @Schema(description = "파일첨부코드")
            String fileAttCd,
            @Schema(description = "파일명")
            String fileNm,
            @Schema(description = "확장자")
            String fileExt,
            @Schema(description = "사이즈")
            Long fileSize
    ) {}

    @Schema(name = "FileDto.VersionUploadRes", description = "파일 업로드 결과")
    public record VersionUploadRes(
            @Schema(description = "파일첨부코드")
            String fileAttCd,
            @Schema(description = "파일명")
            String fileNm,
            @Schema(description = "확장자")
            String fileExt,
            @Schema(description = "사이즈")
            Long fileSize,
            @Schema(description = "파일 버전그룹코드")
            String fileVerGrpCd
    ) {}

    @Schema(name = "FileDto.SearchReq", description = "파일 목록 조회조건")
    public record SearchReq(
            @NotBlank
            @Parameter(description = "폴더그룹코드")
            String folderGrp,
            @NotBlank
            @Parameter(description = "폴더코드")
            String folderCd,
            @Parameter(description = "조건 구분", schema = @Schema(allowableValues = {"FILE_NM", "USR_NM"}))
            String searchCond,
            @Parameter(description = "검색어")
            String searchKeyword
    ) {}

    @Schema(name = "FileDto.SearchRes", description = "파일 목록 조회 결과")
    public record SearchRes(
            @Schema(description = "폴더여부", allowableValues = {"Y", "N"})
            String folderYn,
            @Schema(description = "파일명/폴더명")
            String fileNm,
            @Schema(description = "파일코드/폴더코드")
            String fileAttCd,
            @Schema(description = "파일확장자")
            String fileExt,
            @Schema(description = "파일사이즈")
            Long fileSize,
            @Schema(description = "파일그룹코드")
            String fileGrp,
            @Schema(description = "파일참조코드 (폴더의 경우 부모폴더코드)")
            String fileRefCd,
            @Schema(description = "파일그룹명 (Agent 편집용)")
            String fileGrpNm,
            @Schema(description = "폴더경로")
            String folderPath,
            @Schema(description = "편집구분", allowableValues = {"A", "W", ""})
            String lockDiv,
            @Schema(description = "보안여부", allowableValues = {"Y", "N"})
            String securityYn,
            @Schema(description = "등록시간")
            String regDtt,
            @Schema(description = "등록자코드")
            String regUsrCd,
            @Schema(description = "등록자명")
            String regUsrNm,
            @Schema(description = "최종수정시간")
            String modDtt,
            @Schema(description = "최종수정자코드")
            String modUsrCd,
            @Schema(description = "최종수정자명")
            String modUsrNm
    ) {}

    @Schema(name = "FileDto.PropRes", description = "파일 속성")
    public record PropRes(
            @Schema(description = "파일첨부코드")
            String fileAttCd,
            @Schema(description = "파일명")
            String fileNm,
            @Schema(description = "파일그룹")
            String fileGrp,
            @Schema(description = "파일 확장자")
            String fileExt,
            @Schema(description = "폴더코드")
            String folderCd,
            @Schema(description = "부문/사용자코드")
            String sectorCd,
            @Schema(description = "메뉴경로")
            String menuPath,
            @Schema(description = "폴더경로")
            String folderPath,
            @Schema(description = "파일 사이즈")
            Long fileSize,
            @Schema(description = "등록일시")
            String regDtt,
            @Schema(description = "수정일시")
            String modDtt,
            @Schema(description = "등록자명")
            String regUsrNm,
            @Schema(description = "읽기전용", allowableValues = {"Y", "N"})
            String readonlyYn,
            @Schema(description = "보안여부", allowableValues = {"Y", "N"})
            String securityYn,
            @Schema(description = "편집여부", allowableValues = {"Y", "N"})
            String editYn,
            @Schema(description = "권한 리스트")
            List<AuthRes> auths
    ) {}

    @Schema(name = "FileDto.VersionRes", description = "파일 버전 조회")
    public record VersionRes(
            @Schema(description = "파일첨부코드")
            String fileAttCd,
            @Schema(description = "파일명")
            String fileNm,
            @Schema(description = "파일확장자")
            String fileExt,
            @Schema(description = "파일그룹")
            String fileGrp,
            @Schema(description = "파일참조코드")
            String fileRefCd,
            @Schema(description = "파일버전그룹코드")
            String fileVerGrpCd,
            @Schema(description = "파일 라벨/메모")
            String fileLabel,
            @Schema(description = "수정일시")
            String modDtt,
            @Schema(description = "등록자코드")
            String regUsrCd,
            @Schema(description = "등록자명")
            String regUsrNm
    ) {}

    @Schema(name = "FileDto.FileInfoRes", description = "파일 정보 조회")
    public record FileInfoRes(
            @Schema(description = "파일첨부코드")
            String fileAttCd,
            @Schema(description = "파일명")
            String fileNm,
            @Schema(description = "파일확장자")
            String fileExt,
            @Schema(description = "파일사이즈")
            long fileSize,
            @Schema(description = "파일그룹코드")
            String fileGrp,
            @Schema(description = "파일참조코드")
            String fileRefCd,
            @Schema(description = "파일그룹명")
            String fileGrpNm
    ) {}


}
