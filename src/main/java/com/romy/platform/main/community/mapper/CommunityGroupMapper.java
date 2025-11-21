package com.romy.platform.main.community.mapper;

import static com.romy.platform.main.community.dto.CommunityGroupDto.*;

import com.romy.platform.main.community.dvo.GroupDvo;
import com.romy.platform.main.community.dvo.GroupPropertyDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CommunityGroupMapper {

    String selectGroupsPermission();

    List<GroupRes> selectCommunityGroupMenus();

    List<OrgTreeRes> selectOrganizationWithUserTree(@Param("deptCd") String deptCd);

    String selectGroupEditPermission(@Param("groupCd") String groupCd);

    List<SearchRes> selectCommunityGroups(@Param("groupCd") String groupCd);

    int selectCommunityGroupCount();

    int mergeCommunityGroup(GroupDvo dvo);

    long selectCommunityGroupFolderCnt(@Param("groupCd") String groupCd);

    void deleteGroupRootFolder(@Param("groupCd") String groupCd);
    int deleteCommunityGroup(@Param("groupCd") String groupCd);
    void deleteCommuGroupUserByGroupCd(@Param("groupCd") String groupCd);

    List<UserRes> selectGroupUsers(@Param("groupCd") String groupCd);

    int updateCommunityGroupUser(@Param("groupCd") String groupCd, @Param("usrCd") String usrCd, @Param("adminYn") String adminYn);
    void updateCommunityGroupAdminCnl(@Param("groupCd") String groupCd, @Param("usrCd") String usrCd);
    int selectCommuGroupAdminCount(@Param("groupCd") String groupCd);

    int selectGroupUserCount(@Param("groupCd") String groupCd, @Param("list") List<String> usrCds);

    int insertCommunityGroupUsers(@Param("groupCd") String groupCd, @Param("list") List<String> usrCds);
    int deleteCommunityGroupUser(@Param("groupCd") String groupCd, @Param("list") List<String> usrCds);

    GroupPropertyDvo selectCommunityGroupProperty(@Param("groupCd") String groupCd);

}
