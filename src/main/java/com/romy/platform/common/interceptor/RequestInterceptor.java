package com.romy.platform.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;



@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 처리 시간 계산
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        response.setHeader("X-Request-Id", requestId);

        log.info("■■■■■■■■■■■■■■■■■■■■■■■ Request Start [{}] {} {} ■■■■■■■■■■■■■■■■■■■■■■■"
                , requestId, request.getMethod(), request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        String requestId = MDC.get("requestId");

        log.info("■■■■■■■■■■■■■■■■■■■■■■■ Request End [{}] executeTime={}ms status={} ■■■■■■■■■■■■■■■■■■■■■■■",
                requestId, executeTime, response.getStatus() );

        MDC.clear();
    }
}
