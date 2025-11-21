package com.romy.platform.main.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public class AuthLoginDto {

    @Schema(name = "AuthLoginDto.TokenRes", description = "Token 정보")
    public record TokenRes(
            @Schema(description = "Access Token")
            String accessToken,
            @Schema(description = "Refresh Token")
            String refreshToken
    ){}

    @Schema(name = "AuthLoginDto.TokenReq", description = "토근 발급 Request")
    public record TokenReq(
            @NotBlank
            @Schema(description = "사번", requiredMode = Schema.RequiredMode.REQUIRED)
            String usrId
    ) {}

    @Schema(name = "AuthLoginDto.LogoutReq", description = "로그아웃 Request")
    public record LogoutReq(
            @NotBlank
            @Schema(description = "사용자코드")
            String usrCd,
            @NotBlank
            @Schema(description = "사번")
            String usrId
    ) {}

    @Schema(name = "AuthLoginDto.MenuRes", description = "메뉴정보 조회")
    public record MenuRes(
            @Schema(description = "메뉴코드")
            String menuCd,
            @Schema(description = "메뉴명")
            String menuNm,
            @Schema(description = "부모메뉴코드")
            String parentCd,
            @Schema(description = "메뉴경로")
            String menuUrl,
            @Schema(description = "새탭 오픈여부", allowableValues = {"", "Y", "N"})
            String openYn
    ) {}

}
