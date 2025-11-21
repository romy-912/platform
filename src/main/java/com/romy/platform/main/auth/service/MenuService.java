package com.romy.platform.main.auth.service;

import static com.romy.platform.main.auth.dto.AuthLoginDto.MenuRes;
import com.romy.platform.main.auth.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MenuService {

    @Value("${domain}")
    private String domain;

    private final MenuMapper mapper;


    /**
     * 권한별 메뉴 리스트 조회
     */
    public List<MenuRes> getRoleMenus() {
        return this.mapper.selectRoleMenus(this.domain);
    }

}
