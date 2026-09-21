package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.request.ProductRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.ProductResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import com.eshop.khujolt.eshop_khujolt_backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);

    private final ProductService productService = new ProductService(productRepository);

    @Test
    void shouldCreateProduct() {

        ProductRequest request = new ProductRequest(
                "pisun",
                new BigDecimal("228.5"),
                10
        );

        Product savedProduct = new Product(
                "pisun",
                new BigDecimal("228.5"),
                10
        );

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse result = productService.createProduct(request);

        assertEquals("pisun", result.name());
        assertEquals(new BigDecimal("228.5"), result.price());
        assertEquals(10, result.stock());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldGetAllProducts() {

        List<Product> products = new ArrayList<>();
    }
}
*/