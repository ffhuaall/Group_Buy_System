<template>
  <div class="cart-container">
    <div class="page-header">
      <h2>购物车</h2>
    </div>
    
    <div v-if="cartItems && cartItems.length > 0" class="cart-content">
      <el-card class="cart-table-card">
        <el-table :data="cartItems" style="width: 100%" size="large">
          <el-table-column type="selection" width="55" />
          
          <el-table-column label="商品" min-width="350">
            <template #default="{ row }">
              <div class="product-info">
                <div 
                  class="product-image-placeholder"
                  :style="{ backgroundColor: getProductColor(row.product?.id || row.productId) }"
                >
                  <span>{{ (row.product?.name || '商品').charAt(0) }}</span>
                </div>
                <div class="product-details">
                  <div class="product-name">{{ row.product?.name || '商品名称' }}</div>
                  <div class="product-desc">{{ row.product?.description || '商品描述' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="单价" width="150" align="center">
            <template #default="{ row }">
              <span class="price">¥{{ row.product?.groupPrice || row.product?.price || 0 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="数量" width="180" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.product.stock"
                size="large"
                @change="updateQuantity(row)"
              />
            </template>
          </el-table-column>
          
          <el-table-column label="小计" width="150" align="center">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.product.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button
                type="danger"
                size="small"
                @click="removeFromCart(row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <el-card class="cart-summary">
        <div class="summary-content">
          <div class="total-info">
            <span class="total-label">总计：</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="checkout">
            去结算
          </el-button>
        </div>
      </el-card>
    </div>
    
    <div v-else class="empty-cart">
      <el-empty description="购物车为空">
        <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const cartStore = useCartStore()
const userStore = useUserStore()
const router = useRouter()

const cartItems = computed(() => cartStore.items || [])
const totalPrice = computed(() => cartStore.totalPrice || 0)

const updateQuantity = async (item) => {
  try {
    await cartStore.updateQuantity(item.id, item.quantity)
  } catch (error) {
    ElMessage.error('更新数量失败')
  }
}

const removeFromCart = async (itemId) => {
  try {
    await cartStore.removeFromCart(itemId)
    ElMessage.success('已从购物车移除')
  } catch (error) {
    ElMessage.error('移除失败')
  }
}

const checkout = () => {
  if (!cartItems.value || cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  router.push('/checkout')
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.loadCart()
  }
})

const getProductColor = (productId) => {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff']
  return colors[(productId || 0) % colors.length]
}
</script>

<style scoped>
.product-image-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin-right: 15px;
}

.cart-container {
  padding: 30px 0;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h2 {
  font-size: 2rem;
  color: #303133;
  font-weight: 600;
}

.cart-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-table-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.product-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.4;
}

.price {
  font-size: 16px;
  color: #e6a23c;
  font-weight: 600;
}

.subtotal {
  font-size: 18px;
  color: #f56c6c;
  font-weight: 700;
}

.cart-summary {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.summary-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.total-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-label {
  font-size: 18px;
  color: #303133;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: 700;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-container {
    padding: 20px 10px;
  }
  
  .summary-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .product-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>