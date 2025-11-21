package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TrashDvo {

    // 휴지통코드
    private String trashCd;
    // 파일/폴더코드
    private String targetCd;
    // 파일/폴더명
    private String targetNm;
    // 휴지통그룹
    private String trashGrp;
    // 파일/폴더/폴더위치 구분
    private String div;
    // 위치구분
    private String location;
    // 위치경로상세
    private String locationDetail;
    // 부모폴더코드
    private String parentCd;
    // 최상위여부
    private String rootYn;
    // 삭제여부
    private String delYn;
    // 복구여부
    private String restoreYn;

}
