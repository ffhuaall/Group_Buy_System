package com.groupbuy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Supplier;
import java.util.List;

public interface SupplierService {
    Supplier addSupplier(Supplier supplier);
    List<Supplier> getSuppliers(int page, int size, String status);
    Page<Supplier> getSuppliersPage(int page, int size, String status, String keyword);
    Supplier updateSupplier(Supplier supplier);
    void updateSupplierStatus(Long id, String status);
    Supplier getByUserId(Long userId);
}