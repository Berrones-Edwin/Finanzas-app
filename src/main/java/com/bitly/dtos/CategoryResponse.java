package com.bitly.dtos;

import java.time.LocalDateTime;

import com.bitly.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private long id;
    private String name;
    private CategoryType categoryType;
    private String color;
    private LocalDateTime createdAt;
}
