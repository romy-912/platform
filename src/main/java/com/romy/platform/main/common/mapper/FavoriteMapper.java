package com.romy.platform.main.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface FavoriteMapper {

    void insertFavorite(@Param("fCd") String fCd, @Param("div") String div, @Param("keyCd") String keyCd);

    int deleteFavorite(@Param("fCd") String fCd);

}
