package com.example.user_service_inno.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.user_service_inno.entity.User;

public class UserSpecifications {

    public static Specification<User> hasFirstName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")), 
            "%" + name.toLowerCase() + "%"
        );
    }

    public static Specification<User> hasSurname(String surname) {
        if (surname == null || surname.isBlank()) {
            return null; 
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("surname")), 
            "%" + surname.toLowerCase() + "%"
        );
    }
}