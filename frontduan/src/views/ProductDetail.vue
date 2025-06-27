<template>
  <div class="product-detail">
    <el-row :gutter="20" v-if="product">
      <el-col :span="12">
        <div class="product-images">
          <!-- 替换图片为纯色背景 -->
          <div class="product-image-placeholder">
            <el-icon class="placeholder-icon"><Picture /></el-icon>
            <span class="placeholder-text">{{ product.name }}</span>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="product-info">
          <h1>{{ product.name }}</h1>
          <div class="price">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          
          <div class="product-meta">
            <p><strong>分类：</strong>{{ product.categoryName || '未分类' }}</p>
            <p><strong>供应商：</strong>{{ product.supplierName || '未知' }}</p>
            <p><strong>库存：</strong>{{ product.stock }}件</p>
            <p><strong>单位：</strong>{{ product.unit || '件' }}</p>
          </div>
          
          <div class="description">
            <h3>商品描述</h3>
            <p>{{ product.description || '暂无描述' }}</p>
          </div>
          
          <div class="actions">
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock"
              class="quantity-input"
            />
            <el-button
              type="primary"
              size="large"
              @click="addToCart"
              :loading="loading"
              :disabled="product.stock === 0"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-skeleton v-else :rows="10" animated />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const route = useRoute()
const cartStore = useCartStore()

const product = ref(null)
const quantity = ref(1)
const loading = ref(false)

const loadProduct = async () => {
  try {
    const response = await getProductDetail(route.params.id)
    // 处理响应数据结构
    product.value = response.data || response
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  }
}

const addToCart = async () => {
  loading.value = true
  try {
    await cartStore.addToCart(product.value.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
    ElMessage.error('加入购物车失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.product-images {
  text-align: center;
}

.product-image-placeholder {
  width: 100%;
  height: 400px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  border-radius: 12px;
  margin-bottom: 20px;
}

.placeholder-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.8;
}

.placeholder-text {
  font-size: 18px;
  font-weight: 500;
  text-align: center;
  padding: 0 20px;
  opacity: 0.9;
}

.product-info {
  padding-left: 20px;
}

.product-info h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #303133;
}

.price {
  margin-bottom: 20px;
}

.current-price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
  margin-right: 15px;
}

.original-price {
  font-size: 18px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.product-meta {
  margin-bottom: 30px;
}

.product-meta p {
  margin: 10px 0;
  font-size: 16px;
  color: #606266;
}

.description {
  margin-bottom: 30px;
}

.description h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #303133;
}

.description p {
  font-size: 16px;
  line-height: 1.6;
  color: #606266;
}

.actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quantity-input {
  width: 120px;
}

@media (max-width: 768px) {
  .product-detail {
    padding: 10px;
  }
  
  .product-info {
    padding-left: 0;
    margin-top: 20px;
  }
  
  .product-image-placeholder {
    height: 300px;
  }
  
  .actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .quantity-input {
    width: 100%;
  }
}
</style>