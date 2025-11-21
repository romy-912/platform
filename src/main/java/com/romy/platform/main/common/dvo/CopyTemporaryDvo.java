package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CopyTemporaryDvo {

    // 복사코드
    private String ctCd;
    // 구분
    private String div;
    // 파일폴더구분
    private String dataType;
    // 파일코드
    private String fileAttCd;
    // 폴더코드
    private String folderCd;
    // 복사-파일그룹
    private String beforeFileGrp;
    // 복사-프로젝트코드
    private String beforePrjCd;
    // 복사-프로젝트공정코드
    private String beforePrjProcCd;
    // 복사-공정코드
    private String beforeProcCd;
    // 복사-부모폴더코드
    private String beforeParentFolderCd;
    // 붙여넣기-파일그룹
    private String afterFileGrp;
    // 붙여넣기-프로젝트코드
    private String afterPrjCd;
    // 붙여넣기-프로젝트공정코드
    private String afterPrjProcCd;
    // 붙여넣기-공정코드
    private String afterProcCd;
    // 붙여넣기-부모폴더코드
    private String afterParentFolderCd;

}
