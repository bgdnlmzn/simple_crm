package ru.cft.crm.auth.ldap.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на аутентификацию")
public class AuthenticationRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Имя пользователя", example = "john.doe")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Пароль", example = "password123")
    private String password;
}

