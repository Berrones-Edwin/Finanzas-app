package com.bitly.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.CategoryResponse;
import com.bitly.dtos.PageResponse;
import com.bitly.services.CategoryService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> getAllCategories(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size) {

        return ResponseEntity.ok(categoryService.getAllCategories(userDetails.getUsername(), page, size));
    }

    @GetMapping("{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable("categoryId") long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(categoryService.findCategoryById(id, userDetails.getUsername()));
    }

}
