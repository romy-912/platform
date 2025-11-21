package com.romy.platform.main.common.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InGenerateMapper {

    String selectSeqNextId();

    void updateSeqNextId();

}
