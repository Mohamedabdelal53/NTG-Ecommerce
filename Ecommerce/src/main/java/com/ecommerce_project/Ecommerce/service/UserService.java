package com.ecommerce_project.Ecommerce.service;

import com.ecommerce_project.Ecommerce.DTO.AddressDTO;
import com.ecommerce_project.Ecommerce.DTO.LoginDTO;
import com.ecommerce_project.Ecommerce.entities.Address;
import com.ecommerce_project.Ecommerce.entities.Cart;
import com.ecommerce_project.Ecommerce.entities.Role;
import com.ecommerce_project.Ecommerce.entities.Users;
import com.ecommerce_project.Ecommerce.DTO.UserDTO;
import com.ecommerce_project.Ecommerce.exception.APIException;
import com.ecommerce_project.Ecommerce.impl.UserServiceImpl;
import com.ecommerce_project.Ecommerce.repository.AddressRepo;
import com.ecommerce_project.Ecommerce.repository.CartRepo;
import com.ecommerce_project.Ecommerce.repository.RoleRepo;
import com.ecommerce_project.Ecommerce.repository.UserRepo;
import com.ecommerce_project.Ecommerce.security.JWT.JWTGenerator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceImpl {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private CartRepo cartRepo;

    @Override
    public String addUser(@NotNull UserDTO userDTO) {
        // Create User entity
        Users user = new Users();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        // Create and set Cart for User
        Cart cart = new Cart();
        cart.setTotalAmount(BigDecimal.ZERO); // Initialize cart amount if needed
        cart.setUser(user); // Set the user for the cart
        user.setCart(cart); // Set the cart for the user

        // Find and set Role
        Role role = roleRepo.findByName("ROLE_USER").orElseThrow(() -> new APIException("Role not found"));
        user.getRoles().add(role);

        // Handle multiple addresses
        List<Address> addresses = userDTO.getAddress().stream()
                .map(addressDTO -> {
                    Address address = new Address();
                    address.setStreet(addressDTO.getStreet());
                    address.setBuildingName(addressDTO.getBuildingName());
                    address.setCity(addressDTO.getCity());
                    address.setState(addressDTO.getState());
                    address.setCountry(addressDTO.getCountry());
                    address.setPostalCode(addressDTO.getPostalCode());
                    address.setUser(user);
                    return address;
                })
                .collect(Collectors.toList());

        user.setAddresses(addresses);

        // Save User entity (including addresses and cart)
        Users savedUser = userRepo.save(user); // This will also save the cart because of CascadeType

        // Optional: If the cart is not saved due to a different reason, explicitly save it
        // cartRepo.save(cart);

        return "User Registration Success";
    }

    @Override
    public String login(@NotNull LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtGenerator.generateToken(loginDTO.getUsername());
        }
        return "Login Fail";
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> {
                    List<AddressDTO> addressDTOs = user.getAddresses().stream()
                            .map(address -> new AddressDTO(
                                    address.getId(),
                                    address.getStreet(),
                                    address.getBuildingName(),
                                    address.getCity(),
                                    address.getState(),
                                    address.getCountry(),
                                    address.getPostalCode()
                            ))
                            .collect(Collectors.toList());

                    return new UserDTO(
                            user.getId(),
                            user.getUsername(),
                            null, // Password is not included in DTO for security reasons
                            user.getEmail(),
                            addressDTOs, // Pass list of addresses
                            null // Assuming CartDTO is not used here
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public String deleteUser(Long id) {
        Users user = userRepo.findById(id).orElseThrow(() -> new APIException("User Not Found"));
        String name = user.getUsername();
        userRepo.deleteById(id);
        return name + " Account Is Deleted";
    }

    @Override
    public UserDTO getMyUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        Users user = userRepo.findByUsername(authenticatedUsername).orElseThrow(() -> new APIException("User Not Found"));

        // Check if the authenticated user is trying to view their own profile
        if (!user.getUsername().equals(authenticatedUsername)) {
            throw new APIException("You are not authorized to view this user's profile");
        }


        List<AddressDTO> addressDTOs = user.getAddresses().stream()
                .map(address -> new AddressDTO(
                        address.getId(),
                        address.getStreet(),
                        address.getBuildingName(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getPostalCode()
                ))
                .collect(Collectors.toList());

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                null, // Password is not included in DTO for security reasons
                user.getEmail(),
                addressDTOs,
                null // Assuming CartDTO is not used here
        );
    }

    @Override
    public String updateMyUser(Long id, UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        // Fetch the user to be updated
        Users user = userRepo.findById(id).orElseThrow(() -> new APIException("User Not Found"));

        // Check if the authenticated user is trying to update their own profile
        if (!user.getUsername().equals(authenticatedUsername)) {
            throw new APIException("You are not authorized to update this user's profile");
        }

        // Update user details
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        // Update addresses
        user.getAddresses().clear(); // Clear existing addresses
        List<Address> addresses = userDTO.getAddress().stream()
                .map(addressDTO -> {
                    Address address = new Address();
                    address.setStreet(addressDTO.getStreet());
                    address.setBuildingName(addressDTO.getBuildingName());
                    address.setCity(addressDTO.getCity());
                    address.setState(addressDTO.getState());
                    address.setCountry(addressDTO.getCountry());
                    address.setPostalCode(addressDTO.getPostalCode());
                    address.setUser(user);
                    return address;
                })
                .collect(Collectors.toList());

        user.getAddresses().addAll(addresses);

        // Find and set Role
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        if (!isAdmin) {
            Optional<Role> role = roleRepo.findByName("ROLE_USER");
            role.ifPresent(user.getRoles()::add);
        }

        // Save User entity
        userRepo.save(user);

        return "User Updated Success";
    }


    @Override
    public UserDTO getuserbyadmin(Long id){
        Users user = userRepo.findById(id).orElseThrow(() -> new APIException("User Not Found"));

        List<AddressDTO> addressDTOs = user.getAddresses().stream()
                .map(address -> new AddressDTO(
                        address.getId(),
                        address.getStreet(),
                        address.getBuildingName(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getPostalCode()
                ))
                .collect(Collectors.toList());

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                null, // Password is not included in DTO for security reasons
                user.getEmail(),
                addressDTOs,
                null // Assuming CartDTO is not used here
        );
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }


}
