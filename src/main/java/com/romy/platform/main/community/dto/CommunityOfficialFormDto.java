package com.romy.platform.main.community.dto;


import io.swagger.v3.oas.annotations.media.Schema;


public class CommunityOfficialFormDto {

    @Schema(name = "CommunityOfficialFormDto.CategoryRes", description = "커뮤니티 행정양식 카테고리")
    public record CategoryRes(
            @Schema(description = "행정양식 카테고리 코드")
            String ctgCd,
            @Schema(description = "행정양식 카테고리 명")
            String ctgNm
    ) {}


    @Schema(name = "CommunityOfficialFormDto.SearchRes", description = "커뮤니티 행정양식 조회")
    public record SearchRes(
            @Schema(description = "데이터유형", allowableValues = {"FILE"})
            String dataType,
            @Schema(description = "파일첨부코드")
            String fileAttCd,
            @Schema(description = "파일명")
            String fileNm,
            @Schema(description = "파일사이즈")
            Long fileSize,
            @Schema(description = "파일확장자")
            String fileExt,
            @Schema(description = "파일그룹")
            String fileGrp,
            @Schema(description = "수정일시")
            String modDtt,
            @Schema(description = "가이드")
            String helpCont,
            @Schema(description = "카테고리명")
            String ctgNm
    ) {}

}
