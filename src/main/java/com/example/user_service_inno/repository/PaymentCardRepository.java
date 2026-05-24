package com.example.user_service_inno.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.user_service_inno.entity.PaymentCard;



@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID>, JpaSpecificationExecutor<PaymentCard>{

    @Query(value = "SELECT * FROM PaymentCard WHERE user_id = ?1", nativeQuery = true)
    List<PaymentCard> getAllPaymnetCardByUserId(UUID userId);

    int countByUserId(UUID userId);
}
