package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.request.CategoryRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.CategoryResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Category;
import com.eshop.khujolt.eshop_khujolt_backend.exception.DuplicateResourceException;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceNotFoundException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = new Category();

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Category already exists");
        }

        category.setName(request.name());

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCreatedAt()
        );
    }

    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return toResponse(category);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Category already exists");
        }

        category.setName(request.name());

        Category updatedCategory = categoryRepository.save(category);

        return toResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }

        categoryRepository.deleteById(id);
    }
}
