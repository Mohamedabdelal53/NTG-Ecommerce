package com.ecommerce_project.Ecommerce.impl;

import com.ecommerce_project.Ecommerce.DTO.OrderDTO;
import com.ecommerce_project.Ecommerce.entities.OrderStatus;

import java.util.List;

public interface OrderServiceImpl {
    OrderDTO createOrder();
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getOrdersByUser();
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);

}

