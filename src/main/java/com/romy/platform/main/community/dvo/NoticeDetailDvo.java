package com.romy.platform.main.community.dvo;

import com.romy.platform.main.common.dvo.FileInfoDvo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class NoticeDetailDvo {

    // 게시판코드
    private String brdCd;
    // 게시판마스터코드
    private String brdMstCd;
    // 공지유형코드
    private String noticeType;
    // 공지유형
    private String noticeTypeNm;
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
    // 부서운영현황 공지여부
    private String deptOperNoticeYn;
    // 메인공지여부
    private String mainNoticeYn;
    // 팝업위치정보
    private Integer noticePopupTopLocation;
    private Integer noticePopupLeftLocation;
    private Integer noticePopupWidthSize;
    private Integer noticePopupHeightSize;

    // 공지기간
    private String noticeStDt;
    private String noticeEdDt;

    // 등록일자
    private String modDt;
    // 등록자명
    private String regUsrNm;
    // 등록자부서
    private String regUsrDept;
    // 부문코드
    private String brdDivDetailCd;
    // 부서코드(공개범위코드)
    private String deptCd;
    // 공개범위
    private String exposureScope;
    // 즐겨찾기여부
    private String favoriteYn;
    // 즐겨찾기코드
    private String favoriteCd;
    // 첨부파일 리스트
    private List<FileInfoDvo> files;
}
