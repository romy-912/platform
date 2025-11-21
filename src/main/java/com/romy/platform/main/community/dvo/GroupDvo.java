package com.romy.platform.main.community.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupDvo {

    // 그룹코드
    private String groupCd;
    // 그룹명
    private String groupNm;
    // 정렬순서
    private Integer ordNum;
    // 사용자그룹코드
    private String mgCd;

}
