package ru.cft.crm.mapper;

import lombok.experimental.UtilityClass;
import ru.cft.crm.dto.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.dto.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.dto.seller.SellerCreateRequest;
import ru.cft.crm.dto.seller.SellerResponse;
import ru.cft.crm.entity.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SellerMapper {
    public static Seller mapDtoToSeller(SellerCreateRequest body) {
        Seller seller = new Seller();

        seller.setSellerName(body.sellerName());
        seller.setContactInfo(body.contactInfo());
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        seller.setIsActive(true);
        return seller;
    }

    public static SellerResponse mapSellerToDto(Seller seller) {
        return new SellerResponse(
                seller.getId(),
                seller.getSellerName(),
                seller.getContactInfo(),
                seller.getRegistrationDate(),
                seller.getUpdatedAt(),
                seller.getIsActive()
        );
    }

    public static SellerWithTransactionsResponse mapSellerWithTransactions(
            Seller seller, BigDecimal amount) {
        return new SellerWithTransactionsResponse(
                seller.getId(),
                seller.getSellerName(),
                seller.getContactInfo(),
                amount,
                seller.getRegistrationDate(),
                seller.getUpdatedAt(),
                seller.getIsActive()
        );
    }

    public static List<SellerResponse> mapSellersToResponses(List<Seller> sellers) {
        return sellers.stream()
                .map(SellerMapper::mapSellerToDto)
                .collect(Collectors.toList());
    }

    public static MostProductiveSellerResponse mapToMostProductiveSellerResponse(
            Seller seller,
            BigDecimal totalAmount
    ) {
        return new MostProductiveSellerResponse(seller.getId(),
                seller.getSellerName(),
                seller.getContactInfo(),
                totalAmount,
                seller.getRegistrationDate()
        );
    }
}
