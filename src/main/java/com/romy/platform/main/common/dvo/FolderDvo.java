package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderDvo {

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
    // 폴더그룹코드
    private String folderGrp;
    // 폴더타입
    private Integer folderType;
    // 정렬순서
    private Integer ordNum;
    // 부문-사용자코드-제안서공정코드(proposalProcCd)
    private String sectorCd;
    // 부서코드
    private String deptCd;
    // 참조코드
    private String ref;
    // 폴더 보안여부
    private String foldSecurityYn;
    // 삭제여부
    private String delYn;

    /**
     * 업로드 시 사용
     */
    // 임시 그룹코드
    private String tempGrp;
    // 임시 레벨
    private Integer tempLv;

    /**
     * 외부 솔루션으로 인한 AuditColumn
     */
    private String regDtt;
    private String regUsrCd;
    private String modDtt;
    private String modUsrCd;

    /**
     * 프로젝트 붙여넣기 시 사용
     */
    private String prjCd;
    private String procCd;

}
