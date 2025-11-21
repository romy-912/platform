package com.romy.platform.main.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

import static com.romy.platform.main.community.dto.CommunityAuthDto.AuthRes;


public class FolderDto {

    @Schema(name = "FolderDto.CreateDownReq", description = "다운로드 이력생성 조건")
    public record CreateDownReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderGrp,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더 다운로드 그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folDownGrp,
            @Schema(description = "파일코드 리스트")
            List<String> fileCds,
            @Schema(description = "폴더코드 리스트")
            List<String> folderCds
    ) {}

    @Schema(name = "FolderDto.CreateUpReq", description = "업로드 이력생성 조건")
    public record CreateUpReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderGrp,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더 업로드 그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folUpGrp
    ) {}

    @Schema(name = "FolderDto.UpdateHistReq", description = "업/다운로드 결과 업데이트")
    public record UpdateHistReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderGrp,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더 업/다운로드 그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folGrp,
            @NotBlank
            @Size(max = 1)
            @Schema(description = "업/다운로드 유형", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"U", "D"})
            String udType,
            @NotBlank
            @Size(max = 1)
            @Schema(description = "결과 유형", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"C", "O", "E"})
            String endType
    ) {}

    @Schema(name = "FolderDto.TreeAllRes", description = "폴더 전체 트리구조")
    public record TreeAllRes(
            @Schema(description = "폴더코드")
            String folderCd,
            @Schema(description = "폴더명")
            String folderNm,
            @Schema(description = "부모폴더코드")
            String parentCd,
            @Schema(description = "부모폴더명")
            String parentNm,
            @Schema(description = "레벨")
            int level,
            @Schema(description = "보안여부")
            String foldSecurityYn,
            @Schema(description = "폴더 소유자 코드")
            String ownerUsrCd,
            @Schema(description = "폴더 소유자")
            String ownerUsrNm,
            @Schema(description = "폴더 사이즈")
            long folderSize
    ) {}

    @Schema(name = "FolderDto.CreateReq", description = "하위 폴더 생성")
    public record CreateReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "부모폴더코드")
            String parentCd,
            @NotBlank
            @Size(max = 250)
            @Schema(description = "폴더명")
            String folderNm,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드")
            String folderGrp,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "부문/사용자코드")
            String sectorCd
    ) {}

    @Schema(name = "FolderDto.CreateRes", description = "폴더 생성 결과")
    public record CreateRes(
            @Schema(description = "폴더코드")
            String folderCd
    ) {}

    @Schema(name = "FolderDto.ValidReq", description = "폴더 유효성 체크")
    public record ValidReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderGrp,
            @NotBlank
            @Schema(description = "행위", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"DEL", "COPY", "CUT"})
            String action,
            @Schema(description = "파일코드 리스트")
            List<String> fileCds,
            @Schema(description = "폴더코드 리스트")
            List<String> folderCds
    ) {}

    @Schema(name = "FolderDto.RemoveReq", description = "폴더 삭제")
    public record RemoveReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderGrp,
            @Schema(description = "파일코드 리스트")
            List<String> fileCds,
            @Schema(description = "폴더코드 리스트")
            List<String> folderCds
    ) {}

    @Schema(name = "FolderDto.ClipboardReq", description = "복사-붙여넣기, 잘라내기-붙여넣기 조건")
    public record ClipboardReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "target 폴더코드")
            String folderCd,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드")
            String folderGrp,
            @Size(max = 50)
            @Schema(description = "target 프로젝트 코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String prjCd,
            @Size(max = 30)
            @Schema(description = "target 공정 코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String procCd,
            @Schema(description = "파일코드 리스트")
            List<String> fileCds,
            @Schema(description = "폴더코드 리스트")
            List<String> folderCds
    ) {}

    @Schema(name = "FolderDto.CopyCutReq", description = "복사, 붙여넣기 Request")
    public record CopyCutReq(
            @NotEmpty
            @Schema(description = "복사 개체대상 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
            List<CopyTemp> datas
    ) {}

    @Schema(name = "FolderDto.CopyTemp", description = "복사, 붙여넣기 개체대상")
    public record CopyTemp(
            @NotBlank
            @Schema(description = "파일/폴더 구분", allowableValues = {"FOLDER", "FILE"}, requiredMode = Schema.RequiredMode.REQUIRED)
            String dataType,
            @Schema(description = "파일코드")
            String fileAttCd,
            @Schema(description = "폴더코드")
            String folderCd,
            @NotBlank
            @Schema(description = "파일그룹", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            String fileGrp,
            @Schema(description = "프로젝트코드")
            String prjCd,
            @Schema(description = "프로젝트공정코드")
            String prjProcCd,
            @Schema(description = "공정코드")
            String procCd,
            @NotBlank
            @Schema(description = "부모폴더코드", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            String parentFolderCd
    ) {}

    @Schema(name = "FolderDto.PasteReq", description = "붙여넣기 조건")
    public record PasteReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "target 폴더코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderCd,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "폴더그룹코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String folderGrp,
            @Size(max = 50)
            @Schema(description = "target 프로젝트 코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String prjCd,
            @Size(max = 30)
            @Schema(description = "target 공정 코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String procCd
    ) {}

    @Schema(name = "FolderDto.PropRes", description = "폴더 속성")
    public record PropRes(
            @Schema(description = "폴더코드")
            String folderCd,
            @Schema(description = "폴더명")
            String folderNm,
            @Schema(description = "폴더그룹코드")
            String folderGrp,
            @Schema(description = "부모폴더코드")
            String parentCd,
            @Schema(description = "부문/사용자코드")
            String sectorCd,
            @Schema(description = "메뉴경로")
            String menuPath,
            @Schema(description = "폴더경로")
            String folderPath,
            @Schema(description = "폴더 사이즈")
            long fileSize,
            @Schema(description = "등록일시")
            String regDtt,
            @Schema(description = "수정일시")
            String modDtt,
            @Schema(description = "권한 리스트")
            List<AuthRes> auths
    ) {}

}
