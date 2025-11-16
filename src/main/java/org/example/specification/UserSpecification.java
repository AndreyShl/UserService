package org.example.specification;

import org.example.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> firstNameContains(String name) {
        return (root, query, cb) -> name == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> lastNameContains(String surName) {
        return (root, query, cb) -> surName == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("surname")), "%" + surName.toLowerCase() + "%");
    }
}
