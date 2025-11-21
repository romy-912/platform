package com.romy.platform.main.community.controller;

import static com.romy.platform.main.community.dto.CommunityMenuDto.*;

import com.romy.platform.main.community.service.CommunityMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 메뉴")
@RequestMapping("/community/menus")
public class CommunityMenuController {

    private final CommunityMenuService commuMenuService;


    @GetMapping
    @Operation(summary = "커뮤니티 메뉴 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<SearchRes> getCommunityMenus(@Parameter(description = "메뉴구분", required = true
            , schema = @Schema(allowableValues = {"DEPT", "GROUP"})) @RequestParam(name = "menuDiv") String menuDiv) {

        return this.commuMenuService.getCommunityMeus(menuDiv);
    }

    @GetMapping("/{menuCd}/permission")
    @Operation(summary = "커뮤니티 폴더 권한 조회")
    public String getCommunityFolderPermission(@PathVariable @Parameter(description = "메뉴코드") String menuCd
            , @Parameter(description = "폴더그룹코드", required = true) @RequestParam(name = "folderGrp") String folderGrp) {

        return this.commuMenuService.getCommunityFolderPermission(menuCd, folderGrp);
    }

}
