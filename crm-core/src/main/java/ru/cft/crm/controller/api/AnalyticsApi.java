package ru.cft.crm.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.model.analitycs.BestPeriodsResponse;
import ru.cft.crm.model.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.model.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.model.error.ErrorResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Analytics", description = "API для аналитики продавцов и транзакций")
public interface AnalyticsApi {

    @Operation(
            summary = "Получить самых продуктивных продавцов",
            description = "Возвращает список продавцов отсортированных по продуктивности за указанный период",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Данные не найдены",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/most-productive")
    List<MostProductiveSellerResponse> getMostProductiveSeller(
            @Parameter(description = "Базовая дата для расчета периода", required = true, example = "2023-12-01")
            @RequestParam LocalDate date,

            @Parameter(description = "Тип периода: day, week, month, year", required = true,
                    schema = @Schema(allowableValues = {"day", "week", "month", "year"}))
            @RequestParam String period,

            @Parameter(description = "Фильтр по активности продавца")
            @RequestParam Boolean active
    );

    @Operation(
            summary = "Получить продавцов с транзакциями меньше указанной суммы",
            description = "Возвращает список продавцов, у которых сумма транзакций за период меньше указанной",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Данные не найдены",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/less-then")
    List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            @Parameter(description = "Максимальная сумма транзакции", required = true, example = "1000.00")
            @RequestParam BigDecimal amount,

            @Parameter(description = "Дата начала периода", required = true, example = "2023-12-01")
            @RequestParam LocalDate start,

            @Parameter(description = "Дата окончания периода", required = true, example = "2023-12-31")
            @RequestParam LocalDate end,

            @Parameter(description = "Фильтр по активности продавца")
            @RequestParam Boolean active
    );

    @Operation(
            summary = "Получить лучшие периоды для продавца",
            description = "Возвращает информацию о лучших периодах продаж для указанного продавца",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "404", description = "Продавец не найден",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/best-periods/seller/{id}")
    BestPeriodsResponse getBestTransactionPeriod(
            @Parameter(description = "ID продавца", required = true, example = "1")
            @PathVariable Long id
    );
}
