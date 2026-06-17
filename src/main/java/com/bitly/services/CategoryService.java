package com.bitly.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bitly.dtos.CategoryRequest;
import com.bitly.dtos.CategoryResponse;
import com.bitly.dtos.PageResponse;
import com.bitly.exceptions.CategoryAlreadyExistsException;
import com.bitly.mappers.CategoryMapper;
import com.bitly.models.Category;
import com.bitly.models.User;
import com.bitly.repository.CategoryRepository;
import com.bitly.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

        private final CategoryRepository categoryRepository;
        private final UserRepository userRepository;
        private final CategoryMapper categoryMapper;

        public CategoryService(
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        CategoryMapper categoryMapper) {

                this.categoryRepository = categoryRepository;
                this.userRepository = userRepository;
                this.categoryMapper = categoryMapper;
        }

        public PageResponse<CategoryResponse> getAllCategories(String email, int page, int size) {

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<Category> categories = categoryRepository.findByUserId(user.getId(), pageable);

                List<CategoryResponse> categoryResponses = categories.stream()
                                .map(categoryMapper::toDTO).toList();

                return new PageResponse<>(
                                categoryResponses,
                                categories.getNumber(),
                                categories.getSize(),
                                categories.getTotalElements(),
                                categories.getTotalPages(),
                                categories.isFirst(),
                                categories.isLast());
        }

        public CategoryResponse findCategoryById(long id, String email) {

                CategoryResponse category = categoryRepository.findByIdAndUserEmail(id, email)
                                .map(categoryMapper::toDTO)
                                .orElseThrow(() -> new EntityNotFoundException("No category found with the id " + id));

                return category;

        }

        public CategoryResponse createCategory(CategoryRequest request, String username) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User was not found with email " + username));

                boolean isCategoryDuplicated = categoryRepository.existsByNameIgnoreCaseAndUserId(request.name(),
                                user.getId());

                if (isCategoryDuplicated) {
                        throw new CategoryAlreadyExistsException("You alreadey have a category name " + request.name());
                }

                Category c = Category.builder()
                                .user(user)
                                .name(request.name())
                                .categoryType(request.categoryType())
                                .color(request.color())
                                .build();

                Category categorySaved = categoryRepository.save(c);

                return categoryMapper.toDTO(categorySaved);

        }

        public CategoryResponse updateCategory(CategoryRequest request, String username, long categoryId) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User was not found with email " + username));

                boolean isCategoryDuplicated = categoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                                request.name(),
                                user.getId(),
                                categoryId);

                if (isCategoryDuplicated) {
                        throw new CategoryAlreadyExistsException("You alreadey have a category name " + request.name());
                }

                Category c = categoryRepository.findByIdAndUserEmail(categoryId, username).orElseThrow(
                                () -> new EntityNotFoundException("Category was not found with id " + categoryId));

                c.setName(request.name());
                c.setCategoryType(request.categoryType());
                c.setColor(request.color());

                Category categoryUpdate = categoryRepository.save(c);

                return categoryMapper.toDTO(categoryUpdate);

        }

        public void deleteCategory(String username, long id) {

                Category category = categoryRepository.findByIdAndUserEmail(id, username).orElseThrow(()->new EntityNotFoundException("Category was not found with id " + id));

                category.setDeletedAt(LocalDateTime.now());

                categoryRepository.save(category);
                

        }

}
