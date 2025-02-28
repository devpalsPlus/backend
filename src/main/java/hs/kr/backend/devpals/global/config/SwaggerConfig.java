package hs.kr.backend.devpals.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi boardGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("DevPalse")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.setInfo(new Info()
                                .title("DevPals Api Docs")
                        )
                )
                .build();
    }
}