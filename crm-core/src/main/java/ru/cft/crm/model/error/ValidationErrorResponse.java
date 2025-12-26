package ru.cft.crm.model.error;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {
}
