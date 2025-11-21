package com.romy.platform.common.token;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SessionDvo {

    // 등록일자 (YYYYMMDD)
    private String regDt;
    // 등록일시 (YYYYMMDDHH24MISS)
    private String regDtt;
    // 등록사용자코드
    private String regUsrCd;
    // 수정일자 (YYYYMMDD)
    private String modDt;
    // 수정일시 (YYYYMMDDHH24MISS)
    private String modDtt;
    // 수정사용자코드
    private String modUsrCd;
    // 삭제일시 (YYYYMMDDHH24MISS)
    private String delDtt;
    // 삭제사용자코드
    private String delUsrCd;

    // 사용자코드
    private String usrCd;
    // 생성자
    private String createdBy;
    // 사번
    private String usrId;
    // 부서코드
    private String deptCd;
    // SAP부문코드
    private String sapGroupCd;
}
