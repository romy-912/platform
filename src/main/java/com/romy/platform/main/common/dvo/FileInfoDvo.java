package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileInfoDvo {

    // 파일첨부코드
    private String fileAttCd;
    // 파일명
    private String fileNm;
    // 확장자
    private String fileExt;
    // 사이즈
    private Long fileSize;
    // 프로젝트코드
    private String prjCd;
    // 공정코드
    private String procCd;
}
