package com.ecommerce_project.Ecommerce.impl;

import com.ecommerce_project.Ecommerce.DTO.AddressDTO;
import com.ecommerce_project.Ecommerce.entities.Address;
import org.hibernate.sql.Update;

import java.util.List;

public interface AddressServiceImpl {
    AddressDTO addAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddress(Long id);

    AddressDTO updateAddress(Long id, AddressDTO addressDTO);

    String deleteAddress(Long id);
}
