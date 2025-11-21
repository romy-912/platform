package com.romy.platform.main.auth.controller;

import com.romy.platform.common.provider.RedisProvider;
import static com.romy.platform.main.auth.dto.AuthLoginDto.*;
import com.romy.platform.main.auth.service.AuthLoginService;
import com.romy.platform.main.auth.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "로그인 관련")
@RequestMapping("/auth")
public class AuthLoginController {

    private final AuthLoginService authLoginService;
    private final MenuService menuService;


    @GetMapping("/check")
    @Operation(summary = "세션 체크")
    public boolean checkLogin(@Parameter(description = "사번") @RequestParam(name = "usrId") String usrId) {
        String jSessionId = RedisProvider.getRedisStringValue("spring:session:" + usrId);

        return StringUtils.isNotBlank(jSessionId);
    }

    @PostMapping("/token")
    @Operation(summary = "토큰 발급")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public TokenRes createAuthToken(@RequestBody @Valid TokenReq dto) {
        return this.authLoginService.createAuthToken(dto.usrId());
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public void logout(@RequestBody @Valid LogoutReq dto) {
        this.authLoginService.updateLoginHistoryByLogout(dto);
    }

    
    @GetMapping("/menus")
    @Operation(summary = "메뉴 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<MenuRes> getRoleMenus() {
        return this.menuService.getRoleMenus();
    }


}
