package com.ecommerce_project.Ecommerce.service;

import com.ecommerce_project.Ecommerce.DTO.CartDTO;
import com.ecommerce_project.Ecommerce.DTO.ProductDTO;
import com.ecommerce_project.Ecommerce.entities.Cart;
import com.ecommerce_project.Ecommerce.entities.CartItem;
import com.ecommerce_project.Ecommerce.entities.Product;
import com.ecommerce_project.Ecommerce.entities.Users;
import com.ecommerce_project.Ecommerce.exception.APIException;
import com.ecommerce_project.Ecommerce.exception.ResourceNotFoundException;
import com.ecommerce_project.Ecommerce.impl.CartServiceImpl;
import com.ecommerce_project.Ecommerce.repository.CartItemRepo;
import com.ecommerce_project.Ecommerce.repository.CartRepo;
import com.ecommerce_project.Ecommerce.repository.ProductRepo;
import com.ecommerce_project.Ecommerce.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService implements CartServiceImpl {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CartItemRepo cartItemRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private ModelMapper modelMapper;

    public CartDTO addToCart(Long productId, int quantity){

        // Get the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user by username
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the user's cart
        Cart cart = user.getCart();

        // Create Cart For User If Not Exist
        if(cart == null){
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalAmount(BigDecimal.ZERO);
        }

        // Find the product by productId
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the product is already in the cart
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getId(), productId);

        if (cartItem != null) {
            // Subtract the existing total amount for the current quantity of the CartItem
            cart.setTotalAmount(cart.getTotalAmount().subtract(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));

            // Update the quantity of the CartItem
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            // Add the new total amount based on the updated quantity of the CartItem
            cart.setTotalAmount(cart.getTotalAmount().add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));
            cart.getCartItems().add(cartItem);
        } else {
            // If the product is not in the cart, create a new CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem);

            // Add the amount for the new CartItem to the total amount
            cart.setTotalAmount(cart.getTotalAmount().add(cartItem.getPrice().multiply(BigDecimal.valueOf(quantity))));
        }

        // Save the Cart and CartItem to the repository
        cartRepo.save(cart);


        // Return the updated CartDTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);

        return cartDTO;
    }

    public CartDTO updateCart(Long productId, int quantity){
        // Get the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user by username
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the user's cart
        Cart cart = user.getCart();
        // Create Cart For User If Not Exist
        if(cart == null){
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalAmount(BigDecimal.ZERO);
        }

        // Find the product by productId
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the product is already in the cart
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getId(), productId);

        if (cartItem != null) {
            // Subtract the existing total amount for the current quantity of the CartItem
            cart.setTotalAmount(cart.getTotalAmount().subtract(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));
            // Update the quantity of the CartItem
            cartItem.setQuantity(quantity);
            // Add the new total amount based on the updated quantity of the CartItem
            cart.setTotalAmount(cart.getTotalAmount().add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));
            cart.getCartItems().add(cartItem);
        }else{
            throw new APIException("No Product Exist By This ID");
        }
        cartRepo.save(cart);


        // Return the updated CartDTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);

        return cartDTO;
    }

    public CartDTO getMyCart(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users users = userRepo.findByUsername(username).orElseThrow(()->new APIException("Not Authorized"));
        Cart cart = users.getCart();

        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cart.getId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

        cartDTO.setProducts(products);

        return cartDTO;


    }

    public List<CartDTO> getAllCarts(){
        List<Cart> carts = cartRepo.findAll();

        if (carts.size() == 0) {
            throw new APIException("No cart exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        return cartDTOs;
    }

    public String deleteFromCart(Long productId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users user = userRepo.findByUsername(username).get();

        Cart cart = user.getCart();

        Product product = productRepo.findById(productId).orElseThrow(()->new APIException("Product Not Found Please Enter Valid Product Id In Ypur Cart"));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getId(), productId);

        if(cartItem == null){
            throw new APIException("No Item With This Id In Your Cart");
        }


        cart.setTotalAmount(cart.getTotalAmount().subtract(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));

        if (cart.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            return "Amount value cannot be negative";
        }
        cartRepo.save(cart);

        cartItemRepo.deleteFromCartById(cartItem.getId());

        return "Deleted";
    }
}