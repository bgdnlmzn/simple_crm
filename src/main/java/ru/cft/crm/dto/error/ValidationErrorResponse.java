package ru.cft.crm.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {
}
