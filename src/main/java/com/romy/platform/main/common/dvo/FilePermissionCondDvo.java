package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class FilePermissionCondDvo {

    // 파일그룹
    private String fileGrp;
    // 폴더코드
    private String folderCd;
    // 폴더코드 리스트 (프로젝트 파일용)
    private List<String> refCds;
    // 파일코드 리스트
    private List<String> fileCds;
    // 쓰기권한여부
    private String writeYn;
    // 사용자코드
    private String sectorCd;
}
