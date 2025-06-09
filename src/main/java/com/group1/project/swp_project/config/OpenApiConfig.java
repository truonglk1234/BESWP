package com.group1.project.swp_project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(new Info().title("STI Health API").version("1.0").description("API documentation for the STI Health application"))

                // Đảm bảo danh sách này khớp 100% với tên trong @Tag
                .tags(List.of(
                        new Tag().name("1. Authentication").description("APIs for User Registration, Login, and Verification"),
                        new Tag().name("2. Management (Admin)").description("APIs for Admin to manage user accounts"),
                        new Tag().name("3. Management (Manager)").description("APIs for Manager to manage user accounts")
                        // Bạn có thể thêm các tag khác ở đây
                ))

                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Enter JWT token")
                                )
                );
    }
}