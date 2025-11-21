package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FolderChildDvo {

    // 폴더코드
    private String folderCd;
    // 폴더명
    private String folderNm;
    // 부모폴더코드
    private String parentCd;
    // 부모폴더명
    private String parentNm;
    // 폴더그룹코드
    private String folderGrp;
    // 폴더보안여부
    private String foldSecurityYn;
    // 폴더구조
    private String orgFilePath;

}
