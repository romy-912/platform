package com.romy.platform.main.common.mapper;

import com.romy.platform.main.common.dvo.CodeDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CodeMapper {

    List<CodeDvo> selectCommonCode(@Param("parentCd") String parentCd);
}
