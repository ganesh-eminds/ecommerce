package com.matrix.ecommerce.product.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Service: Manage and Update Products",
                version = "1.0",
                description = "This service allows clients to manage products in the e-commerce platform. It includes endpoints for creating, uploading, retrieving, updating products, and checking stock availability for order processing.",
                contact = @Contact(
                        name = "Matrix E-Commerce Team",
                        email = "support@matrix-ecommerce.com",
                        url = "https://matrix-ecommerce.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class OpenApiConfig {
}
