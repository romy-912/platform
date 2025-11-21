package com.romy.platform.common.aspect;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Objects;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private final List<String> EXCLUDE_URL = List.of("/v3", "/swagger-ui", "/actuator");

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    @Before("restControllerMethods()")
    public void logRestControllerRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        // multipart 요청은 제외
        if (this.isMultipart(request)) return;

        String uri = request.getRequestURI();
        // exclude api 관련 요청 skip
        if (this.isExclude(request)) return;

        String method = request.getMethod();

        log.info("┌─────────────── [{}] {} ────────────────", method, uri);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String paramName = paramNames[i];

            // 제외
            if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                continue;
            }

            if (this.isRecord(arg)) {
                this.logRecordFields(arg);
            } else {
                log.info("│ {{}} = {}", paramName, Objects.toString(arg, "null"));
            }
        }

        log.info("└──────────────────────────────────────────────────────────────");
    }

    private boolean isExclude(HttpServletRequest request) {
        String path = request.getServletPath();

        return EXCLUDE_URL.stream().anyMatch(exclude -> (exclude.equalsIgnoreCase(path))
                || path.startsWith(exclude));
    }

    private boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    private boolean isRecord(Object obj) {
        return obj != null && obj.getClass().isRecord();
    }

    private void logRecordFields(Object recordObj) {
        Class<?> clazz = recordObj.getClass();
        if (!clazz.isRecord()) return;

        for (RecordComponent component : clazz.getRecordComponents()) {
            try {
                Method accessor = component.getAccessor();
                Object value = accessor.invoke(recordObj);
                log.info("│ {{}} = {}", component.getName(), Objects.toString(value, "null"));
            } catch (Exception e) {
                log.warn("│ [!] Failed to read record field: {}", component.getName(), e);
            }
        }
    }
}
