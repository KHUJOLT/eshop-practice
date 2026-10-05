package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.cart.AddCartItemRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.cart.CartResponse;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Cart;
import com.eshop.khujolt.eshop_khujolt_backend.entity.CartItem;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import com.eshop.khujolt.eshop_khujolt_backend.entity.User;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceConflictException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CartItemRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CartRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.ProductRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private Product product;
    private CartItem item;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        product = new Product();
        product.setId(100L);
        product.setName("Gaming Mouse");
        product.setPrice(new BigDecimal("49.99"));
        product.setStock(10);

        item = new CartItem();
        item.setId(20L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);

        when(userRepository.findByIdForCartUpdate(1L))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(100L))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartIdAndProductId(10L, 100L))
                .thenReturn(Optional.of(item));
    }

    @Test
    void shouldIncreaseQuantityAndRecalculateTotal() {
        when(cartItemRepository.findByCartIdOrderByIdAsc(10L))
                .thenReturn(List.of(item));

        CartResponse response = cartService.addItem(
                1L,
                new AddCartItemRequest(100L, 3)
        );

        // Two existing units plus three added units.
        assertEquals(5, item.getQuantity().intValue());
        assertEquals(5L, response.totalQuantity());
        assertEquals(1, response.items().size());

        var responseItem = response.items().getFirst();

        assertEquals(100L, responseItem.productId().longValue());
        assertEquals(5, responseItem.quantity().intValue());
        assertEquals(new BigDecimal("49.99"), responseItem.unitPrice());
        assertEquals(new BigDecimal("249.95"), responseItem.subtotal());
        assertEquals(new BigDecimal("249.95"), response.total());
        assertTrue(responseItem.available());

        verify(cartItemRepository).saveAndFlush(item);

        // Adding to a cart must not reserve or deduct stock.
        assertEquals(10, product.getStock().intValue());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectCombinedQuantityExceedingStock() {
        product.setStock(4);

        // Each quantity fits individually, but 2 + 3 exceeds stock.
        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> cartService.addItem(
                        1L,
                        new AddCartItemRequest(100L, 3)
                )
        );

        assertEquals(
                "Requested quantity exceeds available stock",
                exception.getMessage()
        );

        assertEquals(2, item.getQuantity().intValue());
        assertEquals(4, product.getStock().intValue());

        verify(cartItemRepository, never())
                .saveAndFlush(any(CartItem.class));

        verify(cartItemRepository, never())
                .findByCartIdOrderByIdAsc(anyLong());
    }
}