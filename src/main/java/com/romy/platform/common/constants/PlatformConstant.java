package com.romy.platform.common.constants;


public class PlatformConstant {

    public static final int PLATFORM_EXCEPTION_STATUS = 499;

    public static final String YN_Y = "Y";
    public static final String YN_N = "N";

    public static final String DEPT_NM_ALL = "전체(전사)";


    /**
     * 메시지 코드 리스트
     */
    // {0} 은(는) 필수 입력 항목입니다.
    public static final String COMMON_REQUIRED_VALUE = "common.required.value";
    // 파일이 존재하지 않습니다.\n[경로 : {0}]
    public static final String COMMON_FILE_NOT_EXIST = "common.file.not.exist";
    // 파일 정보를 찾을 수 없습니다.
    public static final String COMMON_FILE_INFO_NOT_EXIST = "common.file.info.not.exist";
    // 확장자가 다른 경우 업로드할 수 없습니다.
    public static final String COMMON_FILE_EXTENSION = "common.file.extension.mismatch";
    // 폴더 정보를 찾을 수 없습니다.
    public static final String COMMON_FOLDER_INFO_NOT_EXIST = "common.folder.info.not.exist";
    // 폴더 생성 권한이 없습니다.
    public static final String COMMON_FOLDER_NOT_PERMISSION = "common.folder.not.permission";

    // 파일 코드와 폴더 코드 중 적어도 하나는 반드시 입력해야 합니다.
    public static final String COMMON_FILEORFOLDER_REQUIRED = "common.fileOrFolder.required";
    // 요청에 실패했습니다.
    public static final String COMMON_REQUEST_FAIL = "common.request.fail";
    // 클립보드가 비어 있습니다.
    public static final String COMMON_CLIPBOARD_EMPTY = "common.clipboard.empty";
    // 하위 폴더 및 파일이 존재하여 삭제할 수 없습니다.
    public static final String COMMON_DELETE_HAS_CHILD = "common.delete.has.child";


    // 사용자가 없습니다. [아이디: {0}]
    public static final String AUTH_USER_EMPTY = "auth.user.empty";
    // 잘못된 접근입니다.
    public static final String AUTH_INVALID_ACCESS = "auth.invalid.access";
    // 권한 없는 폴더가 존재합니다.
    public static final String AUTH_NO_PERMISSION_FOLDER = "auth.no.permission.folder";
    // 권한 없는 파일이 존재합니다.
    public static final String AUTH_NO_PERMISSION_FILE = "auth.no.permission.file";


    // 팝업크기는 필수 입력 항목입니다.
    public static final String COMMU_NOTICE_POPUP_LOCATION = "commu.notice.popup.location";
    // 공지팝업 기간은 필수 입력 항목입니다.
    public static final String COMMU_NOTICE_POPUP_PERIOD = "commu.notice.popup.period";
    // 팝업의 너비(Width) 최대는 1600, 높이(Height) 최대는 1200 입니다.
    public static final String COMMU_NOTICE_POPUP_SIZE = "commu.notice.popup.size";
    // 공지사항이 존재하지 않습니다.
    public static final String COMMU_NOTICE_NOT_EXIST = "commu.notice.not.exist";
    // 삭제 권한이 없습니다.
    public static final String COMMU_NO_PERMISSION_DELETE = "commu.no.permission.delete";
    // 생성 권한이 없습니다.
    public static final String COMMU_NO_PERMISSION_CREATE = "commu.no.permission.create";
    // 해당 그룹에 권한이 없습니다.
    public static final String COMMU_NO_PERMISSION_GROUP = "commu.no.permission.group";
    // 그룹의 관리자는 1명만 지정할 수 있습니다.
    public static final String COMMU_GROUP_ADMIN_SINGLE = "commu.group.admin.single";
    // 그룹은 {0}개를 초과하여 등록할 수 없습니다.
    public static final String COMMU_GROUP_MAX_EXCEEDED = "commu.group.max.exceeded";
    // 이미 등록된 대상자가 포함되어 있습니다.
    public static final String COMMU_TARGET_ALREADY = "commu.target.already";
    // 그룹이 존재하지 않습니다.
    public static final String COMMU_GROUP_NOT_EXIST = "commu.group.not.exist";



    /**
     * 필드
     * */
    public static final String PARENT_CD = "parentCd";
    public static final String FILE_GRP = "fileGrp";
    public static final String FILE_REF_CD = "fileRefCd";
    public static final String FILE_ATT_CD = "fileAttCd";
    public static final String FOLDER_CD = "folderCd";
    public static final String FAVORITE_CD = "favoriteCd";
    public static final String FAVORITE_DIV = "favoriteDiv";
    public static final String FAVORITE_KEY_CD = "favoriteKeyCd";

}
