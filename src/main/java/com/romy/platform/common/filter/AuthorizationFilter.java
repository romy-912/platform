package com.romy.platform.common.filter;

import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.auth.service.AuthRoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Order(2)
@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final AuthRoleService authRoleService;

    private final List<String> EXCLUDE_URL = List.of("/swagger-ui", "/v3", "/auth/check", "/auth/token", "/actuator");


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        AuthUserDvo dvo = PlatformUtil.getUserInfo();
        if (dvo == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String usrCd = dvo.getUsrCd();
        String uri = request.getServletPath();
        String method = request.getMethod();

        boolean isPermission = this.authRoleService.hasPermissionForApi(usrCd, uri, method);
        if (!isPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return EXCLUDE_URL.stream().anyMatch(exclude -> (exclude.equalsIgnoreCase(path))
                || path.startsWith(exclude));
    }

}
