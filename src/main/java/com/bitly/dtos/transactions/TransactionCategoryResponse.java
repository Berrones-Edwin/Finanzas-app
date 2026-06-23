package com.bitly.dtos.transactions;

import com.bitly.enums.CategoryType;

public record TransactionCategoryResponse(
        long id,
        String name,
        String color,
        CategoryType categoryType
    ) {

}
