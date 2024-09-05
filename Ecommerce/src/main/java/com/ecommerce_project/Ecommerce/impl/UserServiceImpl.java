package com.ecommerce_project.Ecommerce.impl;

import com.ecommerce_project.Ecommerce.DTO.LoginDTO;
import com.ecommerce_project.Ecommerce.DTO.UserDTO;

import java.util.List;

public interface UserServiceImpl {
    String addUser(UserDTO userDTO);

    String login(LoginDTO loginDTO);

    List<UserDTO> getAllUsers();

    String deleteUser(Long id);

    UserDTO getMyUser();

    String updateMyUser(Long id, UserDTO userDTO);

    UserDTO getuserbyadmin(Long id);

    boolean usernameExists(String username);
}
