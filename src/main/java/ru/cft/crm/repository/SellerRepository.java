package ru.cft.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cft.crm.entity.Seller;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    List<Seller> findByIsActiveTrue();

    Optional<Seller> findByIdAndIsActiveTrue(Long id);

    Boolean existsByContactInfo(String contactInfo);
}
