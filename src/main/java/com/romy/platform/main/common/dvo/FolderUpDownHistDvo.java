package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FolderUpDownHistDvo {

    // 업/다운로드 코드
    private String fafUdCd;
    // 참조코드
    private String upRefCd;
    // 업/다운 유형
    private String udType;
    // 폴더그룹코드
    private String folderGrp;
    // 결과유형
    private String endType;
}
