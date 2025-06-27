package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.groupbuy.entity.Address;
import com.groupbuy.mapper.AddressMapper;
import com.groupbuy.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    public List<Address> getUserAddresses(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("is_default").orderByDesc("created_at");
        return addressMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public Address addAddress(Address address) {
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        
        if (address.getIsDefault()) {
            // 如果设置为默认地址，先取消其他默认地址
            UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", address.getUserId())
                         .set("is_default", false);
            addressMapper.update(null, updateWrapper);
        }
        
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    @Transactional
    public Address updateAddress(Address address) {
        address.setUpdatedAt(LocalDateTime.now());
        
        if (address.getIsDefault()) {
            // 如果设置为默认地址，先取消其他默认地址
            UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", address.getUserId())
                         .ne("id", address.getId())
                         .set("is_default", false);
            addressMapper.update(null, updateWrapper);
        }
        
        addressMapper.updateById(address);
        return address;
    }
    
    @Override
    public void deleteAddress(Long id) {
        addressMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void setDefaultAddress(Long id) {
        Address address = addressMapper.selectById(id);
        if (address != null) {
            // 先取消该用户的其他默认地址
            UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", address.getUserId())
                         .set("is_default", false);
            addressMapper.update(null, updateWrapper);
            
            // 设置当前地址为默认
            address.setIsDefault(true);
            address.setUpdatedAt(LocalDateTime.now());
            addressMapper.updateById(address);
        }
    }
    
    @Override
    public Address getDefaultAddress(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_default", true);
        return addressMapper.selectOne(wrapper);
    }
    
    @Override
    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }
}
