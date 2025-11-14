package ru.cft.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.controller.api.SellerApi;
import ru.cft.crm.model.seller.SellerCreateRequest;
import ru.cft.crm.model.seller.SellerResponse;
import ru.cft.crm.model.seller.SellerUpdateRequest;
import ru.cft.crm.service.crud.SellerService;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SellerController implements SellerApi {

    private final SellerService sellerService;

    @Override
    public SellerResponse createSeller(SellerCreateRequest body) {
        return sellerService.createSeller(body);
    }

    @Override
    public List<SellerResponse> getAllActiveSellers() {
        return sellerService.getAllActiveSellers();
    }

    @Override
    public List<SellerResponse> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @Override
    public SellerResponse getSellerById(Long id) {
        return sellerService.getSeller(id);
    }

    @Override
    public ResponseEntity<Void> deleteSellerById(Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public SellerResponse updateSeller(Long id, SellerUpdateRequest body) {
        return sellerService.updateSeller(id, body);
    }
}
