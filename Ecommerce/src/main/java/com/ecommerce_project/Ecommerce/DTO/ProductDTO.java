package com.ecommerce_project.Ecommerce.DTO;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productID;

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must be zero or greater")
    private int stock;

    private String imageUrl;
}
