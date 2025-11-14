package ru.cft.crm.model.seller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на обновление продавца")
public record SellerUpdateRequest(
        @Schema(description = "Имя продавца", example = "Иван Иванов", minLength = 2, maxLength = 50)
        @Size(min = 2, max = 50, message = "Имя продавца должно содержать от 2 до 50 символов")
        String sellerName,

        @Schema(description = "Контактная информация (email)", example = "seller@example.com", minLength = 5, maxLength = 50)
        @Size(min = 5, max = 50, message = "Контактная информация должна содержать от 5 до 50 символов")
        @Email(message = "Контактная информация должна быть email адресом")
        String contactInfo
) {}
