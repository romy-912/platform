package com.romy.platform.main.common.mapper;

import com.romy.platform.main.common.dvo.TrashDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface TrashMapper {

    void insertTrashs(@Param("list") List<TrashDvo> dvos);
}
