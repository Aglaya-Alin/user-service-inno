package com.example.user_service_inno.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.user_service_inno.entity.PaymentCard;

public class PaymentCardSpecifications {
    public static Specification<PaymentCard> hasHolder(String holder) {
        if (holder == null || holder.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("holder")), 
            "%" + holder.toLowerCase() + "%"
        );
    }

    public static Specification<PaymentCard> hasActiveStatus(Boolean isActive) {
        if (isActive == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
}
