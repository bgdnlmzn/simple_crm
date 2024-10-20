package ru.cft.crm.dto.seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerCreateRequest(
        @NotBlank(message = "Имя продавца не должно быть пустым")
        @Size(min = 2, max = 50, message = "Имя продавца должно содержать от 2 до 50 символов")
        String sellerName,

        @NotBlank(message = "Контактная информация не должна быть пустой")
        @Size(min = 5, message = "Контактная информация должна содержать от 5 до 50 символов")
        @Email(message = "Контактная информация должна быть email адресом")
        String contactInfo
) {
}
