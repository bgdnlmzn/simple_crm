package ru.cft.crm.dto.seller;

import java.time.LocalDateTime;

public record SellerResponse(
        Long id,
        String sellerName,
        String contactInfo,
        LocalDateTime registrationDate,
        LocalDateTime updatedAt,
        Boolean isActive
) {
}
