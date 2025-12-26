package ru.cft.crm.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.cft.crm.model.error.ErrorResponse;
import ru.cft.crm.model.error.FieldError;
import ru.cft.crm.model.error.ValidationErrorResponse;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Seller Analytics API")
                        .version("1.0")
                        .description("API для системы аналитики продавцов и транзакций с JWT аутентификацией")
                        .contact(new Contact()
                                .name("Support")
                                .email("support@example.com")))
                .addTagsItem(new Tag().name("Authentication").description("API для аутентификации через LDAP"))
                .addTagsItem(new Tag().name("Analytics").description("API для аналитики"))
                .addTagsItem(new Tag().name("Sellers").description("API для управления продавцами"))
                .addTagsItem(new Tag().name("Transactions").description("API для управления транзакциями"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT токен, полученный через /api/auth/login"))
                        .addSchemas("ErrorResponse", new Schema<ErrorResponse>()
                                .type("object")
                                .addProperty("status", new Schema<Integer>().type("integer").example(400))
                                .addProperty("message", new Schema<String>().type("string").example("Ошибка валидации"))
                                .addProperty("timestamp", new Schema<String>().type("string").example("2023-12-01T10:00:00")))
                        .addSchemas("ValidationErrorResponse", new Schema<ValidationErrorResponse>()
                                .type("object")
                                .addProperty("status", new Schema<Integer>().type("integer").example(400))
                                .addProperty("message", new Schema<String>().type("string").example("Ошибка валидации"))
                                .addProperty("timestamp", new Schema<String>().type("string").example("2023-12-01T10:00:00"))
                                .addProperty("errors", new ArraySchema().items(new Schema<FieldError>()
                                        .type("object")
                                        .addProperty("field", new Schema<String>().type("string").example("sellerName"))
                                        .addProperty("message", new Schema<String>().type("string").example("Имя продавца не должно быть пустым"))))));
    }
}
