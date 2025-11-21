package com.romy.platform.main.community.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import static com.romy.platform.main.community.dto.CommunityAuthDto.*;

import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.common.service.IdGenerateService;
import com.romy.platform.main.community.converter.CommunityAuthConverter;
import static com.romy.platform.main.community.dto.CommunityGroupDto.GroupRes;
import com.romy.platform.main.community.dvo.AuthDvo;
import com.romy.platform.main.community.mapper.CommunityAuthMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityAuthService {

    private final CommunityAuthMapper mapper;
    private final CommunityAuthConverter converter;

    private final IdGenerateService idGenService;


    /**
     * 커뮤니티 폴더 생성 권한 조회
     */
    public String getFolderCreatePermission(String deptCd, String usrCd) {
        if (StringUtils.isBlank(usrCd) && PlatformUtil.hasSession()) {
            AuthUserDvo userDvo = PlatformUtil.getUserInfo();
            usrCd = userDvo.getUsrCd();
        }
        String permissionYn = this.mapper.selectFolderCreatePermission(deptCd, usrCd);
        return StringUtils.isEmpty(permissionYn) ? PlatformConstant.YN_N : permissionYn;
    }

    /**
     * 커뮤니티 폴더/파일 권한 조회
     */
    public List<AuthRes> getCommunityAuthorizations(String folderFileCd) {
        return this.mapper.selectCommunityAuthorizations(folderFileCd);
    }

    /**
     * 커뮤니티 권한 삭제
     */
    @PlatformTransactional
    public List<AuthRes> removeCommunityAuth(RemoveReq dto) {
        String permissionYn = this.mapper.selectAuthDeletePermission(dto.folderFileCd(), dto.folderYn());
        if (!PlatformConstant.YN_Y.equals(permissionYn)) {
            // 삭제 권한이 없습니다.
            throw new PlatformException(PlatformConstant.COMMU_NO_PERMISSION_DELETE);
        }

        this.mapper.deleteCommunityAuth(dto.folderFileCd(), dto.authCd());

        return this.mapper.selectCommunityAuthorizations(dto.folderFileCd());
    }

    /**
     * 커뮤니티 폴더 권한 체크
     */
    public void checkCommunityFolderAuth(String folderCd) {
        String permissionYn = this.mapper.selectCommunityFolderAuth(folderCd);
        if (!PlatformConstant.YN_Y.equals(permissionYn)) {
            // 생성 권한이 없습니다.
            throw new PlatformException(PlatformConstant.COMMU_NO_PERMISSION_CREATE);
        }
    }

    /**
     * 커뮤니티 권한 설정
     */
    @PlatformTransactional
    public int createCommunityAuth(String folderFileCd, CreateReq dto) {
        String permissionYn = this.mapper.selectAuthDeletePermission(folderFileCd, dto.folderYn());
        if (!PlatformConstant.YN_Y.equals(permissionYn)) {
            // 생성 권한이 없습니다.
            throw new PlatformException(PlatformConstant.COMMU_NO_PERMISSION_CREATE);
        }

        List<AuthDvo> dvos = this.converter.commuAuthToAuthDvoList(dto.auths());

        return this.mapper.insertCommunityAuths(folderFileCd, dvos);
    }

    /**
     * 폴더/파일 조회 권한체크
     */
    @PlatformTransactional
    public void checkCommuFolderFileAuth(String folderGrp, String sectorCd, String folderCd) {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();

        // 커뮤니티가 아닌 경우 skip
        if (!"MYDOC".equals(folderGrp) && !"DEPT".equals(folderGrp) && !"GROUP".equals(folderGrp)) return;

        if (StringUtils.isAllBlank(sectorCd, folderCd)) return;

        if (StringUtils.isNotBlank(sectorCd)) {
            // root 폴더 체크
            int rootCnt = this.mapper.selectRootFolderCnt(folderGrp, sectorCd);
            if (rootCnt == 0) {
                String newFolderCd = this.idGenService.getNextStringId();
                this.mapper.insertRootFolderByCommunity(newFolderCd, folderGrp, sectorCd);
            }
        }

        if (PlatformConstant.YN_Y.equals(userDvo.getAdminYn())) return;

        // 내캐비닛 권한 체크
        if ("MYDOC".equals(folderGrp)) {
            String usrCd = userDvo.getUsrCd();
            this.checkMyDocAuth(usrCd, sectorCd, folderCd);

        // 그룹 문서함 권한 체크
        } else if ("GROUP".equals(folderGrp)) {

            String check = this.mapper.selectGroupDocAuthCheck(sectorCd, folderCd);
            if (!PlatformConstant.YN_Y.equals(check)) {
                // 잘못된 접근입니다.
                throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
            }

        } else {
            this.checkDeptDocAuth(sectorCd, folderCd);
        }

        // 폴더권한 체크
        if (!"MYDOC".equals(folderGrp) && StringUtils.isNotBlank(folderCd)) {
            List<String> notPermissions = this.mapper.selectCommuFolderAccessAuth(folderCd);
            if (CollectionUtils.isNotEmpty(notPermissions)) {
                // 잘못된 접근입니다.
                throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
            }
        }
    }

    /**
     * 내 캐비닛 문서함 권한 체크
     */
    private void checkMyDocAuth(String usrCd, String sectorCd, String folderCd) {
        // 본인 문서함이 아닌 경우
        if (StringUtils.isNotBlank(sectorCd) && !sectorCd.equals(usrCd)) {
            // 잘못된 접근입니다.
            throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
        }

        // 폴더 단위 권한 확인
        if (StringUtils.isNotBlank(folderCd)) {
            String check = this.mapper.selectMyDocAuthCheck(folderCd);
            if (PlatformConstant.YN_Y.equals(check)) {
                // 잘못된 접근입니다.
                throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
            }
        }
    }

    /**
     * 소속 부서 문서함 권한 체크
     */
    private void checkDeptDocAuth(String sectorCd, String folderCd) {
        List<GroupRes> depts = this.mapper.selectCommunityDepts();

        // sectorCd가 명시된 경우
        if (StringUtils.isNotBlank(sectorCd)) {
            boolean notMatched = depts.stream()
                                      .noneMatch(group -> group.deptCd().equals(sectorCd));
            if (notMatched) {
                // 잘못된 접근입니다.
                throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
            }
            return;
        }

        // folderCd 기준 부서코드 조회 후 검증
        String docSectorCd = this.mapper.selectDeptDocSectorCd(folderCd);
        if (StringUtils.isBlank(docSectorCd)) {
            // 잘못된 접근입니다.
            throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
        }

        boolean notMatched = depts.stream()
                                  .noneMatch(group -> group.deptCd().equals(docSectorCd));
        if (notMatched) {
            // 잘못된 접근입니다.
            throw new PlatformException(PlatformConstant.AUTH_INVALID_ACCESS);
        }
    }


}
