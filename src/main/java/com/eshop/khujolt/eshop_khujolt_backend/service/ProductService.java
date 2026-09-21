package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.request.ProductRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.ProductResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceNotFoundException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt()
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

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }
}
