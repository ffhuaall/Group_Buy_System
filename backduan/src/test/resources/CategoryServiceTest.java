package com.groupbuy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.groupbuy.entity.Category;
import com.groupbuy.service.impl.CategoryServiceImpl;
import com.groupbuy.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 分类服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        // 创建父分类
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("生鲜食品");
        parentCategory.setParentId(0L);
        parentCategory.setLevel(1);
        parentCategory.setSort(1);
        parentCategory.setStatus("ACTIVE");
        parentCategory.setIcon("fresh-food.png");
        parentCategory.setDescription("新鲜食品分类");
        parentCategory.setCreatedAt(LocalDateTime.now());
        parentCategory.setUpdatedAt(LocalDateTime.now());

        // 创建子分类
        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("蔬菜");
        childCategory.setParentId(1L);
        childCategory.setLevel(2);
        childCategory.setSort(1);
        childCategory.setStatus("ACTIVE");
        childCategory.setIcon("vegetables.png");
        childCategory.setDescription("新鲜蔬菜");
        childCategory.setCreatedAt(LocalDateTime.now());
        childCategory.setUpdatedAt(LocalDateTime.now());

        // 创建测试分类
        testCategory = new Category();
        testCategory.setId(3L);
        testCategory.setName("水果");
        testCategory.setParentId(1L);
        testCategory.setLevel(2);
        testCategory.setSort(2);
        testCategory.setStatus("ACTIVE");
        testCategory.setIcon("fruits.png");
        testCategory.setDescription("新鲜水果");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetCategoryById_Success() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(parentCategory);

        // When
        Category result = categoryService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("生鲜食品", result.getName());
        assertEquals(0L, result.getParentId());
        verify(categoryMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Given
        when(categoryMapper.selectById(999L)).thenReturn(null);

        // When
        Category result = categoryService.getById(999L);

        // Then
        assertNull(result);
        verify(categoryMapper, times(1)).selectById(999L);
    }

    @Test
    void testGetAllCategories() {
        // Given
        List<Category> categories = Arrays.asList(parentCategory, childCategory, testCategory);
        when(categoryMapper.selectList(any(QueryWrapper.class))).thenReturn(categories);

        // When
        List<Category> result = categoryService.list();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(categoryMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetCategoriesByParentId() {
        // Given
        List<Category> childCategories = Arrays.asList(childCategory, testCategory);
        when(categoryMapper.selectList(any(QueryWrapper.class))).thenReturn(childCategories);

        // When
        List<Category> result = categoryService.list(
            new QueryWrapper<Category>().eq("parent_id", 1L));

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getParentId());
        assertEquals(1L, result.get(1).getParentId());
        verify(categoryMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetRootCategories() {
        // Given
        when(categoryMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(parentCategory));

        // When
        List<Category> result = categoryService.list(
            new QueryWrapper<Category>().eq("parent_id", 0L));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0L, result.get(0).getParentId());
        assertEquals(1, result.get(0).getLevel());
        verify(categoryMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testSaveCategory_Success() {
        // Given
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        // When
        boolean result = categoryService.save(testCategory);

        // Then
        assertTrue(result);
        verify(categoryMapper, times(1)).insert(testCategory);
    }

    @Test
    void testUpdateCategory_Success() {
        // Given
        when(categoryMapper.updateById(any(Category.class))).thenReturn(1);

        // When
        testCategory.setName("更新后的水果");
        testCategory.setDescription("更新后的描述");
        boolean result = categoryService.updateById(testCategory);

        // Then
        assertTrue(result);
        assertEquals("更新后的水果", testCategory.getName());
        verify(categoryMapper, times(1)).updateById(testCategory);
    }

    @Test
    void testDeleteCategory_Success() {
        // Given
        when(categoryMapper.deleteById(3L)).thenReturn(1);

        // When
        boolean result = categoryService.removeById(3L);

        // Then
        assertTrue(result);
        verify(categoryMapper, times(1)).deleteById(3L);
    }

    @Test
    void testGetCategoriesByStatus() {
        // Given
        List<Category> activeCategories = Arrays.asList(parentCategory, childCategory);
        when(categoryMapper.selectList(any(QueryWrapper.class))).thenReturn(activeCategories);

        // When
        List<Category> result = categoryService.list(
            new QueryWrapper<Category>().eq("status", "ACTIVE"));

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(category -> assertEquals("ACTIVE", category.getStatus()));
        verify(categoryMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetCategoriesByLevel() {
        // Given
        List<Category> level2Categories = Arrays.asList(childCategory, testCategory);
        when(categoryMapper.selectList(any(QueryWrapper.class))).thenReturn(level2Categories);

        // When
        List<Category> result = categoryService.list(
            new QueryWrapper<Category>().eq("level", 2));

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(category -> assertEquals(2, category.getLevel()));
        verify(categoryMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testUpdateCategoryStatus() {
        // Given
        when(categoryMapper.selectById(3L)).thenReturn(testCategory);
        when(categoryMapper.updateById(any(Category.class))).thenReturn(1);

        // When
        testCategory.setStatus("INACTIVE");
        boolean result = categoryService.updateById(testCategory);

        // Then
        assertTrue(result);
        assertEquals("INACTIVE", testCategory.getStatus());
        verify(categoryMapper, times(1)).updateById(testCategory);
    }

    @Test
    void testGetCategoriesOrderBySort() {
        // Given
        List<Category> sortedCategories = Arrays.asList(childCategory, testCategory);
        when(categoryMapper.selectList(any(QueryWrapper.class))).thenReturn(sortedCategories);

        // When
        List<Category> result = categoryService.list(
            new QueryWrapper<Category>().orderByAsc("sort"));

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryMapper, times(1)).selectList(any(QueryWrapper.class));
    }
}