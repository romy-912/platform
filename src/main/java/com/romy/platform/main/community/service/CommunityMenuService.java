package com.romy.platform.main.community.service;

import static com.romy.platform.main.community.dto.CommunityMenuDto.*;

import static com.romy.platform.main.community.dto.CommunityGroupDto.GroupRes;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.main.community.converter.CommunityMenuConverter;
import com.romy.platform.main.community.mapper.CommunityMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityMenuService {

    private final CommunityMenuConverter converter;
    private final CommunityMenuMapper commMenuMapper;

    private final CommunityGroupService commuGrpService;
    private final CommunityAuthService commuAuthService;



    /**
     * 커뮤니티 메뉴조회
     */
    public List<SearchRes> getCommunityMeus(String menuDiv) {

        if ("DEPT".equals(menuDiv)) {
            // 목록형 메뉴
            List<SearchRes> results = new ArrayList<>(this.commMenuMapper.selectCommunityMenus());

            // 소속부문 문서함 조회
            List<GroupRes> docs = this.commMenuMapper.selectCommunityDeptMenus();

            List<SearchRes> datas = this.converter.convertGroupResToSearchResList(docs);
            results.addAll(datas);

            return results;
        } else if ("GROUP".equals(menuDiv)) {
            List<GroupRes> groups = this.commuGrpService.getCommunityGroupMenus();
            return this.converter.convertGroupResToSearchResList(groups);
        }

        return null;
    }

    /**
     * 폴더 권한 체크
     */
    public String getCommunityFolderPermission(String menuCd, String folderGrp) {

        // 커뮤니티 소속부서 문서함일 경우 체크
        if ("DEPT".equals(folderGrp)) {
            return this.commuAuthService.getFolderCreatePermission(menuCd, null);
        }

        return PlatformConstant.YN_Y;
    }
}
