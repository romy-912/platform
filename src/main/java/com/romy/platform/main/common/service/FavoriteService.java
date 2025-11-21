package com.romy.platform.main.common.service;


import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.main.common.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper mapper;

    private final IdGenerateService idGenService;


    /**
     * 즐겨찾기 등록
     */
    @PlatformTransactional
    public String createFavorite(String div, String keyCd) {
        // 필수 체크
        MessageProvider.checkNotNullData(div, PlatformConstant.FAVORITE_DIV);
        MessageProvider.checkNotNullData(keyCd, PlatformConstant.FAVORITE_KEY_CD);

        String fCd = this.idGenService.getNextStringId();

        this.mapper.insertFavorite(fCd, div, keyCd);

        return fCd;
    }

    /**
     * 즐겨찾기 삭제
     */
    @PlatformTransactional
    public int removeFavorite(String fCd) {
        // 필수 체크
        MessageProvider.checkNotNullData(fCd, PlatformConstant.FAVORITE_CD);

        return this.mapper.deleteFavorite(fCd);
    }

}
