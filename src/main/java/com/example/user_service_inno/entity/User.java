package com.example.user_service_inno.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "users") //, unique = true ?
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String surname;

    @Setter
    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Setter
    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentCard> cards;
}
