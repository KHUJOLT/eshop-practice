package com.eshop.khujolt.eshop_khujolt_backend.service;

import com.eshop.khujolt.eshop_khujolt_backend.dto.cart.AddCartItemRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.cart.CartItemResponse;
import com.eshop.khujolt.eshop_khujolt_backend.dto.cart.CartResponse;
import com.eshop.khujolt.eshop_khujolt_backend.dto.cart.UpdateCartItemRequest;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Cart;
import com.eshop.khujolt.eshop_khujolt_backend.entity.CartItem;
import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import com.eshop.khujolt.eshop_khujolt_backend.entity.User;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceConflictException;
import com.eshop.khujolt.eshop_khujolt_backend.exception.ResourceNotFoundException;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CartItemRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.CartRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.ProductRepository;
import com.eshop.khujolt.eshop_khujolt_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return cartRepository.findByUserId(userId)
                .map(this::toResponse)
                .orElseGet(this::emptyCart);
    }

    public CartResponse addItem(
            Long userId,
            AddCartItemRequest request
    ) {
        User user = lockUser(userId);

        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        Cart cart = getOnCreateCart(user);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        long newQuantity = (long) item.getQuantity() + request.quantity();

        checkQuantity(product, newQuantity);

        item.setQuantity((int) newQuantity);
        cartItemRepository.saveAndFlush(item);

        return toResponse(cart);
    }

    public CartResponse updateItem(
            Long userId,
            Long productId,
            UpdateCartItemRequest request
    ) {
        lockUser(userId);

        Cart cart = requireCart(userId);
        CartItem item = requireItem(cart.getId(), productId);

        checkQuantity(item.getProduct(), request.quantity());

        item.setQuantity(request.quantity());
        cartItemRepository.saveAndFlush(item);

        return toResponse(cart);
    }

    public void removeItem(
            Long userId,
            Long productID
    ) {
        lockUser(userId);

        Cart cart = requireCart(userId);
        CartItem item = requireItem(cart.getId(), productID);

        cartItemRepository.delete(item);
    }

    public void clearCart(Long userId) {
        lockUser(userId);

        cartRepository.findByUserId(userId).ifPresent(cart -> {
            List<CartItem> items = cartItemRepository.findByCartIdOrderByIdAsc(cart.getId());

            cartItemRepository.deleteAll(items);
        });
    }

    private User lockUser(Long userId) {
        return userRepository.findByIdForCartUpdate(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private Cart getOnCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    private Cart requireCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));
    }

    private CartItem requireItem(Long cartId, Long userId) {
        return cartItemRepository
                .findByCartIdAndProductId(cartId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));
    }

    private void checkQuantity(Product product, long quantity) {
        if (quantity < 1 || quantity > 999) {
            throw new ResourceConflictException("Quantity must be between 1 and 999");
        }

        if (quantity > product.getStock()) {
            throw new ResourceConflictException("Requested quantity exceeds available stock");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository
                .findByCartIdOrderByIdAsc(cart.getId())
                .stream()
                .map(item -> {
                    Product product = item.getProduct();

                    BigDecimal subtotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            subtotal,
                            product.getStock(),
                            item.getQuantity() <= product.getStock()
                    );
                })
                .toList();

        long totalQuantity = items.stream()
                .mapToLong(CartItemResponse::quantity)
                .sum();

        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);

        return new CartResponse(items, totalQuantity, total);
    }

    private CartResponse emptyCart() {
        return new CartResponse(
                List.of(),
                0,
                new BigDecimal("0.00")
        );
    }
}
