package com.romy.platform.main.auth.mapper;

import com.romy.platform.main.auth.dvo.AccessHisDvo;
import com.romy.platform.main.auth.dvo.AuthPermissionDvo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RoleMapper {

    List<AccessHisDvo> selectPermittedMenu(AuthPermissionDvo dvo);

    void insertAccessHis(AccessHisDvo dvo);

}
