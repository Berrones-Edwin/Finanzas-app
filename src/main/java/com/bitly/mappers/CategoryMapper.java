package com.bitly.mappers;

import org.springframework.stereotype.Component;

import com.bitly.dtos.CategoryResponse;
import com.bitly.models.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toDTO(Category c) {

        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .categoryType(c.getCategoryType())
                .color(c.getColor())
                .createdAt(c.getCreatedAt())
                .build();
    }

    
}
