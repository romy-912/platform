package com.romy.platform.common.provider;

import com.romy.platform.config.WebClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpClientProvider {
    private final WebClientConfig webClientConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * GET 요청
     *
     * @param url              요청 URL
     * @param responseDtoClass 응답 DTO 클래스
     * @param mediaType        요청/응답 MediaType
     * @param <T>              응답 DTO 타입
     * @return 응답 DTO 객체
     */
    public <T> T get(String url, Class<T> responseDtoClass, MediaType mediaType) {
        return webClientConfig.webClient().method(HttpMethod.GET)
                .uri(url)
                .accept(mediaType)
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }

    /**
     * GET 요청 (기본 MediaType: application/json)
     */
    public <T> T get(String url, Class<T> responseDtoClass) {
        return get(url, responseDtoClass, MediaType.APPLICATION_JSON);
    }

    /**
     * POST 요청
     *
     * @param url              요청 URL
     * @param requestDto       요청 DTO 객체
     * @param responseDtoClass 응답 DTO 클래스
     * @param mediaType        요청/응답 MediaType
     * @param <T>              응답 DTO 타입
     * @param <V>              요청 DTO 타입
     * @return 응답 DTO 객체
     */
    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass, MediaType mediaType) throws JsonProcessingException {
        return webClientConfig.webClient().method(HttpMethod.POST)
                .uri(url)
                .accept(mediaType)
                .bodyValue(objectMapper.writeValueAsString(requestDto))
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }

    /**
     * POST 요청 (기본 MediaType: application/json)
     */
    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass) throws JsonProcessingException {
        return post(url, requestDto, responseDtoClass, MediaType.APPLICATION_JSON);
    }

    /**
     * DELETE 요청
     *
     * @param url              요청 URL
     * @param responseDtoClass 응답 DTO 클래스
     * @param mediaType        요청/응답 MediaType
     * @param <T>              응답 DTO 타입
     * @return 응답 DTO 객체
     */
    public <T> T delete(String url, Class<T> responseDtoClass, MediaType mediaType) {
        return webClientConfig.webClient().method(HttpMethod.DELETE)
                .uri(url)
                .accept(mediaType)
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }
    /**
     * DELETE 요청 (기본 MediaType: application/json)
     */
    public <T> T delete(String url, Class<T> responseDtoClass) {
        return delete(url, responseDtoClass, MediaType.APPLICATION_JSON);
    }

    /**
     * PUT 요청
     *
     * @param url              요청 URL
     * @param requestDto       요청 DTO 객체
     * @param responseDtoClass 응답 DTO 클래스
     * @param mediaType        요청/응답 MediaType
     * @param <T>              응답 DTO 타입
     * @param <V>              요청 DTO 타입
     * @return 응답 DTO 객체
     */
    public <T, V> T put(String url, V requestDto, Class<T> responseDtoClass, MediaType mediaType) throws JsonProcessingException {
        return webClientConfig.webClient().method(HttpMethod.PUT)
                .uri(url)
                .accept(mediaType)
                .bodyValue(objectMapper.writeValueAsString(requestDto))
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }

    /**
     * PUT 요청 (기본 MediaType: application/json)
     */
    public <T, V> T put(String url, V requestDto, Class<T> responseDtoClass) throws JsonProcessingException {
        return put(url, requestDto, responseDtoClass, MediaType.APPLICATION_JSON);
    }


    /**
     * Multipart POST 요청
     *
     * @param url              요청 URL
     * @param builder          MultipartBodyBuilder 객체
     * @param responseDtoClass 응답 DTO 클래스
     * @param <T>              응답 DTO 타입
     * @return 응답 DTO 객체
     */
    public <T> T postMultipart(String url, MultipartBodyBuilder builder, Class<T> responseDtoClass) {
        return webClientConfig.webClient().method(HttpMethod.POST)
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }


}