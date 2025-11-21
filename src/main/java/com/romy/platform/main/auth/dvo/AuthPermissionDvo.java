package com.romy.platform.main.auth.dvo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthPermissionDvo {

    // application context path
    private String contextPath;
    // 사용자코드
    private String usrCd;
    // uri
    private String uri;
    // http method
    private String method;

}
