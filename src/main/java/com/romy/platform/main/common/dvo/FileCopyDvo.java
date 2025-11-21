package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileCopyDvo {

    // Original 파일첨부코드
    private String fileAttCd;
    // New 파일첨부코드
    private String newFileAttCd;
    // New 파일 물리경로
    private String newFilePhyPath;
    // New 파일그룹코드
    private String newFileGrp;
    // New 파일참조코드
    private String newFileRefCd;
    // New 프로젝트코드
    private String newPrjCd;
    // New 공정코드
    private String newProcCd;
    // 영구지정
    private String permanent;

}
