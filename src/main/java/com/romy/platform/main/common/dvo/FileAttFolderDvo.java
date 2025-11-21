package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileAttFolderDvo {

    // 폴더코드
    private String folderCd;
    // 폴더명
    private String folderNm;
    // 부모폴더코드
    private String parentCd;
    // 부모폴더명
    private String parentNm;
    // 부모상위 폴더명
    private String grandParentNm;
    // 폴더참조코드
    private String folderRefCd;
    // 레벨
    private Integer level;
    // 폴더그룹코드
    private String folderGrp;
    // 정렬순서
    private Integer ordNum;
    // 폴더 보안여부
    private String foldSecurityYn;
    // 삭제여부
    private String delYn;


    /**
     * 외부 솔루션으로 인한 AuditColumn
     */
    private String regDtt;
    private String regUsrCd;
    private String delDtt;
    private String delUsrCd;

}
