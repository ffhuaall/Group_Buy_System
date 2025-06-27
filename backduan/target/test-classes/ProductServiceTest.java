package com.groupbuy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Product;
import com.groupbuy.service.impl.ProductServiceImpl;
import com.groupbuy.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 商品服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setDescription("这是一个测试商品");
        testProduct.setOriginalPrice(new BigDecimal("99.99"));
        testProduct.setGroupPrice(new BigDecimal("79.99"));
        testProduct.setStock(100);
        testProduct.setStatus("ON_SALE");
        testProduct.setCategoryId(1L);
        testProduct.setSupplierId(1L);
        testProduct.setSalesCount(10);
        testProduct.setViewCount(50);
        testProduct.setCreatedAt(LocalDateTime.now());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("热门商品");
        product2.setDescription("这是一个热门商品");
        product2.setOriginalPrice(new BigDecimal("199.99"));
        product2.setGroupPrice(new BigDecimal("159.99"));
        product2.setStock(50);
        product2.setStatus("ON_SALE");
        product2.setCategoryId(2L);
        product2.setSupplierId(1L);
        product2.setSalesCount(100);
        product2.setViewCount(200);
        product2.setCreatedAt(LocalDateTime.now());

        productList = Arrays.asList(testProduct, product2);
    }

    @Test
    void testGetProductPage_WithAllFilters() {
        // Given
        Page<Product> mockPage = new Page<>(1, 10);
        mockPage.setRecords(productList);
        mockPage.setTotal(2);
        
        when(productMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
            .thenReturn(mockPage);

        // When
        Page<Product> result = productService.getProductPage(1, 10, 1L, "测试", "ON_SALE");

        // Then
        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(2, result.getTotal());
        verify(productMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testGetProductPage_WithoutFilters() {
        // Given
        Page<Product> mockPage = new Page<>(1, 10);
        mockPage.setRecords(productList);
        mockPage.setTotal(2);
        
        when(productMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
            .thenReturn(mockPage);

        // When
        Page<Product> result = productService.getProductPage(1, 10, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(productMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testGetHotProducts() {
        // Given
        when(productMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(productList);

        // When
        List<Product> result = productService.getHotProducts(5);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetProductById_Success() {
        // Given
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        // When
        Product result = productService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试商品", result.getName());
        verify(productMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When
        Product result = productService.getById(999L);

        // Then
        assertNull(result);
        verify(productMapper, times(1)).selectById(999L);
    }

    @Test
    void testUpdateProductStock() {
        // Given
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        // When
        boolean result = productService.updateById(testProduct);

        // Then
        assertTrue(result);
        verify(productMapper, times(1)).updateById(testProduct);
    }

    @Test
    void testSaveProduct() {
        // Given
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        // When
        boolean result = productService.save(testProduct);

        // Then
        assertTrue(result);
        verify(productMapper, times(1)).insert(testProduct);
    }

    @Test
    void testDeleteProduct() {
        // Given
        when(productMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = productService.removeById(1L);

        // Then
        assertTrue(result);
        verify(productMapper, times(1)).deleteById(1L);
    }

    @Test
    void testGetProductsByCategoryId() {
        // Given
        when(productMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(testProduct));

        // When
        List<Product> result = productService.list(new QueryWrapper<Product>().eq("category_id", 1L));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCategoryId());
        verify(productMapper, times(1)).selectList(any(QueryWrapper.class));
    }
}