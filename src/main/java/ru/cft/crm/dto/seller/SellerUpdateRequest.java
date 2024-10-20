package ru.cft.crm.dto.seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record SellerUpdateRequest(
        @Size(min = 2, max = 50, message = "Имя продавца должно содержать от 2 до 50 символов")
        String sellerName,

        @Size(min = 5, message = "Контактная информация должна содержать от 5 до 50 символов")
        @Email(message = "Контактная информация должна быть email адресом")
        String contactInfo
) {
}
