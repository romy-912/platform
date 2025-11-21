package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FolderTreeDvo {
    
    // 폴더코드
    private String folderCd;
    // 폴더명
    private String folderNm;
    // 부모폴더코드
    private String parentCd;
    // 부모폴더명
    private String parentNm;
    // 정렬순서
    private Integer ordNum;
    // 레벨
    private int lv;
    // 폴더유형
    private String folderType;
    // 보안여부
    private String foldSecurityYn;
    // 폴더 소유자코드
    private String ownerUsrCd;
    // 폴더 소유자
    private String ownerUsrNm;
    // 폴더 사이즈
    private long folderSize;
    
}
