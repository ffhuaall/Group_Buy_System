package com.groupbuy.service;

import com.groupbuy.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getUserAddresses(Long userId);
    Address addAddress(Address address);
    Address updateAddress(Address address);
    void deleteAddress(Long id);
    void setDefaultAddress(Long id);
    Address getDefaultAddress(Long userId);
    Address getById(Long id);
}