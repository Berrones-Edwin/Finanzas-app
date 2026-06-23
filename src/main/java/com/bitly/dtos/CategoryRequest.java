package com.bitly.dtos;

import com.bitly.enums.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Name is mandatory and cannot be blank") @Size(min = 4,max = 100,message = "Name must be between 4 and 100 characters") String name,

        @NotNull(message = "Type is mandatory") CategoryType categoryType,

        @NotBlank(message ="Color is mandatory") @Pattern(regexp = "^#([A-Fa-f0-9]{6})$",message = "Color must be a valid 7-character hexadeciaml code (e.g., #6B7280") String color

) {
}
