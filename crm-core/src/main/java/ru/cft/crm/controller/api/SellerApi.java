package ru.cft.crm.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.model.error.ErrorResponse;
import ru.cft.crm.model.error.ValidationErrorResponse;
import ru.cft.crm.model.seller.SellerCreateRequest;
import ru.cft.crm.model.seller.SellerResponse;
import ru.cft.crm.model.seller.SellerUpdateRequest;

import java.util.List;

@Tag(name = "Sellers", description = "API для управления продавцами")
public interface SellerApi {

    @Operation(
            summary = "Создать нового продавца",
            description = "Создает нового продавца в системе. Требуется роль ADMIN или право WRITE_SELLERS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Продавец успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Продавец уже существует",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'WRITE_SELLERS')")
    @PostMapping("/sellers")
    SellerResponse createSeller(@RequestBody @Valid SellerCreateRequest body);

    @Operation(
            summary = "Получить всех активных продавцов",
            description = "Возвращает список всех активных продавцов. Требуется авторизация",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sellers")
    List<SellerResponse> getAllActiveSellers();

    @Operation(
            summary = "Получить всех продавцов",
            description = "Возвращает список всех продавцов (включая неактивных). Требуется авторизация",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sellers/all")
    List<SellerResponse> getAllSellers();

    @Operation(
            summary = "Получить продавца по ID",
            description = "Возвращает информацию о продавце по его идентификатору. Требуется авторизация",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Продавец не найден",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sellers/{id}")
    SellerResponse getSellerById(
            @Parameter(description = "ID продавца", required = true, example = "1")
            @PathVariable Long id);

    @Operation(
            summary = "Удалить продавца",
            description = "Удаляет продавца по идентификатору. Требуется роль ADMIN",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Продавец успешно удален"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Продавец не найден",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/sellers/{id}")
    ResponseEntity<Void> deleteSellerById(
            @Parameter(description = "ID продавца", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(
            summary = "Обновить данные продавца",
            description = "Обновляет информацию о продавце. Требуется роль ADMIN или право WRITE_SELLERS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные успешно обновлены"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Продавец не найден",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Конфликт данных",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'WRITE_SELLERS')")
    @PatchMapping("/sellers/{id}")
    SellerResponse updateSeller(
            @Parameter(description = "ID продавца", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody @Valid SellerUpdateRequest body
    );
}
