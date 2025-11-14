package ru.cft.crm.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
import ru.cft.crm.model.transaction.TransactionCreateRequest;
import ru.cft.crm.model.transaction.TransactionResponse;
import ru.cft.crm.model.transaction.TransactionUpdateRequest;

import java.util.List;

@Tag(name = "Transactions", description = "API для управления транзакциями")
public interface TransactionApi {

    @Operation(
            summary = "Создать транзакцию",
            description = "Создает новую транзакцию для продавца",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Транзакция успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Продавец не найден",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/transactions")
    TransactionResponse createTransaction(@RequestBody @Valid TransactionCreateRequest body);

    @Operation(
            summary = "Получить транзакцию по ID",
            description = "Возвращает информацию о транзакции по идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/transactions/{transactionId}")
    TransactionResponse getTransaction(
            @Parameter(description = "ID транзакции", required = true, example = "1")
            @PathVariable Long transactionId);

    @Operation(
            summary = "Получить все транзакции продавца",
            description = "Возвращает все транзакции указанного продавца",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "404", description = "Продавец не найден",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/transactions/seller/{sellerId}")
    List<TransactionResponse> getAllSellersTransactions(
            @Parameter(description = "ID продавца", required = true, example = "1")
            @PathVariable Long sellerId);

    @Operation(
            summary = "Получить все активные транзакции",
            description = "Возвращает список всех активных транзакций",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос")
            }
    )
    @GetMapping("/transactions")
    List<TransactionResponse> getAllActiveTransactions();

    @Operation(
            summary = "Получить все транзакции",
            description = "Возвращает список всех транзакций (включая неактивные)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос")
            }
    )
    @GetMapping("/transactions/all")
    List<TransactionResponse> getAllTransactions();

    @Operation(
            summary = "Обновить транзакцию",
            description = "Обновляет информацию о транзакции",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Транзакция успешно обновлена"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/transactions/{transactionId}")
    TransactionResponse updateTransaction(
            @Parameter(description = "ID транзакции", required = true, example = "1")
            @PathVariable Long transactionId,
            @RequestBody @Valid TransactionUpdateRequest body);

    @Operation(
            summary = "Удалить транзакцию",
            description = "Удаляет транзакцию по идентификатору",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Транзакция успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/transactions/{transactionId}")
    ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "ID транзакции", required = true, example = "1")
            @PathVariable Long transactionId
    );
}
