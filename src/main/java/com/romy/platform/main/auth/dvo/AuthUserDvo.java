package com.romy.platform.main.auth.dvo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
public class AuthUserDvo implements UserDetails {

    // 사용자코드
    private String usrCd;
    // 사용자명
    private String usrNm;
    // 사번
    private String usrId;
    // 메일주소
    private String usrMail;
    // 부서코드
    private String deptCd;
    // 부서명
    private String deptNm;
    // SAP연동 ORGNZT_ID
    private String sapDeptCd;
    // SAP연동 ORGNZT_NM
    private String sapDeptNm;
    // SAP연동 MIS_DEPT_CD
    private String sapMisDeptCd;
    // SAP연동 MIS_DEPT_NM
    private String sapMisDeptNm;
    // SAP연동 직위코드
    private String sapSpotCd;
    // SAP연동 직위명
    private String sapSpotNm;
    // SAP연동 부문코드
    private String sapGroupCd;
    // 관리자여부
    private String adminYn;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.usrId;
    }
}
