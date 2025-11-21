package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FilePropDvo {

    // 파일첨부코드
    private String fileAttCd;
    // 파일명
    private String fileNm;
    // 파일그룹
    private String fileGrp;
    // 파일 확장자
    private String fileExt;
    // 폴더코드
    private String folderCd;
    // 부문/사용자코드
    private String sectorCd;
    // 폴더경로
    private String folderPath;
    // 파일 사이즈
    private Long fileSize;
    // 등록일시
    private String regDtt;
    // 수정일시
    private String modDtt;
    // 등록자명
    private String regUsrNm;
    // 읽기전용
    private String readonlyYn;
    // 보안여부
    private String securityYn;
    // 편집여부
    private String editYn;
}
