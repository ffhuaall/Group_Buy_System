import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { addToCart as addToCartApi, getUserCart, updateCartItem, removeFromCart as removeFromCartApi } from '@/api/cart'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])
  const userStore = useUserStore()
  
  const items = computed(() => cartItems.value)
  const totalPrice = computed(() => {
    return cartItems.value.reduce((total, item) => {
      return total + (item.product?.groupPrice || 0) * item.quantity
    }, 0)
  })
  
  const addToCart = async (productId, quantity = 1) => {
    if (!userStore.isLoggedIn || !userStore.user?.id) {
      throw new Error('请先登录')
    }
    
    try {
      await addToCartApi({
        userId: userStore.user.id,
        productId,
        quantity
      })
      await loadCart()
    } catch (error) {
      throw error
    }
  }
  
  const loadCart = async () => {
    if (!userStore.isLoggedIn || !userStore.user?.id) {
      cartItems.value = []
      return
    }
    
    try {
      const response = await getUserCart(userStore.user.id)
      cartItems.value = response || []
    } catch (error) {
      console.error('加载购物车失败:', error)
      cartItems.value = []
    }
  }
  
  const updateQuantity = async (itemId, quantity) => {
    if (!userStore.user?.id) {
      throw new Error('用户未登录')
    }
    
    try {
      const item = cartItems.value.find(item => item.id === itemId)
      if (item) {
        await updateCartItem({
          userId: userStore.user.id,
          productId: item.productId,
          quantity
        })
        await loadCart()
      }
    } catch (error) {
      throw error
    }
  }
  
  const removeFromCart = async (itemId) => {
    if (!userStore.user?.id) {
      throw new Error('用户未登录')
    }
    
    try {
      const item = cartItems.value.find(item => item.id === itemId)
      if (item) {
        await removeFromCartApi(userStore.user.id, item.productId)
        await loadCart()
      }
    } catch (error) {
      throw error
    }
  }

  const clearCart = async () => {
    if (!userStore.user?.id) {
      throw new Error('用户未登录')
    }
    
    try {
      const { clearCart: clearCartApi } = await import('@/api/cart')
      await clearCartApi(userStore.user.id)
      cartItems.value = []
    } catch (error) {
      console.error('清空购物车失败:', error)
      throw error
    }
  }

  return {
    cartItems,
    items,
    totalPrice,
    addToCart,
    loadCart,
    updateQuantity,
    removeFromCart,
    clearCart
  }
})