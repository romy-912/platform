package com.romy.platform.main.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;



public class CommunityAuthDto {

    @Schema(name = "CommunityAuthDto.AuthRes", description = "커뮤니티 폴더/파일 권한")
    public record AuthRes(
            @Schema(description = "폴더/파일코드")
            String folderFileCd,
            @Schema(description = "권한코드")
            String authCd,
            @Schema(description = "권한명")
            String authNm,
            @Schema(description = "권한구분", allowableValues = {"DEPT", "GROUP", "USER"})
            String authDiv,
            @Schema(description = "권한구분명")
            String authDivNm
    ) {}

    @Schema(name = "CommunityAuthDto.RemoveReq", description = "커뮤니티 권한 삭제 조건")
    public record RemoveReq(
            @NotBlank
            @Schema(description = "폴더여부", allowableValues = {"Y", "N"})
            String folderYn,
            @NotBlank
            @Schema(description = "폴더/파일코드")
            String folderFileCd,
            @NotBlank
            @Schema(description = "권한코드")
            String authCd
    ) {}

    @Schema(name = "CommunityAuthDto.CreateReq", description = "커뮤니티 권한 등록 조건")
    public record CreateReq(
            @NotBlank
            @Schema(description = "폴더여부", allowableValues = {"Y", "N"})
            String folderYn,
            @NotEmpty
            @Schema(description = "권한리스트")
            List<CommuAuth> auths
    ) {}

    @Schema(name = "CommunityAuthDto.CommuAuth", description = "커뮤니티 권한 정보")
    public record CommuAuth(
            @NotBlank
            @Schema(description = "권한구분", allowableValues = {"DEPT", "USER", "GROUP"})
            String authDiv,
            @NotBlank
            @Schema(description = "권한코드 (부서코드, 사용자코드, 그룹코드)")
            String authCd
    ) {}

}
