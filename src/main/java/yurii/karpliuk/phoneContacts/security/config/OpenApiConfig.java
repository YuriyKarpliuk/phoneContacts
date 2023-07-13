package yurii.karpliuk.phoneContacts.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Yura",
                        email = "yurakarpliuk19062003@gmail.com"
                ),
                description = "API documentation for managing contacts and their info(phone numbers,email,names,image)",
                title = "PhoneContact API - Yura",
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        description = "Local development server",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name="bearerAuth"
                )
        }

)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
