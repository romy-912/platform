package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileDvo {

    // 파일첨부코드
    private String fileAttCd;
    // 파일명
    private String fileNm;
    // 확장자
    private String fileExt;
    // 물리경로
    private String filePhyPath;
    // 사이즈
    private Long fileSize;
    // 참조코드
    private String fileRefCd;
    // 파일그룹코드
    private String fileGrp;
    // 다운로드횟수
    private Integer fileDlCnt;
    // 최종버전여부
    private String fileFinalVerYn;
    // 프로젝트코드
    private String prjCd;
    // 공정코드
    private String procCd;

    private String fileLabel;
    private String confidential;
    // Synap ID
    private String docId;
    // Lock여부
    private String isLock;
    // 편집여부
    private String openYn;
    // 파일버전그룹코드
    private String fileVerGrpCd;
    // 다운로드여부
    private String downloadYn;
    // 보안여부
    private String fileSecurityYn;

    // 삭제여부
    private String delYn;
    // 삭제일시
    private String delDtt;
    // 삭제사용자코드
    private String delUsrCd;

    // 영구지정
    private String permanent;

    private String modDtt;
    private String modUsrCd;
    private String regUsrCd;
    private String regDtt;

}
