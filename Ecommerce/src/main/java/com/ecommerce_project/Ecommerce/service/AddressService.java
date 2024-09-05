package com.ecommerce_project.Ecommerce.service;

import com.ecommerce_project.Ecommerce.DTO.AddressDTO;
import com.ecommerce_project.Ecommerce.entities.Address;
import com.ecommerce_project.Ecommerce.exception.APIException;
import com.ecommerce_project.Ecommerce.impl.AddressServiceImpl;
import com.ecommerce_project.Ecommerce.repository.AddressRepo;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressService implements AddressServiceImpl {
    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO addAddress(@NotNull AddressDTO addressDTO) {
        Address addressFromDB = addressRepo.findByCountryAndStateAndCityAndPostalCodeAndStreetAndBuildingName(
                addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity(),
                addressDTO.getPostalCode(), addressDTO.getStreet(), addressDTO.getBuildingName()
        );
        if (addressFromDB != null) {
            throw new APIException("Address already exists with addressId: " + addressFromDB.getId());
        }
        Address address = modelMapper.map(addressDTO, Address.class);
        Address savedAddress = addressRepo.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> allAddress = addressRepo.findAll();
        return allAddress.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(Long id) {
        Address address = addressRepo.findById(id).orElseThrow(() -> new APIException("Address Not Exist"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address addressFromDB = addressRepo.findById(id).orElseThrow(() -> new APIException("Address Not Found"));
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setPostalCode(addressDTO.getPostalCode());
        addressFromDB.setStreet(addressDTO.getStreet());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepo.save(addressFromDB);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long id) {
        Address address = addressRepo.findById(id).orElseThrow(() -> new APIException("Address Not Found"));
        addressRepo.deleteById(id);
        return "Address Deleted";
    }
}
