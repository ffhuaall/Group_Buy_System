package com.groupbuy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.common.Result;
import com.groupbuy.entity.Category;
import com.groupbuy.service.CategoryService;
import com.groupbuy.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return Result.success(categories);
    }
    
    @PostMapping("/add")
    public Result<Category> addCategory(@Valid @RequestBody Category category) {
        Category savedCategory = categoryService.addCategory(category);
        return Result.success(savedCategory);
    }
    
    @PutMapping("/update")
    public Result<Category> updateCategory(@Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(category);
        return Result.success(updatedCategory);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
    
    // 管理员获取分类列表（带分页）
    @GetMapping("/admin/list")
    public Result<Page<Category>> getAdminCategoryList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Page<Category> result = categoryService.getAdminCategoryList(
                current, size, keyword, status, sortBy, sortOrder);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    // 管理员添加分类
    @PostMapping("/admin/add")
    public Result<String> addCategoryByAdmin(@Valid @RequestBody Category category,
                                            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            categoryService.save(category);
            return Result.success("分类添加成功");
            
        } catch (Exception e) {
            return Result.error("添加分类失败: " + e.getMessage());
        }
    }
    
    // 管理员更新分类
    @PutMapping("/admin/update/{id}")
    public Result<String> updateCategoryByAdmin(@PathVariable Long id,
                                               @Valid @RequestBody Category category,
                                               @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Category existingCategory = categoryService.getById(id);
            if (existingCategory == null) {
                return Result.error("分类不存在");
            }
            
            category.setId(id);
            categoryService.updateById(category);
            
            return Result.success("分类更新成功");
            
        } catch (Exception e) {
            return Result.error("更新分类失败: " + e.getMessage());
        }
    }
    
    // 管理员删除分类
    @DeleteMapping("/admin/delete/{id}")
    public Result<String> deleteCategoryByAdmin(@PathVariable Long id,
                                               @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Category category = categoryService.getById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            
            categoryService.removeById(id);
            return Result.success("分类删除成功");
            
        } catch (Exception e) {
            return Result.error("删除分类失败: " + e.getMessage());
        }
    }
    
    // 管理员更新分类状态
    @PutMapping("/admin/status/{id}")
    public Result<String> updateCategoryStatus(@PathVariable Long id,
                                              @RequestParam String status,
                                              @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Category category = categoryService.getById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            
            // 将字符串状态转换为整数
            Integer statusValue = "ACTIVE".equals(status) ? 1 : 0;
            category.setStatus(statusValue);
            categoryService.updateById(category);
            
            return Result.success("状态更新成功");
            
        } catch (Exception e) {
            return Result.error("更新状态失败: " + e.getMessage());
        }
    }
    
    // 获取分类统计信息
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getCategoryStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = categoryService.getCategoryStats();
            return Result.success(stats);
            
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
}