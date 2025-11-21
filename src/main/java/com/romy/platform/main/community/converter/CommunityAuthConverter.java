package com.romy.platform.main.community.converter;

import static com.romy.platform.main.community.dto.CommunityAuthDto.*;
import com.romy.platform.main.community.dvo.AuthDvo;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CommunityAuthConverter {

    List<AuthDvo> commuAuthToAuthDvoList(List<CommuAuth> dtos);
}
