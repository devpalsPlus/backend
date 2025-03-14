package hs.kr.backend.devpals.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                                .title("DevPals API Docs")
                                .description("DevPals 백엔드 API 문서")
                                .version("1.0")
                        )
                )
                .build();
    }

    /*
    // 기본 서버를 HTTPS로 설정
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevPals API Docs")
                        .description("DevPals API 문서")
                        .version("1.0")
                )
                .servers(List.of(
                        new Server().url("https://dev.devpals.site").description("Production Server")
                ));
    }
    */
}
