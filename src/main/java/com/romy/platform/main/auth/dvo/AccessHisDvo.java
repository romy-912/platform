package com.romy.platform.main.auth.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccessHisDvo {

    // 메뉴코드
    private String menuCd;
    // 프로그램코드
    private String programCd;
    // 사용자 IP
    private String accessIp;
    // 접근메뉴경로
    private String accessMenu;
    // 프로그램명
    private String reqProgram;
    // 사용자ID
    private String regUsrId;
    // 사용자명
    private String regUsrNm;
    // 직책명
    private String regUsrSpot;
    // 사용자부서명
    private String regUsrDept;
    // 프로그램경로
    private String programUrl;
}
