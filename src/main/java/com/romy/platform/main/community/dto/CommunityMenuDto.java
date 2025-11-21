package com.romy.platform.main.community.dto;


import io.swagger.v3.oas.annotations.media.Schema;




public class CommunityMenuDto {


    @Schema(name = "CommunityMenuDto.SearchRes", description = "커뮤니티 메뉴 조회")
    public record SearchRes(
            @Schema(description = "메뉴코드")
            String menuCd,
            @Schema(description = "메뉴명")
            String menuNm,
            @Schema(description = "폴더여부", allowableValues = {"Y", "N"})
            String folderYn,
            @Schema(description = "폴더그룹코드")
            String folderGrp,
            @Schema(description = "활성화여부")
            boolean enable
    ) {}
    
}
