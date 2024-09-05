package com.ecommerce_project.Ecommerce.service;

import com.ecommerce_project.Ecommerce.DTO.OrderDTO;
import com.ecommerce_project.Ecommerce.entities.*;
import com.ecommerce_project.Ecommerce.exception.APIException;
import com.ecommerce_project.Ecommerce.exception.ResourceNotFoundException;
import com.ecommerce_project.Ecommerce.impl.OrderServiceImpl;
import com.ecommerce_project.Ecommerce.repository.CartItemRepo;
import com.ecommerce_project.Ecommerce.repository.CartRepo;
import com.ecommerce_project.Ecommerce.repository.OrderRepo;
import com.ecommerce_project.Ecommerce.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService implements OrderServiceImpl {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private CartItemRepo cartItemRepo;

    @Override
    public OrderDTO createOrder() {
        // Get the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user by username
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the user's cart
        Cart cart = user.getCart();

        // Check if user has a cart
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new APIException("Your cart is empty. Add items to the cart before checking out.");
        }

        // Create a new Order
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount()); // Set total amount from cart
        order.setStatus(OrderStatus.PENDING); // Assuming an enum for order status
        order.setUser(user);

        // Optional: Set a shipping address (use a default one or ask user to provide)
        Address shippingAddress = getDefaultShippingAddress(user); // Implement this method if needed
        if (shippingAddress == null) {
            throw new APIException("Shipping address not found. Please set a shipping address.");
        }
        order.setShippingAddress(shippingAddress);

        // Copy cart items to order items
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        // Save Order
        orderRepo.save(order);

        // Clear all cart items
        cartItemRepo.deleteAllCartItemsByCartId(cart.getId());

        // Reset cart total amount and save
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepo.save(cart);

        // Convert Order entity to DTO and return
        return modelMapper.map(order, OrderDTO.class);
    }


    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByUser() {
        // Get the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user by username
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepo.findByUser(user); // Add this method to OrderRepo
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);
        Order updatedOrder = orderRepo.save(order);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    private Address getDefaultShippingAddress(Users user) {
        // Implement this method based on your requirements
        // E.g., return the first address or a specific default address
        return user.getAddresses().stream().findFirst().orElse(null);
    }
}
