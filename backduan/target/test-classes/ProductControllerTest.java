package com.groupbuy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Product;
import com.groupbuy.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商品控制器单元测试
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // 创建测试商品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("新鲜苹果");
        testProduct.setDescription("优质红富士苹果，口感甜脆");
        testProduct.setCategoryId(1L);
        testProduct.setOriginalPrice(new BigDecimal("12.99"));
        testProduct.setGroupPrice(new BigDecimal("9.99"));
        testProduct.setStock(100);
        testProduct.setMinGroupSize(10);
        testProduct.setStatus("ON_SALE");
        testProduct.setImages("apple1.jpg,apple2.jpg");
        testProduct.setUnit("斤");
        testProduct.setWeight(new BigDecimal("1.0"));
        testProduct.setSalesCount(50);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        // 创建商品列表
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("有机香蕉");
        product2.setDescription("进口有机香蕉，营养丰富");
        product2.setCategoryId(1L);
        product2.setOriginalPrice(new BigDecimal("8.99"));
        product2.setGroupPrice(new BigDecimal("6.99"));
        product2.setStock(80);
        product2.setMinGroupSize(15);
        product2.setStatus("ON_SALE");
        product2.setImages("banana1.jpg");
        product2.setUnit("斤");
        product2.setWeight(new BigDecimal("1.0"));
        product2.setSalesCount(30);
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());

        productList = Arrays.asList(testProduct, product2);
    }

    @Test
    void testGetProductById_Success() throws Exception {
        // Given
        when(productService.getById(1L)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/product/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("新鲜苹果"))
                .andExpect(jsonPath("$.data.originalPrice").value(12.99))
                .andExpect(jsonPath("$.data.groupPrice").value(9.99))
                .andExpect(jsonPath("$.data.stock").value(100));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        // Given
        when(productService.getById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/product/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("商品不存在"));
    }

    @Test
    void testGetProductList_Success() throws Exception {
        // Given
        Page<Product> productPage = new Page<>(1, 10);
        productPage.setRecords(productList);
        productPage.setTotal(2);
        
        when(productService.page(any(Page.class), any()))
            .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/product/list")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    void testGetProductsByCategory_Success() throws Exception {
        // Given
        when(productService.getProductsByCategory(eq(1L), any(Page.class)))
            .thenReturn(productList);

        // When & Then
        mockMvc.perform(get("/api/product/category/1")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetHotProducts_Success() throws Exception {
        // Given
        when(productService.getHotProducts(10)).thenReturn(productList);

        // When & Then
        mockMvc.perform(get("/api/product/hot")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testSearchProducts_Success() throws Exception {
        // Given
        Page<Product> searchResult = new Page<>(1, 10);
        searchResult.setRecords(Arrays.asList(testProduct));
        searchResult.setTotal(1);
        
        when(productService.searchProducts(eq("苹果"), any(Page.class)))
            .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(get("/api/product/search")
                .param("keyword", "苹果")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.records[0].name").value("新鲜苹果"));
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        // Given
        when(productService.save(any(Product.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/product/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("商品创建成功"));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        // Given
        when(productService.updateById(any(Product.class))).thenReturn(true);

        // When & Then
        testProduct.setName("更新后的苹果");
        testProduct.setGroupPrice(new BigDecimal("8.99"));
        
        mockMvc.perform(put("/api/product/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("商品更新成功"));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        // Given
        when(productService.removeById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/product/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("商品删除成功"));
    }

    @Test
    void testUpdateProductStock_Success() throws Exception {
        // Given
        when(productService.updateStock(1L, 50)).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/product/1/stock")
                .param("stock", "50")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("库存更新成功"));
    }

    @Test
    void testUpdateProductStatus_Success() throws Exception {
        // Given
        when(productService.updateStatus(1L, "OFF_SALE")).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/product/1/status")
                .param("status", "OFF_SALE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("商品状态更新成功"));
    }

    @Test
    void testGetProductRecommendations_Success() throws Exception {
        // Given
        when(productService.getRecommendations(1L, 5))
            .thenReturn(Arrays.asList(productList.get(1)));

        // When & Then
        mockMvc.perform(get("/api/product/1/recommendations")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}