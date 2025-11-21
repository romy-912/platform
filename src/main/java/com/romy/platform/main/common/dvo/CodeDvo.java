package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CodeDvo {

    // 코드
    private String cd;
    // 부모코드
    private String parentCd;
    // 코드명
    private String cdNm;
    // 코드내용
    private String cdCont;
    // 정렬순서
    private Integer ordNum;
    // 외부코드여부
    private String outCdYn;
    // 참조코드
    private String refCd;
    // 시스템코드여부
    private String sysCdYn;
    // 사용여부
    private String useYn;
    // SAP연동구분
    private String sapCdDiv;
    // SAP코드
    private String sapCd;
    // SAP연동 MIS 관련코드
    private String sapMisCd;
    // SAP연동 MIS 관련코드명
    private String sapMisNm;
    // 설계용 부문여부
    private String designSectorCdYn;
    // 코드내용2
    private String cdCont2;
    // 그룹
    private String tempGrp;
    // 레벨
    private String tempLv;
}
