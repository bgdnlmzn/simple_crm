package ru.cft.crm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.dto.seller.SellerCreateRequest;
import ru.cft.crm.dto.seller.SellerResponse;
import ru.cft.crm.dto.seller.SellerUpdateRequest;
import ru.cft.crm.service.crud.SellerService;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/sellers")
    public SellerResponse createSeller(@RequestBody @Valid SellerCreateRequest body) {
        return sellerService.createSeller(body);
    }

    @GetMapping("/sellers")
    public List<SellerResponse> getAllActiveSellers() {
        return sellerService.getAllActiveSellers();
    }

    @GetMapping("/sellers/all")
    public List<SellerResponse> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @GetMapping("/sellers/{id}")
    public SellerResponse getSellerById(@PathVariable Long id) {
        return sellerService.getSeller(id);
    }

    @DeleteMapping("/sellers/{id}")
    public ResponseEntity<Void> deleteSellerById(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sellers/{id}")
    public SellerResponse updateSeller(
            @PathVariable Long id,
            @RequestBody @Valid SellerUpdateRequest body) {
        return sellerService.updateSeller(id, body);
    }
}
