package com.bitly.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.CategoryRequest;
import com.bitly.dtos.CategoryResponse;
import com.bitly.dtos.PageResponse;
import com.bitly.services.CategoryService;

import jakarta.validation.Valid;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

        return ResponseEntity.ok(categoryService.findCategoryById( userDetails.getUsername(),id));
    }

// return ResponseEntity.noContent().build(); // Genera un HTTP 204 (No Content)
    @PostMapping
    public ResponseEntity<CategoryResponse> saveCategory(
        @Valid @RequestBody CategoryRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request,userDetails.getUsername()));
    }


    @PatchMapping("{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
        @Valid @RequestBody CategoryRequest request,
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable("categoryId") long id
    ){
        return ResponseEntity.ok(categoryService.updateCategory(request,userDetails.getUsername(),id));
    }

    @DeleteMapping("{categoryId}")
    public ResponseEntity<?> deleteCategory(
        @AuthenticationPrincipal UserDetails userdetails,
        @PathVariable("categoryId") long id
    ){

        categoryService.deleteCategory(userdetails.getUsername(),id);
        return ResponseEntity.noContent().build();
    }


}
