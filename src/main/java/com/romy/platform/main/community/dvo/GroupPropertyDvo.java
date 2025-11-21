package com.romy.platform.main.community.dvo;

import static com.romy.platform.main.community.dto.CommunityGroupDto.UserRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GroupPropertyDvo {

    // 그룹코드
    private String groupCd;
    // 그룹명
    private String groupNm;
    // 등록일
    private String regDt;
    // 수정여부
    private String editYn;

    // 등록자정보
    private String regUsrCd;
    private String regUsrNm;
    private String regDeptNm;
    private String regSpotNm;

    // 관리자정보
    private String adminUsrCd;
    private String adminUsrNm;
    private String adminDeptNm;
    private String adminSpotNm;

    private List<UserRes> users;

}
