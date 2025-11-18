package org.example.specification;

import org.example.model.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {

    public static Specification<PaymentCard> userNameContains(String name) {
        return (root, query, cb) ->
                (name == null || name.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("user").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<PaymentCard> userSurnameContains(String surname) {
        return (root, query, cb) ->
                (surname == null || surname.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("user").get("surname")), "%" + surname.toLowerCase() + "%");
    }
}