package com.ecommerce_project.Ecommerce.DTO;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDTO {
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;

    private ProductDTO product;
}
