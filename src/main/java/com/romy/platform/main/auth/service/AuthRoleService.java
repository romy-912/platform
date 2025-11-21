package com.romy.platform.main.auth.service;


import com.romy.platform.annotation.PlatformTransactional;

import com.romy.platform.common.provider.RedisProvider;
import com.romy.platform.main.auth.dvo.AccessHisDvo;
import com.romy.platform.main.auth.dvo.AuthPermissionDvo;

import com.romy.platform.main.auth.mapper.RoleMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthRoleService {

    private final RoleMapper roleMapper;


    /**
     * api 인가 처리
     */
    public boolean hasPermissionForApi(String usrCd, String uri, String method) {

        List<AccessHisDvo> dvos = this.getPermittedMenus(usrCd, method);
        AntPathMatcher matcher = new AntPathMatcher();

        for (AccessHisDvo hisDvo : dvos) {
            if (matcher.match(hisDvo.getProgramUrl(), uri)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 권한 메뉴 조회 (15분 캐싱)
     */
    public List<AccessHisDvo> getPermittedMenus(String usrCd, String method) {

        String redisKey = "auth:menu:" + usrCd + ":" + method;

        List<AccessHisDvo> datas = RedisProvider.getRedisValue(redisKey, new TypeReference<>() {});
        if (CollectionUtils.isNotEmpty(datas)) return datas;

        AuthPermissionDvo dvo = new AuthPermissionDvo();
        dvo.setUsrCd(usrCd);
        dvo.setMethod(method);

        datas = this.roleMapper.selectPermittedMenu(dvo);
        RedisProvider.setRedisValue(redisKey, datas, 15L);

        return datas;
    }

    /**
     * 접근 메뉴 정보 조회
     */
    public AccessHisDvo getPermittedMenu(String usrCd, String uri, String method) {

        List<AccessHisDvo> dvos = this.getPermittedMenus(usrCd, method);

        AntPathMatcher matcher = new AntPathMatcher();

        for (AccessHisDvo hisDvo : dvos) {
            if (matcher.match(hisDvo.getProgramUrl(), uri)) {
                return hisDvo;
            }
        }

        return null;
    }

    /**
     * 접속이력 생성
     */
    @PlatformTransactional
    public void createAccessHis(AccessHisDvo dvo) {
        this.roleMapper.insertAccessHis(dvo);
    }


}
