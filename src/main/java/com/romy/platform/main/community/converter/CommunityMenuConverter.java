package com.romy.platform.main.community.converter;

import static com.romy.platform.main.community.dto.CommunityMenuDto.*;

import static com.romy.platform.main.community.dto.CommunityGroupDto.GroupRes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityMenuConverter {

    List<SearchRes> convertGroupResToSearchResList(List<GroupRes> dtos);

}
