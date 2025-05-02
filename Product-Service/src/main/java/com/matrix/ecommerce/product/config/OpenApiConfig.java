package com.matrix.ecommerce.product.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "My Spring Boot API",
                version = "1.0",
                description = "An example Spring Boot API with Swagger 3"
        )
)
public class OpenApiConfig {
}