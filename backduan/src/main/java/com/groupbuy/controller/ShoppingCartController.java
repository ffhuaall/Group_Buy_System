package com.groupbuy.controller;

import com.groupbuy.common.Result;
import com.groupbuy.dto.CartItemDTO;
import com.groupbuy.dto.CartItemRequest;
import com.groupbuy.entity.ShoppingCart;
import com.groupbuy.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    
    @Autowired
    private ShoppingCartService shoppingCartService;
    
    @GetMapping("/list/{userId}")
    public Result<List<CartItemDTO>> getUserCart(@PathVariable Long userId) {
        List<CartItemDTO> cartItems = shoppingCartService.getUserCart(userId);
        return Result.success(cartItems);
    }
    
    @PostMapping("/add")
    public Result<ShoppingCart> addToCart(@Valid @RequestBody CartItemRequest request) {
        ShoppingCart cartItem = shoppingCartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return Result.success(cartItem);
    }
    
    @PutMapping("/update")
    public Result<ShoppingCart> updateCartItem(@Valid @RequestBody CartItemRequest request) {
        ShoppingCart cartItem = shoppingCartService.updateCartItem(request.getUserId(), request.getProductId(), request.getQuantity());
        return Result.success(cartItem);
    }
    
    @DeleteMapping("/remove/{userId}/{productId}")
    public Result<Void> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        shoppingCartService.removeFromCart(userId, productId);
        return Result.success();
    }
    
    @DeleteMapping("/clear")
    public Result clearCart(@RequestParam Long userId) {
        shoppingCartService.clearCart(userId);
        return Result.success("购物车已清空");
    }
    
    @DeleteMapping("/batch-remove")
    public Result batchRemoveFromCart(@RequestParam Long userId, @RequestBody List<Long> productIds) {
        shoppingCartService.batchRemoveFromCart(userId, productIds);
        return Result.success("批量删除成功");
    }
    
    @PutMapping("/batch-update")
    public Result batchUpdateCartItems(@RequestParam Long userId, @RequestBody List<CartItemDTO> items) {
        shoppingCartService.batchUpdateCartItems(userId, items);
        return Result.success("批量更新成功");
    }
    
    @GetMapping("/count")
    public Result getCartItemCount(@RequestParam Long userId) {
        int count = shoppingCartService.getCartItemCount(userId);
        return Result.success(count);
    }
    
    @GetMapping("/total-amount")
    public Result getCartTotalAmount(@RequestParam Long userId) {
        double totalAmount = shoppingCartService.getCartTotalAmount(userId);
        return Result.success(totalAmount);
    }
    
    @GetMapping("/validate")
    public Result validateCartItems(@RequestParam Long userId) {
        boolean isValid = shoppingCartService.validateCartItems(userId);
        if (isValid) {
            return Result.success("购物车商品验证通过");
        } else {
            List<String> invalidItems = shoppingCartService.getInvalidCartItems(userId);
            return Result.error("购物车存在无效商品: " + String.join(", ", invalidItems));
        }
    }
    
    @GetMapping("/invalid-items")
    public Result getInvalidCartItems(@RequestParam Long userId) {
        List<String> invalidItems = shoppingCartService.getInvalidCartItems(userId);
        return Result.success(invalidItems);
    }
}