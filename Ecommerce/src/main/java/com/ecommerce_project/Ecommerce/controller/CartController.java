package com.ecommerce_project.Ecommerce.controller;

import com.ecommerce_project.Ecommerce.DTO.CartDTO;
import com.ecommerce_project.Ecommerce.DTO.OrderDTO;
import com.ecommerce_project.Ecommerce.service.CartService;
import com.ecommerce_project.Ecommerce.service.OrderService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@RequestParam Long productId, @RequestParam @Min(1) Integer quantity) {
        CartDTO cartDTO = cartService.addToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateCart(@RequestParam Long productId, @RequestParam @Min(1) Integer quantity) {
        CartDTO cartDTO = cartService.updateCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFromCart(@RequestParam Long productId) {
        cartService.deleteFromCart(productId);
        return new ResponseEntity<>("Item removed from cart", HttpStatus.OK);
    }

    @GetMapping("/admin/all-carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> carts = cartService.getAllCarts();
        if (carts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/my-cart")
    public ResponseEntity<CartDTO> getMyCarts() {
        CartDTO cartDTO = cartService.getMyCart();
        if (cartDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout() {
        OrderDTO orderDTO = orderService.createOrder();
        if (orderDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
