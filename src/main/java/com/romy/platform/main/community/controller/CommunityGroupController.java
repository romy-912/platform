package com.romy.platform.main.community.controller;


import static com.romy.platform.main.community.dto.CommunityGroupDto.*;


import com.romy.platform.main.community.service.CommunityGroupService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 그룹")
@RequestMapping("/community/groups")
public class CommunityGroupController {

    private final CommunityGroupService communityGrpService;

    @GetMapping("/permission")
    @Operation(summary = "그룹 생성 권한 조회")
    public String getGroupsPermission() {
        return this.communityGrpService.getGroupsPermission();
    }

    @GetMapping
    @Operation(summary = "그룹 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public List<SearchRes> getCommunityGroups(@Parameter(description = "그룹코드")
                                                  @RequestParam(name = "groupCd", required = false) String groupCd) {
        return this.communityGrpService.getCommunityGroups(groupCd);
    }

    @GetMapping("/organizations-with-users")
    @Operation(summary = "조직/사용자 트리구조 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public List<OrgTreeRes> getOrganizationWithUserTree(@Parameter(description = "부서코드")
                                                            @RequestParam(name = "deptCd", required = false) String deptCd) {
        return this.communityGrpService.getOrganizationWithUserTree(deptCd);
    }

    @PostMapping
    @Operation(summary = "그룹 등록")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public CreateRes createCommunityGroup(@Valid @RequestBody CreateReq dto) {
        return this.communityGrpService.createCommunityGroup(dto);
    }

    @PutMapping("/{groupCd}")
    @Operation(summary = "그룹 수정")
    public int updateCommunityGroup(@PathVariable @Parameter(description = "그룹코드") String groupCd
            , @Valid @RequestBody UpdateReq dto) {
        return this.communityGrpService.updateCommunityGroup(groupCd, dto);
    }

    @DeleteMapping("/{groupCd}")
    @Operation(summary = "그룹 삭제")
    public int removeCommunityGroup(@PathVariable @Parameter(description = "그룹코드") String groupCd) {
        return this.communityGrpService.removeCommunityGroup(groupCd);
    }

    @GetMapping("/{groupCd}/users")
    @Operation(summary = "그룹 대상자 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public List<UserRes> getCommunityGroupUsers(@PathVariable @Parameter(description = "그룹코드") String groupCd) {
        return this.communityGrpService.getCommunityGroupUsers(groupCd);
    }

    @PostMapping("/users")
    @Operation(summary = "그룹 대상자 등록")
    public int createCommunityGroupUsers(@Valid @RequestBody SaveUserReq dto) {
        return this.communityGrpService.createCommunityGroupUsers(dto);
    }

    @DeleteMapping("/users")
    @Operation(summary = "그룹 대상자 삭제")
    public int removeCommunityGroupUsers(@Valid @RequestBody SaveUserReq dto) {
        return this.communityGrpService.removeCommunityGroupUsers(dto);
    }

    @PutMapping("/user")
    @Operation(summary = "그룹 대상자 수정")
    public int updateCommunityGroupUser(@Valid @RequestBody UpdateUserReq dto) {
        return this.communityGrpService.updateCommunityGroupUser(dto);
    }

    @GetMapping("/{groupCd}/property")
    @Operation(summary = "그룹 속성정보 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true )
    public PropRes getCommunityGroupProperty(@PathVariable @Parameter(description = "그룹코드") String groupCd) {

        return this.communityGrpService.getCommunityGroupProperty(groupCd);
    }



}
