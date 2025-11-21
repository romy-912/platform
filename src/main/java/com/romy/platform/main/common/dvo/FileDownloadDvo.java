package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileDownloadDvo {

    // 파일명
    private String fileNm;
    // 물리경로
    private String filePhyPath;
    // 물리파일명
    private String filePhyNm;
    // lock 여부
    private String isLock;
    // docId
    private String docId;
    // 파일참조코드
    private String fileRefCd;
    // 파일그룹
    private String fileGrp;
    // 프로젝트코드
    private String prjCd;
    // 공정코드
    private String procCd;
}
