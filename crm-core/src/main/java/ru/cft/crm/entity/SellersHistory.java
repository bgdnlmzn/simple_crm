package ru.cft.crm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.cft.crm.type.ChangeType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sellers_history")
public class SellersHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String sellerName;

    private String contactInfo;

    private LocalDateTime registrationDate;

    private LocalDateTime updatedAt;

    private LocalDateTime changeTimestamp;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
}
