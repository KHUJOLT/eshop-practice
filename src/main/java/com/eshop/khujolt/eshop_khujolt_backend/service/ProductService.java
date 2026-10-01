package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.request.ProductRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.ProductResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Category;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceNotFoundException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CategoryRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<ProductResponse> getProducts(
            Long categoryId,
            String name,
            Pageable pageable
    ) {
        Page<Product> products;

        if (categoryId != null && name != null && !name.isBlank()) {

            products = productRepository
                    .findByCategoryIdAndNameContainingIgnoreCase(categoryId, name, pageable);

        } else if (categoryId != null) {

            products = productRepository.findByCategoryId(categoryId, pageable);

        } else if (name != null && !name.isBlank()) {

            products = productRepository.findByNameContainingIgnoreCase(name, pageable);

        } else {

            products = productRepository.findAll(pageable);
        }

        return products.map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }

    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return toResponse(product);
    }

    public ProductResponse updateProduct(
            Long id,
            ProductRequest request
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        if(productRepository.existsByCategoryId(id)){
            throw new IllegalStateException("Cannot delete category containing products");
        }

        productRepository.deleteById(id);
    }


}
