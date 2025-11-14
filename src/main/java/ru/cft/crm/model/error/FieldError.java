package ru.cft.crm.model.error;

public record FieldError(
        String field,
        String errorMessage
) {
}
