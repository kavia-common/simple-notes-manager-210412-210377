package com.example.notes.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration metadata for Notes API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Notes CRUD API",
                version = "1.0.0",
                description = "GxP-ready Notes management API with audit trail and RBAC stubs.",
                contact = @Contact(name = "Notes API Team", email = "support@example.com")
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        },
        tags = {
                @Tag(name = "Notes", description = "CRUD operations for notes"),
                @Tag(name = "System", description = "System endpoints and docs")
        }
)
@SecurityScheme(
        name = "X-Role",
        type = SecuritySchemeType.APIKEY,
        paramName = "X-Role",
        description = "RBAC role header (USER/ADMIN)",
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER
)
@SecurityScheme(
        name = "X-User-Id",
        type = SecuritySchemeType.APIKEY,
        paramName = "X-User-Id",
        description = "User identifier used for audit trail",
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
