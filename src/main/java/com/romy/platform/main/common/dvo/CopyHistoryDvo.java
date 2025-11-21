package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CopyHistoryDvo {

    // 구분
    private String div;
    // 파일코드
    private String fCd;
    // 파일명
    private String fName;
    // 파일그룹
    private String fGrp;
    // 파일참조코드
    private String fRefCd;
    // 이전 파일그룹
    private String bGrp;
    // 이후 파일그룹
    private String aGrp;
    // 연결파일코드
    private String afterCd;

}
