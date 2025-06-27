package com.groupbuy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.dto.CreateOrderRequest;
import com.groupbuy.entity.Order;
import com.groupbuy.entity.OrderItem;
import com.groupbuy.entity.Product;
import com.groupbuy.service.impl.OrderServiceImpl;
import com.groupbuy.mapper.OrderMapper;
import com.groupbuy.service.OrderItemService;
import com.groupbuy.service.ProductService;
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
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private CreateOrderRequest createOrderRequest;
    private Product testProduct;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // 创建测试商品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setOriginalPrice(new BigDecimal("99.99"));
        testProduct.setGroupPrice(new BigDecimal("79.99"));
        testProduct.setStock(100);
        testProduct.setStatus("ON_SALE");

        // 创建测试订单项
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrderId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setProductName("测试商品");
        testOrderItem.setOriginalPrice(new BigDecimal("99.99"));
        testOrderItem.setGroupPrice(new BigDecimal("79.99"));
        testOrderItem.setQuantity(2);
        testOrderItem.setTotalAmount(new BigDecimal("159.98"));

        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD20231201001");
        testOrder.setUserId(1L);
        testOrder.setGroupLeaderId(1L);
        testOrder.setTotalAmount(new BigDecimal("159.98"));
        testOrder.setStatus("PENDING_PAYMENT");
        testOrder.setReceiverName("张三");
        testOrder.setReceiverPhone("13800138001");
        testOrder.setReceiverAddress("北京市朝阳区测试地址");
        testOrder.setCreatedAt(LocalDateTime.now());

        // 创建订单请求
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setGroupLeaderId(1L);
        createOrderRequest.setReceiverName("张三");
        createOrderRequest.setReceiverPhone("13800138001");
        createOrderRequest.setReceiverAddress("北京市朝阳区测试地址");
        createOrderRequest.setRemark("测试订单");
        
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        createOrderRequest.setOrderItems(Arrays.asList(itemRequest));
    }

    @Test
    void testCreateOrder_Success() {
        // Given
        when(productService.getById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemService.save(any(OrderItem.class))).thenReturn(true);
        when(productService.updateById(any(Product.class))).thenReturn(true);

        // When
        Order result = orderService.createOrder(createOrderRequest, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getGroupLeaderId());
        assertEquals("张三", result.getReceiverName());
        assertEquals("PENDING_PAYMENT", result.getStatus());
        verify(orderMapper, times(1)).insert(any(Order.class));
        verify(orderItemService, times(1)).save(any(OrderItem.class));
    }

    @Test
    void testGetOrderById_Success() {
        // Given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // When
        Order result = orderService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ORD20231201001", result.getOrderNo());
        verify(orderMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Given
        when(orderMapper.selectById(999L)).thenReturn(null);

        // When
        Order result = orderService.getById(999L);

        // Then
        assertNull(result);
        verify(orderMapper, times(1)).selectById(999L);
    }

    @Test
    void testGetUserOrders() {
        // Given
        Page<Order> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testOrder));
        mockPage.setTotal(1);
        
        when(orderMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
            .thenReturn(mockPage);

        // When
        Page<Order> result = orderService.page(new Page<>(1, 10), 
            new QueryWrapper<Order>().eq("user_id", 1L));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(1L, result.getRecords().get(0).getUserId());
        verify(orderMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testUpdateOrderStatus() {
        // Given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // When
        testOrder.setStatus("PAID");
        boolean result = orderService.updateById(testOrder);

        // Then
        assertTrue(result);
        assertEquals("PAID", testOrder.getStatus());
        verify(orderMapper, times(1)).updateById(testOrder);
    }

    @Test
    void testCancelOrder() {
        // Given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // When
        testOrder.setStatus("CANCELLED");
        boolean result = orderService.updateById(testOrder);

        // Then
        assertTrue(result);
        assertEquals("CANCELLED", testOrder.getStatus());
        verify(orderMapper, times(1)).updateById(testOrder);
    }

    @Test
    void testGetOrdersByStatus() {
        // Given
        when(orderMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(testOrder));

        // When
        List<Order> result = orderService.list(
            new QueryWrapper<Order>().eq("status", "PENDING_PAYMENT"));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING_PAYMENT", result.get(0).getStatus());
        verify(orderMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testDeleteOrder() {
        // Given
        when(orderMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = orderService.removeById(1L);

        // Then
        assertTrue(result);
        verify(orderMapper, times(1)).deleteById(1L);
    }
}