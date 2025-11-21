package com.romy.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        SecurityScheme accessToken = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("accessToken");

        SecurityScheme refreshToken = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("refreshToken");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("accessToken").addList("refreshToken");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("accessToken", accessToken)
                        .addSecuritySchemes("refreshToken", refreshToken))
                .security(Collections.singletonList(securityRequirement))
                .info(this.apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Platform New Swagger")
                .version(System.getenv("REGISTRY_IMAGE_TAG") != null ? System.getenv("REGISTRY_IMAGE_TAG") : "local");
    }

    @Profile({"local", "stage"})
    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            SwaggerUiConfigProperties config,
            SwaggerUiOAuthProperties auth,
            SwaggerWelcomeCommon common,
            ObjectMapperProvider mapper) {
        return new SwaggerTransformer(config, auth, common, mapper);
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("-전체")
                .pathsToMatch("/**/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("공통(common)")
                .pathsToMatch("/common/**", "/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi communityApi() {
        return GroupedOpenApi.builder()
                .group("커뮤니티(community)")
                .pathsToMatch("/community/**", "/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi loginApi() {
        return GroupedOpenApi.builder()
                .group("로그인(login)")
                .pathsToMatch("/auth/**")
                .build();
    }

}
