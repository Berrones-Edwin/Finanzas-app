package com.bitly.dtos;

import com.bitly.enums.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
        @NotBlank(message = "Name is mandatory and cannot be blank") @Size(min = 4, max = 100, message = "Name must be between 4 and 100 characters") String name,

        @NotNull(message = "Type is mandatory") AccountType accountType,

        @NotBlank(message = "Currency is mandatory and cannot be blank") @Size(min = 3, max = 3, message = "Currency must be 3 characters") @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters (e.g., USD, MXN, EUR)") String currency,

        @NotBlank(message = "Color is mandatory") @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid 7-character hexadeciaml code (e.g., #6B7280") String color) {

}
