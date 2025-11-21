package com.romy.platform.main.auth.mapper;

import static com.romy.platform.main.auth.dto.AuthLoginDto.MenuRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MenuMapper {

    List<MenuRes> selectRoleMenus(@Param("domain") String domain);
}
