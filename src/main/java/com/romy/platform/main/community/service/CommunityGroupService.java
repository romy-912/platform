package com.romy.platform.main.community.service;


import static com.romy.platform.main.community.dto.CommunityGroupDto.*;


import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;

import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.common.service.IdGenerateService;
import com.romy.platform.main.community.converter.CommunityGroupConverter;
import com.romy.platform.main.community.dvo.GroupDvo;
import com.romy.platform.main.community.dvo.GroupPropertyDvo;
import com.romy.platform.main.community.dvo.NoticeCondDvo;
import com.romy.platform.main.community.mapper.CommunityGroupMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityGroupService {

    private final CommunityGroupConverter converter;
    private final CommunityGroupMapper communityGrpMapper;

    private final IdGenerateService idGenService;
    private final CommunityNoticeService noticeService;


    /**
     * 그룹 생성 권한 조회
     */
    public String getGroupsPermission() {
        return this.communityGrpMapper.selectGroupsPermission();
    }

    /**
     * 커뮤니티 그룹 메뉴 조회
     */
    public List<GroupRes> getCommunityGroupMenus() {
        return this.communityGrpMapper.selectCommunityGroupMenus();
    }

    /**
     * 부서, 사용자 트리구조 조회
     */
    public List<OrgTreeRes> getOrganizationWithUserTree(String deptCd) {
        String sectorDeptCd = null;
        // 부서코드가 존재할 경우 부문 조회
        if (StringUtils.isNotBlank(deptCd)) {
            NoticeCondDvo condDvo = this.noticeService.getSectorInfoByDeptCd(deptCd);
            sectorDeptCd = condDvo.getSectorDeptCd();
        }

        return this.communityGrpMapper.selectOrganizationWithUserTree(sectorDeptCd);
    }

    /**
     * 커뮤니티 그룹조회
     */
    public List<SearchRes> getCommunityGroups(String groupCd) {
        // 그룹코드가 있을 경우 권한체크
        if (StringUtils.isNotBlank(groupCd)) {
            this.checkCommunityGroupPermission(groupCd);
        }

        return this.communityGrpMapper.selectCommunityGroups(groupCd);
    }

    /**
     * 그룹 관리 권한 체크
     */
    private void checkCommunityGroupPermission(String groupCd) {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        // 관리자인 경우 skip
        if (PlatformConstant.YN_Y.equals(userDvo.getAdminYn())) return;

        String permissionYn = this.communityGrpMapper.selectGroupEditPermission(groupCd);
        if (!PlatformConstant.YN_Y.equals(permissionYn)) {
            // 해당 그룹에 권한이 없습니다.
            throw new PlatformException(PlatformConstant.COMMU_NO_PERMISSION_GROUP);
        }
    }

    /**
     * 커뮤니티 그룹 등록
     */
    @PlatformTransactional
    public CreateRes createCommunityGroup(CreateReq dto) {
        // 그룹 생성권한 체크
        String permissionYn = this.getGroupsPermission();
        if (!PlatformConstant.YN_Y.equals(permissionYn)) {
            // 생성 권한이 없습니다.
            throw new PlatformException(PlatformConstant.COMMU_NO_PERMISSION_CREATE);
        }

        GroupDvo dvo = this.converter.createReqToGroupDvo(dto);
        dvo.setGroupCd(this.idGenService.getNextStringId());

        String groupCd = dvo.getGroupCd();

        // 커뮤니티 그룹 저장
        this.communityGrpMapper.mergeCommunityGroup(dvo);

        int groupCnt = this.communityGrpMapper.selectCommunityGroupCount();
        if (groupCnt > 3) {
            // 그룹은 {0}개를 초과하여 등록할 수 없습니다.
            String msg = MessageProvider.getMessage(PlatformConstant.COMMU_GROUP_MAX_EXCEEDED, new Object[]{3});
            throw new PlatformException(msg);
        }

        List<String> usrCd = dto.userCds();
        if (CollectionUtils.isNotEmpty(usrCd)) {
            SaveUserReq userDto = this.converter.dataToSaveUserReq(groupCd, usrCd);
            this.createCommunityGroupUsers(userDto);
        }

        return new CreateRes(groupCd);
    }


    /**
     * 커뮤니티 그룹 업데이트
     */
    @PlatformTransactional
    public int updateCommunityGroup(String groupCd, UpdateReq dto) {
        // 그룹 권한 체크
        this.checkCommunityGroupPermission(groupCd);

        GroupDvo dvo = this.converter.dataToGroupDvo(groupCd, dto);

        return this.communityGrpMapper.mergeCommunityGroup(dvo);
    }

    /**
     * 커뮤니티 그룹 삭제
     */
    @PlatformTransactional
    public int removeCommunityGroup(String groupCd) {
        // 그룹 권한체크
        String permissionYn = this.getGroupsPermission();
        if (!PlatformConstant.YN_Y.equals(permissionYn)) {
            // 삭제 권한이 없습니다.
            throw new PlatformException(PlatformConstant.COMMU_NO_PERMISSION_DELETE);
        }

        // 폴더 존재여부 체크
        long folderCnt = this.communityGrpMapper.selectCommunityGroupFolderCnt(groupCd);
        if (folderCnt > 0) {
            // 하위 폴더 및 파일이 존재하여 삭제할 수 없습니다.
            throw new PlatformException(PlatformConstant.COMMON_DELETE_HAS_CHILD);
        }

        // 그룹 대상자 삭제
        this.communityGrpMapper.deleteCommuGroupUserByGroupCd(groupCd);

        // ROOT 폴더 삭제
        this.communityGrpMapper.deleteGroupRootFolder(groupCd);

        return this.communityGrpMapper.deleteCommunityGroup(groupCd);
    }

    /**
     * 커뮤니티 그룹 대상자 조회
     */
    public List<UserRes> getCommunityGroupUsers(String groupCd) {
        return this.communityGrpMapper.selectGroupUsers(groupCd);
    }


    /**
     * 커뮤니티 그룹 대상자 생성
     */
    @PlatformTransactional
    public int createCommunityGroupUsers(SaveUserReq dto) {
        String groupCd = dto.groupCd();
        // 그룹 권한체크
        this.checkCommunityGroupPermission(groupCd);

        List<String> usrCds = dto.userCds();

        // 사용자 체크
        int userCnt = this.communityGrpMapper.selectGroupUserCount(groupCd, usrCds);
        if (userCnt > 0) {
            // 이미 등록된 대상자가 포함되어 있습니다.
            throw new PlatformException(PlatformConstant.COMMU_TARGET_ALREADY);
        }

        return this.communityGrpMapper.insertCommunityGroupUsers(groupCd, usrCds);
    }

    /**
     * 커뮤니티 그룹 대상자 삭제
     */
    @PlatformTransactional
    public int removeCommunityGroupUsers(SaveUserReq dto) {
        String groupCd = dto.groupCd();
        // 그룹 권한체크
        this.checkCommunityGroupPermission(groupCd);

        List<String> usrCds = dto.userCds();

        return this.communityGrpMapper.deleteCommunityGroupUser(groupCd, usrCds);
    }

    /**
     * 커뮤니티 그룹 대상자 수정
     */
    @PlatformTransactional
    public int updateCommunityGroupUser(UpdateUserReq dto) {
        String groupCd = dto.groupCd();

        // 그룹 권한체크
        this.checkCommunityGroupPermission(groupCd);

        String usrCd = dto.usrCd();
        String adminYn = dto.adminYn();

        // 해당 사용자 제외 하고 관리자 취소
        if (PlatformConstant.YN_Y.equals(adminYn)) {
            this.communityGrpMapper.updateCommunityGroupAdminCnl(groupCd, usrCd);
        }

        int count = this.communityGrpMapper.updateCommunityGroupUser(groupCd, usrCd, adminYn);

        // 관리자 체크
        int adminCnt = this.communityGrpMapper.selectCommuGroupAdminCount(groupCd);
        if (adminCnt > 1) {
            // 그룹의 관리자는 1명만 지정할 수 있습니다.
            throw new PlatformException(PlatformConstant.COMMU_GROUP_ADMIN_SINGLE);
        }

        return count;
    }

    /**
     * 그룹 속성정보 조회
     */
    public PropRes getCommunityGroupProperty(String groupCd) {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();

        // 그룹 정보 조회
        GroupPropertyDvo dvo = this.communityGrpMapper.selectCommunityGroupProperty(groupCd);
        if (dvo == null) {
            // 그룹이 존재하지 않습니다.
            throw new PlatformException(PlatformConstant.COMMU_GROUP_NOT_EXIST);
        }

        if (PlatformConstant.YN_Y.equals(userDvo.getAdminYn())) {
            dvo.setEditYn(PlatformConstant.YN_Y);
        }

        List<UserRes> users = this.getCommunityGroupUsers(groupCd);
        dvo.setUsers(users);

        return this.converter.groupPropertyDvoToPropRes(dvo);
    }



}
