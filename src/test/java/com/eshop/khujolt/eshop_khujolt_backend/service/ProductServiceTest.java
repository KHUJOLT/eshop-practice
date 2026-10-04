package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.product.ProductRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.product.ProductResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Category;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceNotFoundException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CategoryRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        System.out.println("Created category: " + category.getName());

        ProductRequest request = new ProductRequest(
                "Gaming Mouse",
                "Wireless mouse",
                new BigDecimal("149.99"),
                10,
                1L
        );

        System.out.println("Requested: " + request.name());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Product product = new Product();

        product.setId(1L);
        product.setName("Gaming Mouse");
        product.setDescription("Wireless mouse");
        product.setPrice(new BigDecimal("149.99"));
        product.setStock(10);
        product.setCategory(category);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.createProduct(request);

        assertEquals(
                "Gaming Mouse",
                response.name()
        );

        assertEquals(
                1L,
                response.categoryId()
        );
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        ProductRequest request = new ProductRequest(
                "Gaming Mouse",
                "Wireless mouse",
                new BigDecimal("149.99"),
                10,
                999L
        );

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.createProduct(request)
        );
    }
}

