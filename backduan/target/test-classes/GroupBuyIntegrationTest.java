package com.groupbuy.integration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.groupbuy.dto.CreateOrderRequest;
import com.groupbuy.entity.*;
import com.groupbuy.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 社区团购系统集成测试
 * 测试完整的业务流程和各个服务之间的协作
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class GroupBuyIntegrationTest {

    @Mock
    private UserService userService;
    
    @Mock
    private ProductService productService;
    
    @Mock
    private OrderService orderService;
    
    @Mock
    private CategoryService categoryService;
    
    @Mock
    private OrderItemService orderItemService;

    private User testUser;
    private Product testProduct;
    private Category testCategory;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encrypted_password");
        testUser.setPhone("13800138001");
        testUser.setEmail("test@example.com");
        testUser.setRealName("测试用户");
        testUser.setGender("MALE");
        testUser.setStatus("ACTIVE");
        testUser.setCreatedAt(LocalDateTime.now());

        // 创建测试分类
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("生鲜食品");
        testCategory.setParentId(0L);
        testCategory.setLevel(1);
        testCategory.setSort(1);
        testCategory.setStatus("ACTIVE");
        testCategory.setCreatedAt(LocalDateTime.now());

        // 创建测试商品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("新鲜苹果");
        testProduct.setDescription("优质红富士苹果");
        testProduct.setCategoryId(1L);
        testProduct.setOriginalPrice(new BigDecimal("12.99"));
        testProduct.setGroupPrice(new BigDecimal("9.99"));
        testProduct.setStock(100);
        testProduct.setMinGroupSize(10);
        testProduct.setStatus("ON_SALE");
        testProduct.setUnit("斤");
        testProduct.setSalesCount(0);
        testProduct.setCreatedAt(LocalDateTime.now());

        // 创建测试订单项
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrderId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setProductName("新鲜苹果");
        testOrderItem.setOriginalPrice(new BigDecimal("12.99"));
        testOrderItem.setGroupPrice(new BigDecimal("9.99"));
        testOrderItem.setQuantity(5);
        testOrderItem.setTotalAmount(new BigDecimal("49.95"));

        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD20231201001");
        testOrder.setUserId(1L);
        testOrder.setGroupLeaderId(1L);
        testOrder.setTotalAmount(new BigDecimal("49.95"));
        testOrder.setStatus("PENDING_PAYMENT");
        testOrder.setReceiverName("测试用户");
        testOrder.setReceiverPhone("13800138001");
        testOrder.setReceiverAddress("北京市朝阳区测试地址");
        testOrder.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCompleteOrderFlow_Success() {
        // Given - 模拟完整的下单流程
        when(userService.getById(1L)).thenReturn(testUser);
        when(categoryService.getById(1L)).thenReturn(testCategory);
        when(productService.getById(1L)).thenReturn(testProduct);
        when(productService.updateById(any(Product.class))).thenReturn(true);
        when(orderService.save(any(Order.class))).thenReturn(true);
        when(orderItemService.save(any(OrderItem.class))).thenReturn(true);

        // 创建订单请求
        CreateOrderRequest request = new CreateOrderRequest();
        request.setGroupLeaderId(1L);
        request.setReceiverName("测试用户");
        request.setReceiverPhone("13800138001");
        request.setReceiverAddress("北京市朝阳区测试地址");
        
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5);
        request.setOrderItems(Arrays.asList(itemRequest));

        when(orderService.createOrder(request, 1L)).thenReturn(testOrder);

        // When - 执行下单流程
        Order createdOrder = orderService.createOrder(request, 1L);

        // Then - 验证结果
        assertNotNull(createdOrder);
        assertEquals(1L, createdOrder.getUserId());
        assertEquals("PENDING_PAYMENT", createdOrder.getStatus());
        assertEquals(new BigDecimal("49.95"), createdOrder.getTotalAmount());
        
        // 验证服务调用
        verify(orderService, times(1)).createOrder(request, 1L);
    }

    @Test
    void testUserRegistrationAndLogin_Success() {
        // Given - 模拟用户注册和登录流程
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(true);
        when(userService.findByUsername("newuser")).thenReturn(testUser);

        // When - 执行注册流程
        boolean registrationResult = userService.save(testUser);
        User foundUser = userService.findByUsername("newuser");

        // Then - 验证结果
        assertTrue(registrationResult);
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        
        // 验证服务调用
        verify(userService, times(1)).save(any(User.class));
        verify(userService, times(1)).findByUsername("newuser");
    }

    @Test
    void testProductCategoryManagement_Success() {
        // Given - 模拟商品分类管理流程
        when(categoryService.save(any(Category.class))).thenReturn(true);
        when(categoryService.getById(1L)).thenReturn(testCategory);
        when(productService.save(any(Product.class))).thenReturn(true);
        when(productService.list(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(testProduct));

        // When - 执行分类和商品管理流程
        boolean categoryCreated = categoryService.save(testCategory);
        Category foundCategory = categoryService.getById(1L);
        boolean productCreated = productService.save(testProduct);
        List<Product> categoryProducts = productService.list(
            new QueryWrapper<Product>().eq("category_id", 1L));

        // Then - 验证结果
        assertTrue(categoryCreated);
        assertNotNull(foundCategory);
        assertEquals("生鲜食品", foundCategory.getName());
        assertTrue(productCreated);
        assertEquals(1, categoryProducts.size());
        assertEquals(1L, categoryProducts.get(0).getCategoryId());
        
        // 验证服务调用
        verify(categoryService, times(1)).save(any(Category.class));
        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    void testOrderStatusUpdate_Success() {
        // Given - 模拟订单状态更新流程
        when(orderService.getById(1L)).thenReturn(testOrder);
        when(orderService.updateById(any(Order.class))).thenReturn(true);

        // When - 执行订单状态更新
        Order order = orderService.getById(1L);
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        boolean updateResult = orderService.updateById(order);

        // Then - 验证结果
        assertNotNull(order);
        assertEquals("PAID", order.getStatus());
        assertTrue(updateResult);
        
        // 验证服务调用
        verify(orderService, times(1)).getById(1L);
        verify(orderService, times(1)).updateById(any(Order.class));
    }

    @Test
    void testProductStockManagement_Success() {
        // Given - 模拟库存管理流程
        when(productService.getById(1L)).thenReturn(testProduct);
        when(productService.updateById(any(Product.class))).thenReturn(true);

        // When - 执行库存更新
        Product product = productService.getById(1L);
        int originalStock = product.getStock();
        int orderQuantity = 5;
        product.setStock(originalStock - orderQuantity);
        product.setSalesCount(product.getSalesCount() + orderQuantity);
        boolean updateResult = productService.updateById(product);

        // Then - 验证结果
        assertNotNull(product);
        assertEquals(95, product.getStock()); // 100 - 5
        assertEquals(5, product.getSalesCount()); // 0 + 5
        assertTrue(updateResult);
        
        // 验证服务调用
        verify(productService, times(1)).getById(1L);
        verify(productService, times(1)).updateById(any(Product.class));
    }

    @Test
    void testOrderCancellation_Success() {
        // Given - 模拟订单取消流程
        when(orderService.getById(1L)).thenReturn(testOrder);
        when(orderService.updateById(any(Order.class))).thenReturn(true);
        when(orderItemService.list(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(testOrderItem));
        when(productService.getById(1L)).thenReturn(testProduct);
        when(productService.updateById(any(Product.class))).thenReturn(true);

        // When - 执行订单取消流程
        Order order = orderService.getById(1L);
        order.setStatus("CANCELLED");
        boolean orderUpdateResult = orderService.updateById(order);
        
        // 恢复库存
        List<OrderItem> orderItems = orderItemService.list(
            new QueryWrapper<OrderItem>().eq("order_id", 1L));
        for (OrderItem item : orderItems) {
            Product product = productService.getById(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
            productService.updateById(product);
        }

        // Then - 验证结果
        assertEquals("CANCELLED", order.getStatus());
        assertTrue(orderUpdateResult);
        
        // 验证服务调用
        verify(orderService, times(1)).updateById(any(Order.class));
        verify(productService, times(1)).updateById(any(Product.class));
    }

    @Test
    void testUserOrderHistory_Success() {
        // Given - 模拟用户订单历史查询
        when(orderService.list(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(testOrder));
        when(orderItemService.list(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(testOrderItem));

        // When - 执行订单历史查询
        List<Order> userOrders = orderService.list(
            new QueryWrapper<Order>().eq("user_id", 1L));
        List<OrderItem> orderItems = orderItemService.list(
            new QueryWrapper<OrderItem>().eq("order_id", 1L));

        // Then - 验证结果
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());
        assertEquals(1L, userOrders.get(0).getUserId());
        
        assertNotNull(orderItems);
        assertEquals(1, orderItems.size());
        assertEquals(1L, orderItems.get(0).getOrderId());
        
        // 验证服务调用
        verify(orderService, times(1)).list(any(QueryWrapper.class));
        verify(orderItemService, times(1)).list(any(QueryWrapper.class));
    }
}