package com.ecommerce_project.Ecommerce.DTO;

import com.ecommerce_project.Ecommerce.entities.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    @DecimalMin(value = "0.0", message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;

    @Min(value = 1, message = "User ID must be valid")
    private Long userId;

    private AddressDTO shippingAddress;
    private List<OrderItemDTO> orderItems;
}
