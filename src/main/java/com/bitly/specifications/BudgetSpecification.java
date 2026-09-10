package com.bitly.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.bitly.models.Budget;

public class BudgetSpecification {

    public static Specification<Budget> hasUserEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("email"), email);
    }

    public static Specification<Budget> hasMonth(Integer month) {
        return (root, query, criteriaBuilder) -> {
            if (month == null) {
                return null;
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.function("date_part", Integer.class,
                            criteriaBuilder.literal("month"),
                            root.get("month")),
                    month);
        };
    }

    public static Specification<Budget> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null)
                return null;
            return criteriaBuilder.equal(
                    root.get("category").get("id"),
                    categoryId);
        };
    }

    public static Specification<Budget> hasYear(Integer year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null)
                return null;
            return criteriaBuilder.equal(
                    criteriaBuilder.function("date_part", Integer.class,
                            criteriaBuilder.literal("year"),
                            root.get("month")),
                    year);
        };
    }

}
