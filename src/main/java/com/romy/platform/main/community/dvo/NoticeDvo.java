package com.romy.platform.main.community.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoticeDvo {

    // 게시판코드
    private String brdCd;
    // 게시판마스터코드
    private String brdMstCd;
    // 공지유형
    private String noticeType;
    // 부문코드
    private String brdDivDetailCd;
    // 부서코드(공개범위코드)
    private String deptCd;
    // 제목
    private String brdTit;
    // 내용
    private String brdCont;
    // 검색내용
    private String brdSchCont;
    // 조회수
    private Long schCnt;
    // 상단공지여부
    private String noticeYn;
    // 메인공지여부
    private String mainNoticeYn;
    // 팝업위치정보
    private Integer noticePopupTopLocation;
    private Integer noticePopupLeftLocation;
    private Integer noticePopupWidthSize;
    private Integer noticePopupHeightSize;

    // 팝업게시일자
    private String noticeStDt;
    private String noticeEdDt;

    // 삭제여부
    private String delYn;
    // 공개여부
    private String openYn;
    // 답변여부
    private String answerYn;
    // 등록자명
    private String regUsrNm;
    // 등록자직위
    private String regUsrSpot;
    // 등록자부서
    private String regUsrDept;
}
