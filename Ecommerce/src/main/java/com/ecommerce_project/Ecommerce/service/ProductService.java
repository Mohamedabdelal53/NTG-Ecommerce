package com.ecommerce_project.Ecommerce.service;

import com.ecommerce_project.Ecommerce.DTO.ProductDTO;
import com.ecommerce_project.Ecommerce.entities.Category;
import com.ecommerce_project.Ecommerce.entities.Product;
import com.ecommerce_project.Ecommerce.exception.APIException;
import com.ecommerce_project.Ecommerce.impl.ProductServiceImpl;
import com.ecommerce_project.Ecommerce.repository.CategoryRepo;
import com.ecommerce_project.Ecommerce.repository.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements ProductServiceImpl {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<ProductDTO> getProducts() {
        return productRepo.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new APIException("This Category Does Not Exist"));

        // Check if the product already exists by name and description
        if (productRepo.existsByNameAndDescription(productDTO.getName(), productDTO.getDescription())) {
            throw new APIException("Product already exists!");
        }

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImageUrl("default.png"); // Default image if no image is uploaded yet

        Product savedProduct = productRepo.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new APIException("Product not found"));

        if (image == null || image.isEmpty()) {
            throw new APIException("Invalid image file");
        }

        String imageUrl = saveImage(image);
        product.setImageUrl(imageUrl);

        Product savedProduct = productRepo.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    private String saveImage(MultipartFile image) throws IOException {

        String uploadDir = "uploaded-images/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // Create the directory if it doesn't exist
        }

        String imageName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path imagePath = uploadPath.resolve(imageName);
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + imageName; // Return the path or URL where the image is stored
    }

    @Override
    public ProductDTO getProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new APIException("Product not found"));
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        productRepo.findById(productId)
                .orElseThrow(() -> new APIException("Product not found"));
        productRepo.deleteById(productId);
        return "Product Deleted";
    }

    @Override
    public List<ProductDTO> searchProducts(String searchInput) {
        // Split the input into multiple keywords
        String[] keywords = searchInput.split(" ");

        List<Product> products;
        if (keywords.length > 1) {
            // If more than one keyword, use multiple keyword search
            products = productRepo.searchByMultipleKeywords(keywords[0], keywords[1]);
        } else {
            // For a single keyword, use the basic search
            products = productRepo.searchByKeyword(keywords[0]);
        }

        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }


}
