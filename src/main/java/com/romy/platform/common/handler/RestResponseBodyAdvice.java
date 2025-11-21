package com.romy.platform.common.handler;

import com.romy.platform.common.response.RestResponse;
import com.romy.platform.common.response.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@Slf4j
@Component
@ControllerAdvice
public class RestResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public RestResponseBodyAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 모든 응답에 적용
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();

        if (this.shouldSkipWrap(body, httpRequest, httpResponse)) {
            return body;
        } else if (body instanceof byte[] image) {
            httpResponse.setContentLength(image.length);
            if (image.length > 0) {
                httpResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
            }
            return body;
        }

        SuccessResponse<Object> succRes = new SuccessResponse<>(httpRequest.getRequestURI(), body);

        if (body instanceof String) {
            try {
                // JSON 문자열로 변환
                String json = objectMapper.writeValueAsString(succRes);

                httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpResponse.setCharacterEncoding("UTF-8");

                return json;
            } catch (JsonProcessingException e) {
                log.debug(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return succRes;
    }

    private boolean shouldSkipWrap(Object body, HttpServletRequest request, HttpServletResponse response) {
        return body instanceof RestResponse ||
                response.getStatus() >= HttpStatus.BAD_REQUEST.value() ||
                request.getServletPath().startsWith("/v3");
    }
}
