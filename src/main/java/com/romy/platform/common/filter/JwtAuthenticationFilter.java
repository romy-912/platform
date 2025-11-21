package com.romy.platform.common.filter;

import com.romy.platform.common.token.JwtAuthService;
import com.romy.platform.common.token.JwtTokenProvider;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;


@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final JwtAuthService jwtAuthService;

    private final List<String> EXCLUDE_URL = List.of("/swagger-ui", "/v3", "/auth/check", "/auth/token", "/actuator");


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {

        final String accessToken = request.getHeader("accessToken");
        final String refreshToken = request.getHeader("refreshToken");

        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(refreshToken)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // access token 만료체크
        try {
            boolean isExpired = this.jwtProvider.isTokenExpired(accessToken);
            if (!isExpired && this.jwtProvider.isValidToken(accessToken)) {
                this.jwtProvider.setAuthentication(accessToken);
                response.setHeader("accessToken", accessToken);
                filterChain.doFilter(request, response);
                return;
            }

            isExpired = this.jwtProvider.isTokenExpired(refreshToken);
            // refresh token 만료
            if (isExpired || !this.jwtProvider.isValidToken(refreshToken)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            String usrId = this.jwtProvider.extUsrId(refreshToken);
            AuthUserDvo dvo = this.jwtAuthService.loadUserByUsername(usrId);
            if (dvo == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            // accessToken 재발급
            String newAccessToken = this.jwtProvider.createAccessToken(dvo);
            this.jwtProvider.setAuthentication(newAccessToken);
            response.setHeader("accessToken", newAccessToken);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return EXCLUDE_URL.stream().anyMatch(exclude -> (exclude.equalsIgnoreCase(path))
                || path.startsWith(exclude));
    }
}
