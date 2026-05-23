package com.example.user_service_inno.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.user_service_inno.entity.User;

public class UserSpecifications {

     public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName == null || firstName.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("firstName")), 
                "%" + firstName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, criteriaBuilder) -> {
            if (surname == null || surname.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("surname")), 
                "%" + surname.toLowerCase() + "%"
            );
        };
    }
    
}
