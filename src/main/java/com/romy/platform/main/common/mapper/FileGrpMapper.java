package com.romy.platform.main.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface FileGrpMapper {

    String selectMenuPathByFileGrp(@Param("fileGrp") String fileGrp);

}
