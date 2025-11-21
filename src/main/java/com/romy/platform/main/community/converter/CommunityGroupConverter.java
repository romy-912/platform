package com.romy.platform.main.community.converter;

import static com.romy.platform.main.community.dto.CommunityGroupDto.*;

import com.romy.platform.main.community.dvo.GroupDvo;
import com.romy.platform.main.community.dvo.GroupPropertyDvo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityGroupConverter {

    @Mapping(target = "groupNm", source = "dto.groupNm")
    @Mapping(target = "ordNum", source = "dto.ordNum")
    GroupDvo dataToGroupDvo(String groupCd, UpdateReq dto);

    GroupDvo createReqToGroupDvo(CreateReq dto);

    SaveUserReq dataToSaveUserReq(String groupCd, List<String> userCds);

    PropRes groupPropertyDvoToPropRes(GroupPropertyDvo dvo);
}
