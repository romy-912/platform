package com.romy.platform.main.auth.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginHistoryDvo {

    // 로그인이력 코드
    private int lhCd;
    // 사용자코드
    private String usrCd;
    // 로그일일시
    private String loginDtt;
    // 접속IP
    private String accessIp;
    // 브라우저구분
    private String browserDiv;
    // 로그인구분
    private String div;

}
