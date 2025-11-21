package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;


@Getter
@Setter
public class CompressDvo {

    // 폴더여부
    private String folderYn;
    // 폴더/파일코드
    private String fileAttCd;
    // 폴더경로
    private String folderPath;
    // 대상 파일
    private Path targetFile;
    // 파일명
    private String fileNm;
    // 프로젝트 코드
    private String prjCd;
    // 공정코드
    private String procCd;
    // 파일그룹코드
    private String fileGrp;
    // 파일참조코드
    private String fileRefCd;
}
