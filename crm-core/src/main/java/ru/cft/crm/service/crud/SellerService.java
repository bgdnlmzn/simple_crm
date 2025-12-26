package ru.cft.crm.service.crud;

import ru.cft.crm.model.seller.SellerCreateRequest;
import ru.cft.crm.model.seller.SellerResponse;
import ru.cft.crm.model.seller.SellerUpdateRequest;

import java.util.List;

public interface SellerService {
    SellerResponse createSeller(SellerCreateRequest body);

    SellerResponse getSeller(Long sellerId);

    List<SellerResponse> getAllActiveSellers();

    List<SellerResponse> getAllSellers();

    SellerResponse updateSeller(Long sellerId, SellerUpdateRequest body);

    void deleteSeller(Long sellerId);
}
