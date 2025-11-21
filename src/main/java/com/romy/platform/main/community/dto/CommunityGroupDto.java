package com.romy.platform.main.community.dto;

import com.romy.platform.common.constants.PlatformConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;


public class CommunityGroupDto {

    @Schema(name = "CommunityGroupDto.GroupRes", description = "커뮤니티 그룹 조회")
    public record GroupRes(
            @Schema(description = "부서/그룹코드")
            String deptCd,
            @Schema(description = "부서/그룹명")
            String deptNm,
            @Schema(description = "폴더그룹코드")
            String folderGrp,
            @Schema(description = "그룹 정렬순서")
            int ordMst,
            @Schema(description = "세부 정렬순서")
            int ordDtl,
            @Schema(description = "활성화여부")
            boolean enable
    ){}

    @Schema(name = "CommunityGroupDto.OrgTreeRes", description = "커뮤니티 그룹 조직/사용자 트리 조회")
    public record OrgTreeRes(
            @Schema(description = "부서코드/사용자코드")
            String deptCd,
            @Schema(description = "부서명/성명")
            String deptNm,
            @Schema(description = "사용자 부서명")
            String userDeptNm,
            @Schema(description = "직위명")
            String sapSpotNm,
            @Schema(description = "부모부서코드")
            String parentDeptCd,
            @Schema(description = "구분", allowableValues = {"DEPT", "USER", "GROUP"})
            String div,
            @Schema(description = "정렬순서1(정렬용)")
            String ordStr,
            @Schema(description = "정렬순서2(정렬용)")
            String ord
    ) {}

    @Schema(name = "CommunityGroupDto.SearchRes", description = "커뮤니티 그룹 조회")
    public record SearchRes(
            @Schema(description = "그룹코드")
            String groupCd,
            @Schema(description = "그룹명")
            String groupNm,
            @Schema(description = "정렬순서")
            Integer ordNum,
            @Schema(description = "사용자그룹코드(매핑용)")
            String mgCd,
            @Schema(description = "정렬변경 가능 여부", allowableValues = {"Y", "N"})
            String reorderYn,
            @Schema(description = "등록사용자코드")
            String regUsrCd,
            @Schema(description = "등록자")
            String regUsrNm
    ) {}

    @Schema(name = "CommunityGroupDto.CreateReq", description = "커뮤니티 그룹 등록")
    public record CreateReq(
            @NotBlank
            @Size(max = 100)
            @Schema(description = "그룹명")
            String groupNm,
            @Schema(description = "정렬순서")
            Integer ordNum,
            @Schema(description = "대상자 리스트")
            List<String> userCds
    ) {}

    @Schema(name = "CommunityGroupDto.CreateRes", description = "커뮤니티 그룹 등록 결과")
    public record CreateRes(
            @Schema(description = "그룹코드")
            String groupCd
    ) {}

    @Schema(name = "CommunityGroupDto.UpdateReq", description = "커뮤니티 그룹 수정")
    public record UpdateReq(
            @NotBlank
            @Size(max = 100)
            @Schema(description = "그룹명")
            String groupNm,
            @Schema(description = "정렬순서")
            Integer ordNum
    ) {}

    @Schema(name = "CommunityGroupDto.UserRes", description = "커뮤니티 그룹 대상자 조회")
    public record UserRes(
            @Schema(description = "그룹코드")
            String groupCd,
            @Schema(description = "사용자코드")
            String usrCd,
            @Schema(description = "성명")
            String usrNm,
            @Schema(description = "직위")
            String sapSpotNm,
            @Schema(description = "부서명")
            String deptNm,
            @Schema(description = "관리자여부", allowableValues = {"Y", "N"})
            String adminYn
    ) {}

    @Schema(name = "CommunityGroupDto.SaveUserReq", description = "커뮤니티 그룹 대상자 생성/삭제")
    public record SaveUserReq(
            @NotBlank
            @Schema(description = "그룹코드")
            String groupCd,
            @NotEmpty
            @Schema(description = "대상자 리스트(사용자코드)")
            List<String> userCds
    ) {}

    @Schema(name = "CommunityGroupDto.UpdateUserReq", description = "커뮤니티 그룹 대상자 수정")
    public record UpdateUserReq(
            @NotBlank
            @Schema(description = "그룹코드")
            String groupCd,
            @NotBlank
            @Schema(description = "사용자코드")
            String usrCd,
            @NotBlank
            @Schema(description = "관리자 여부", allowableValues = {"Y", "N"})
            String adminYn
    ) {
        public UpdateUserReq {
            if (!PlatformConstant.YN_Y.equals(adminYn)) adminYn = PlatformConstant.YN_N;
        }
    }

    @Schema(name = "CommunityGroupDto.PropRes", description = "커뮤니티 그룹 속성정보 조회")
    public record PropRes(
            @Schema(description = "그룹코드")
            String groupCd,
            @Schema(description = "그룹명")
            String groupNm,
            @Schema(description = "등록일자")
            String regDt,
            @Schema(description = "편집 여부", allowableValues = {"Y", "N"})
            String editYn,
            @Schema(description = "등록자코드")
            String regUsrCd,
            @Schema(description = "등록자명")
            String regUsrNm,
            @Schema(description = "등록자 부서")
            String regDeptNm,
            @Schema(description = "등록자 직위")
            String regSpotNm,
            @Schema(description = "관리자코드")
            String adminUsrCd,
            @Schema(description = "관리자명")
            String adminUsrNm,
            @Schema(description = "관리자 부서")
            String adminDeptNm,
            @Schema(description = "관리자 직위")
            String adminSpotNm,
            @Schema(description = "대상자 목록")
            List<UserRes> users
    ) {}

}
