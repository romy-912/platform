package com.romy.platform.config;

import com.romy.platform.common.filter.AuthorizationFilter;
import com.romy.platform.common.token.JwtAuthEntryPoint;
import com.romy.platform.common.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthorizationFilter authFilter;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(this.corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headersConfigurer ->
                    headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeRequest ->
                    authorizeRequest
                            // swagger, login uri 권한 허용
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")
                                    , AntPathRequestMatcher.antMatcher("/swagger-ui/**")
                                    , AntPathRequestMatcher.antMatcher("/v3/**")
                                    , AntPathRequestMatcher.antMatcher("/auth/check")
                                    , AntPathRequestMatcher.antMatcher("/auth/token")
                                    , AntPathRequestMatcher.antMatcher("/actuator/**")
                            )
                            .permitAll()
                            .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint(this.jwtAuthEntryPoint))
            .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(this.authFilter, JwtAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) ->
                web.ignoring()
                   .requestMatchers("/swagger-ui.html",
                           "/swagger-ui/**",
                           "/v3/**",
                           "/auth/check",
                           "/auth/token",
                           "/actuator/**");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Arrays.asList(
                    "Content-Type",
                    "Accept",
                    "Range",
                    "X-Requested-With",
                    "X-Request-Id",
                    "accessToken",
                    "refreshToken"
            ));
            config.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "PATCH"));
            // 허용할 origin
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setExposedHeaders(Arrays.asList(
                    "Content-Disposition",
                    "Content-Type",
                    "Content-Length",
                    "X-Request-Id"
            ));
            return config;
        };
    }
}
