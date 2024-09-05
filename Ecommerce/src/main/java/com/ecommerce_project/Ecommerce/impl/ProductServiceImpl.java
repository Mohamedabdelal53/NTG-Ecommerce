package com.ecommerce_project.Ecommerce.impl;

import com.ecommerce_project.Ecommerce.DTO.ProductDTO;
import com.ecommerce_project.Ecommerce.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductServiceImpl {
    List<ProductDTO> getProducts();

    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    String deleteProduct(Long productId);

    ProductDTO getProduct(Long productId);

    List<ProductDTO> searchProducts(String keyword);
}
