package com.romy.platform.main.community.mapper;

import static com.romy.platform.main.community.dto.CommunityOfficialFormDto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;



@Mapper
public interface CommunityOfficialFormMapper {

    List<CategoryRes> selectOfficialFormCategory();

    List<SearchRes> selectOfficialForms(@Param("ctgCd") String ctgCd);

}
