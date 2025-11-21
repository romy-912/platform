package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileAttFolderLocDvo {

    // 파일첨부코드
    private String fileAttCd;
    // 폴더코드
    private String folderCd;
    // 삭제여부
    private String delYn;

    /**
     * 외부 솔루션으로 인한 AuditColumn
     */
    private String regDtt;
    private String regUsrCd;
}
