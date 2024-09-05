package com.ecommerce_project.Ecommerce.impl;

import com.ecommerce_project.Ecommerce.DTO.CartDTO;

import java.util.List;

public interface CartServiceImpl {

    CartDTO addToCart(Long productId, int quantity);

    CartDTO updateCart(Long productId, int quantity);

    CartDTO getMyCart();

    List<CartDTO> getAllCarts();

    String deleteFromCart(Long productId);
}
