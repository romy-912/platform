package com.romy.platform.main.community.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoticeCondDvo {

    // 조건 구분
    private String searchCond;
    // 검색어
    private String searchKeyword;
    // 연도
    private String year;
    // 페이지번호
    private int startIdx;
    // 페이지 건수
    private int listSize;
    // 중요 게시만 보기
    private String favoriteYn;

    // 관리자여부
    private String adminYn;
    // 부문코드 (공통코드)
    private String sectorCd;
    // 부문부서코드
    private String sectorDeptCd;
}
