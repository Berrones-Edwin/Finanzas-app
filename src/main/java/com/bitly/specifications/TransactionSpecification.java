package com.bitly.specifications;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.bitly.enums.TransactionType;
import com.bitly.models.Transaction;

import jakarta.persistence.criteria.JoinType;

public class TransactionSpecification {

    public static Specification<Transaction> hasUserEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("account", JoinType.LEFT);
                root.fetch("category", JoinType.LEFT);
            }
            return criteriaBuilder.equal(root.get("user").get("email"), email);
        };
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null)
                return null;
            return criteriaBuilder.equal(root.get("transactionType"), type);
        };
    }

    public static Specification<Transaction> hasAccountId(Long accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId == null)
                return null;
            return criteriaBuilder.equal(root.get("account").get("id"), accountId);
        };
    }

    public static Specification<Transaction> hasCategorytId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null)
                return null;
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Transaction> betweenDates(LocalDateTime start, LocalDateTime end) {

        return (root, query, criteriaBuilder) -> {

            if (start == null || end == null)
                return null;
            return criteriaBuilder.between(root.get("createdAt"), start, end);

        };
    }

}
