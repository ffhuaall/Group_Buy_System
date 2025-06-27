package com.groupbuy.controller;

import com.groupbuy.common.Result;
import com.groupbuy.entity.Address;
import com.groupbuy.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping("/user/{userId}")
    public Result<List<Address>> getUserAddresses(@PathVariable Long userId) {
        List<Address> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }
    
    @GetMapping("/{id}")
    public Result<Address> getAddressById(@PathVariable Long id) {
        Address address = addressService.getById(id);
        return Result.success(address);
    }
    
    @GetMapping("/default/{userId}")
    public Result<Address> getDefaultAddress(@PathVariable Long userId) {
        Address address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
    
    @PostMapping("/add")
    public Result<Address> addAddress(@Valid @RequestBody Address address) {
        Address savedAddress = addressService.addAddress(address);
        return Result.success(savedAddress);
    }
    
    @PutMapping("/update")
    public Result<Address> updateAddress(@Valid @RequestBody Address address) {
        Address updatedAddress = addressService.updateAddress(address);
        return Result.success(updatedAddress);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return Result.success();
    }
    
    @PutMapping("/setDefault/{id}")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        addressService.setDefaultAddress(id);
        return Result.success();
    }
}