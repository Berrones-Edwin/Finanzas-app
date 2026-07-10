package com.bitly.specifications;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.bitly.models.Transfer;

import jakarta.persistence.criteria.JoinType;

public class TransferSpecification {

    public static Specification<Transfer> hasUserEmail(String email) {
        return (root, query, criteriaBuilder) -> {

if (Long.class != query.getResultType()) {
            root.fetch("fromAccount", JoinType.LEFT);
            root.fetch("toAccount", JoinType.LEFT);
        }

            return criteriaBuilder.equal(root.get("user").get("email"), email);
        };
    }

    public static Specification<Transfer> hasAccountId(Long accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId == null)
                return null;
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("fromAccount").get("id"), accountId),
                    criteriaBuilder.equal(root.get("toAccount").get("id"), accountId));
        };
    }

     public static Specification<Transfer> betweenDates(LocalDateTime start, LocalDateTime end) {

        return (root, query, criteriaBuilder) -> {

            if (start == null || end == null)
                return null;
            return criteriaBuilder.between(root.get("createdAt"), start, end);

        };
    }

}
