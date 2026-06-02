package com.example.user_service_inno.entity;


import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@DynamicUpdate
@Table(name = "payment_cards", indexes = {
        @Index(name = "user_id_index", columnList = "user_id")
    })
public class PaymentCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String holder;

    @Column(nullable = false, name = "expiration_date", columnDefinition = "DATE")
    private YearMonth expirationDate;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(updatable = false, name = "created_at", nullable = false)
    private Instant createdAt;


    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
