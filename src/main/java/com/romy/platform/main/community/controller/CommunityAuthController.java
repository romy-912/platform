package com.romy.platform.main.community.controller;

import static com.romy.platform.main.community.dto.CommunityAuthDto.*;
import com.romy.platform.main.community.service.CommunityAuthService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 권한")
@RequestMapping("/community/auth")
public class CommunityAuthController {

    private final CommunityAuthService commuAuthService;

    @DeleteMapping
    @Operation(summary = "커뮤니티 권한 삭제")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public List<AuthRes> removeCommunityAuth(@Valid @ParameterObject RemoveReq dto) {

        return this.commuAuthService.removeCommunityAuth(dto);
    }

    @GetMapping("/{folderFileCd}")
    @Operation(summary = "커뮤니티 권한 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public List<AuthRes> getCommunityAuthorizations(@PathVariable @Parameter(description = "폴더/파일코드") String folderFileCd) {
        return this.commuAuthService.getCommunityAuthorizations(folderFileCd);
    }

    @PostMapping("/{folderFileCd}")
    @Operation(summary = "커뮤니티 권한 등록")
    public int createCommunityAuth(@PathVariable @Parameter(description = "폴더/파일코드") String folderFileCd
            , @Valid @RequestBody CreateReq dto) {

        return this.commuAuthService.createCommunityAuth(folderFileCd, dto);
    }

}
