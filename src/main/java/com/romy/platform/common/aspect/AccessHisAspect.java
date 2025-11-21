package com.romy.platform.common.aspect;

import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AccessHisDvo;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.auth.service.AuthRoleService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;


@Aspect
@Component
@RequiredArgsConstructor
public class AccessHisAspect {

    private final AuthRoleService authRoleService;

    private final List<String> EXCLUDE_URL = List.of("/v3", "/swagger-ui", "/actuator", "/auth/check", "/auth/token");

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    @Before("restControllerMethods()")
    public void logRestControllerRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        String uri = request.getServletPath();

        // exclude api 관련 요청 skip
        if (this.isExclude(request)) return;

        String method = request.getMethod();
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        String usrCd = userDvo.getUsrCd();

        // 접근 메뉴정보
        AccessHisDvo dvo = this.authRoleService.getPermittedMenu(usrCd, uri, method);
        if (dvo == null) return;

        dvo.setAccessIp(this.getAccessIp(request));
        dvo.setRegUsrDept(userDvo.getDeptNm());

        // accessHistory 생성
        this.authRoleService.createAccessHis(dvo);
    }

    private boolean isExclude(HttpServletRequest request) {
        String path = request.getServletPath();

        return EXCLUDE_URL.stream().anyMatch(exclude -> (exclude.equalsIgnoreCase(path))
                || path.startsWith(exclude));
    }

    private String getAccessIp(HttpServletRequest request) {
        String accessIp = request.getHeader("X-FORWARDED-FOR");
        if (StringUtils.isBlank(accessIp)) {
            accessIp = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isBlank(accessIp)) {
            accessIp = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isBlank(accessIp)) {
            accessIp = request.getRemoteAddr();
        }
        return accessIp;
    }

}
