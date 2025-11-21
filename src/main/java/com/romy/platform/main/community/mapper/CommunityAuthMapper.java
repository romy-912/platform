package com.romy.platform.main.community.mapper;

import static com.romy.platform.main.community.dto.CommunityAuthDto.*;

import static com.romy.platform.main.community.dto.CommunityGroupDto.GroupRes;
import com.romy.platform.main.community.dvo.AuthDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CommunityAuthMapper {

    String selectFolderCreatePermission(@Param("deptCd") String deptCd, @Param("usrCd") String usrCd);

    List<AuthRes> selectCommunityAuthorizations(@Param("folderFileCd") String folderFileCd);

    String selectAuthDeletePermission(@Param("folderFileCd") String folderFileCd, @Param("folderYn") String folderYn);
    void deleteCommunityAuth(@Param("folderFileCd") String folderFileCd, @Param("authCd") String authCd);

    String selectCommunityFolderAuth(@Param("folderCd") String folderCd);

    int insertCommunityAuths(@Param("folderFileCd") String folderFileCd, @Param("list") List<AuthDvo> dvos);

    String selectMyDocAuthCheck(@Param("folderCd") String folderCd);
    String selectGroupDocAuthCheck(@Param("sectorCd") String sectorCd, @Param("folderCd") String folderCd);

    List<GroupRes> selectCommunityDepts();
    String selectDeptDocSectorCd(@Param("folderCd") String folderCd);

    int selectRootFolderCnt(@Param("folderGrp") String folderGrp, @Param("sectorCd") String sectorCd);
    void insertRootFolderByCommunity(@Param("newFolderCd") String newFolderCd, @Param("folderGrp") String folderGrp
            , @Param("sectorCd") String sectorCd);

    List<String> selectCommuFolderAccessAuth(@Param("folderCd") String folderCd);

}
