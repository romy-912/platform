package com.romy.platform.main.department.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface CooperationNoticeMapper {

    void deleteCoopNoticeByBrdCd(@Param("brdCd") String brdCd);

    String selectCoopNoticeCode(@Param("brdCd") String brdCd);

    void mergeCoopNoticeForCommuNotice(@Param("noticeCd") String noticeCd, @Param("brdCd") String brdCd);
}
