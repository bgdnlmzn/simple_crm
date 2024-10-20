package ru.cft.crm.dto.error;

public record FieldError(
        String field,
        String errorMessage
) {
}
