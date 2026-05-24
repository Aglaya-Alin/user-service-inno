package com.example.user_service_inno.entity;


import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "payment_cards")
public class PaymentCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Column(nullable = false)
    private Long number;

    @Setter
    @Column(nullable = false)
    private String holder;

    @Setter
    @Column(nullable = false, name = "expiration_date")
    private Instant expirationDate;

    @Setter
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(updatable = false, name = "created_at", nullable = false)
    private Instant createdAt;

    
    @Setter
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
