package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupwareMailFileDvo {

    // 파일첨부코드
    private String fileAttCd;
    // 사용자코드
    private String usrCd;
    // 파일명
    private String fileNm;
    // 파일사이즈
    private long fileSize;

}
