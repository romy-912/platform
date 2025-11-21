package com.romy.platform.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SwaggerTransformer extends SwaggerIndexPageTransformer {

    public SwaggerTransformer(SwaggerUiConfigProperties swaggerUiConfig, SwaggerUiOAuthProperties swaggerUiOAuthProperties, SwaggerWelcomeCommon swaggerWelcomeCommon, ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        if (resource.toString().contains("index.html")) {
            final InputStream is = resource.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            try (BufferedReader br = new BufferedReader(isr)) {
                String html = br.lines().collect(Collectors.joining("\n"));

                String swaggerInitJs = """
                        <script>
                        window.onload = function() {
                            const ui = SwaggerUIBundle({
                                url: "",
                                dom_id: "#swagger-ui",
                                presets: [
                                  SwaggerUIBundle.presets.apis,
                                  SwaggerUIStandalonePreset
                                ],
                                plugins: [
                                  SwaggerUIBundle.plugins.DownloadUrl
                                ],
                                layout: "StandaloneLayout",
                                configUrl: "/api/v3/api-docs/swagger-config",
                                defaultModelsExpandDepth: "-1",
                                operationsSorter: "method",
                                validatorUrl: "",
                                // ----------------------
                                // 모든 요청에 헤더 적용
                                // ----------------------
                                requestInterceptor: function(request) {
                                    // localStorage에서 토큰 가져오기
                                    const accessToken = localStorage.getItem("swagger_auth_token_access");
                                    const refreshToken = localStorage.getItem("swagger_auth_token_refresh");
                        
                                    if (!request.headers) request.headers = {};
                        
                                    if (accessToken) {
                                        request.headers["accessToken"] = accessToken;
                                    }
                                    if (refreshToken) {
                                        request.headers["refreshToken"] = refreshToken;
                                    }
                        
                                    console.log("💡 요청 헤더 적용:", request.url, request.headers);
                                    return request;
                                },
                                // ----------------------
                                // 응답 후 interceptor
                                // ----------------------
                                responseInterceptor: function(response) {
                                    console.log("💡 API 응답 감지", response);
                        
                                    if (response.url.includes("/api/auth/token") && response.body) {
                                        try {
                                            const body = typeof response.body === "string" ? JSON.parse(response.body) : response.body;
                        
                                            if (body.data.accessToken) {
                                                localStorage.setItem("swagger_auth_token_access", body.data.accessToken);
                                                console.log("💾 accessToken 저장됨:", body.data.accessToken);
                                            }
                                            if (body.data.refreshToken) {
                                                localStorage.setItem("swagger_auth_token_refresh", body.data.refreshToken);
                                                console.log("💾 refreshToken 저장됨:", body.data.refreshToken);
                                            }
                        
                                        } catch (e) {
                                            console.warn("⚠️ response.body JSON 파싱 실패", e);
                                        }
                                    }
                        
                                    return response;
                                }
                            });
                        
                            window.ui = ui;
                        };
                        </script>
                        """;

                String modifiedHtml = html.replaceAll(
                        "<script src=\"\\.\\/swagger-initializer\\.js\" charset=\"UTF-8\">\\s*</script>",
                        "<script src=\"./swagger-initializer.js\" charset=\"UTF-8\"></script>\n" +
                                swaggerInitJs
                );


                return new TransformedResource(resource, modifiedHtml.getBytes(StandardCharsets.UTF_8));
            } // AutoCloseable br > isr > is
        }
        return super.transform(request, resource, transformerChain);
    }
}