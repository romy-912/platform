package com.romy.platform.main.auth.mapper;

import com.romy.platform.main.auth.dvo.LoginHistoryDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface LoginHistoryMapper {

    LoginHistoryDvo selectRecentLoginHistory(@Param("usrCd") String usrCd);

    int insertLoginHistory(LoginHistoryDvo dvo);

}
