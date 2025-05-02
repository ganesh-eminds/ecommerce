package com.matrix.ecommerce.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-Commerce User Service API")
                .description("API documentation for the User Service")
                .version("1.0")
                .contact(new Contact()
                    .name("Your Name")
                    .email("your.email@example.com")
                    .url("https://your-portfolio.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")));
    }
}
