package ru.cft.crm.auth.ldap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import ru.cft.crm.auth.ldap.model.AuthenticationResponse;
import ru.cft.crm.auth.security.jwt.JwtTokenProvider;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Profile("dev")
@Tag(name = "Development", description = "API для разработки (доступно только в dev профиле)")
public class DevAuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "Получить тестовый JWT токен",
            description = "Генерирует JWT токен с указанными authorities без проверки через LDAP. " +
                    "Используется только для разработки и тестирования."
    )
    @PostMapping("/token")
    public AuthenticationResponse getTestToken(
            @Parameter(description = "Имя пользователя", example = "testuser")
            @RequestParam String username,
            @Parameter(description = "Список authorities через запятую", 
                    example = "ROLE_ADMIN,READ_SELLERS,WRITE_SELLERS")
            @RequestParam(required = false) String authorities) {

        List<String> authList;
        if (authorities != null && !authorities.isEmpty()) {
            authList = Arrays.asList(authorities.split(","));
        } else {
            authList = List.of("ROLE_USER", "READ_SELLERS");
        }

        log.info("Generating test token for user: {} with authorities: {}", username, authList);

        String token = jwtTokenProvider.createToken(username, authList);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(username)
                .authorities(authList)
                .build();
    }

    @Operation(
            summary = "Получить токен администратора",
            description = "Генерирует JWT токен с правами администратора"
    )
    @PostMapping("/token/admin")
    public AuthenticationResponse getAdminToken(
            @Parameter(description = "Имя пользователя", example = "admin")
            @RequestParam(defaultValue = "admin") String username) {

        List<String> authList = List.of("ROLE_ADMIN", "READ_SELLERS", "WRITE_SELLERS", 
                "READ_TRANSACTIONS", "WRITE_TRANSACTIONS");

        log.info("Generating admin token for user: {}", username);

        String token = jwtTokenProvider.createToken(username, authList);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(username)
                .authorities(authList)
                .build();
    }

    @Operation(
            summary = "Получить токен обычного пользователя",
            description = "Генерирует JWT токен с базовыми правами пользователя (только чтение)"
    )
    @PostMapping("/token/user")
    public AuthenticationResponse getUserToken(
            @Parameter(description = "Имя пользователя", example = "user")
            @RequestParam(defaultValue = "user") String username) {

        List<String> authList = List.of("ROLE_USER", "READ_SELLERS", "READ_TRANSACTIONS");

        log.info("Generating user token for user: {}", username);

        String token = jwtTokenProvider.createToken(username, authList);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(username)
                .authorities(authList)
                .build();
    }
}

