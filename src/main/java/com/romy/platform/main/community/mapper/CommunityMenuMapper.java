package com.romy.platform.main.community.mapper;

import static com.romy.platform.main.community.dto.CommunityMenuDto.*;

import static com.romy.platform.main.community.dto.CommunityGroupDto.GroupRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface CommunityMenuMapper {

    List<SearchRes> selectCommunityMenus();

    List<GroupRes> selectCommunityDeptMenus();
}
