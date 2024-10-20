package ru.cft.crm.dto.utilis;

import java.time.LocalDateTime;

public record DateRange(
        LocalDateTime start,
        LocalDateTime end
) {
}
