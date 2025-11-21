package com.romy.platform.main.auth.mapper;

import com.romy.platform.main.auth.dvo.AuthUserDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserMapper {

    AuthUserDvo selectUserInfoByToken(@Param("usrId") String usrId);

}
