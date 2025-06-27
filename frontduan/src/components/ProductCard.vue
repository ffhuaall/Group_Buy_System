<template>
  <el-card class="product-card" @click="goToDetail">
    <div class="product-image">
      <el-image
        :src="getProductImageUrl(product.name)"
        :alt="product.name"
        fit="cover"
        style="width: 100%; height: 100%;"
        :preview-src-list="[getProductImageUrl(product.name)]"
      >
        <template #error>
          <div class="product-placeholder" :style="{ background: getProductColor(product.id) }">
            <el-icon class="placeholder-icon"><Picture /></el-icon>
            <span class="placeholder-text">{{ product.name }}</span>
          </div>
        </template>
      </el-image>
      <div class="product-overlay">
        <el-button type="primary" circle @click.stop="addToCart">
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </div>
    </div>
    <div class="product-info">
      <h4 class="product-name">{{ product.name }}</h4>
      <p class="product-desc">{{ product.description }}</p>
      <div class="product-price">
        <span class="current-price">¥{{ product.price }}</span>
        <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
      </div>
      <div class="product-actions">
        <el-button type="primary" size="default" @click.stop="addToCart" style="width: 100%">
          <el-icon><ShoppingCart /></el-icon>
          加入购物车
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()

const goToDetail = () => {
  router.push(`/product/${props.product.id}`)
}

const addToCart = async () => {
  try {
    await cartStore.addToCart(props.product.id, 1)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  }
}

// 根据商品名称获取对应的图片URL
const getProductImageUrl = (productName) => {
  const imageMap = {
    '新鲜土豆': 'potato.png',
    '有机蔬菜': 'vegetables.png',
    '精选西红柿': 'tomato.png',
    '新鲜青椒': 'pepper.png',
    '新鲜香蕉': 'banana.png',
    '新鲜苹果': 'apple.png',
    '优质大米': 'rice.png',
    '农家鸡蛋': 'eggs.png',
    '纯牛奶': 'milk.png',
    '甜橙': '甜橙.png',
    '新疆哈密瓜': '新疆哈密瓜.png',
    '新鲜草莓': '新鲜草莓.png',
    '进口奇异果': '进口奇异果.png',
    '新鲜牛肉': '新鲜牛肉.png',
    '新鲜猪肉': '新鲜猪肉.png',
    '新鲜羊肉': '新鲜羊肉.png',
    '洗洁精': '洗洁精.png',
    '洗衣液': '洗衣液.png',
    '厨房清洁剂': '厨房清洁剂.png',
    '多功能清洁剂': '多功能清洁剂.png',
    '胡萝卜': 'carrot.png',
    '白菜': 'cabbage.png',
    '黄瓜': 'cucumber.png',
    '茄子': 'eggplant.png',
    '洋葱': 'onion.png'
  }
  
  const fileName = imageMap[productName] || 'vegetables.png'
  return `/images/products/${fileName}`
}

// 根据商品ID生成颜色
const getProductColor = (productId) => {
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'
  ]
  return colors[productId % colors.length]
}
</script>

<style scoped>
.product-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.product-image {
  height: 220px;
  overflow: hidden;
  position: relative;
  background: #f8f9fa;
}

.product-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s ease;
}

.product-card:hover .product-placeholder {
  transform: scale(1.05);
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 10px;
  opacity: 0.8;
}

.placeholder-text {
  font-size: 14px;
  font-weight: 500;
  text-align: center;
  padding: 0 10px;
  opacity: 0.9;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 90%;
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 10px 0;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.product-desc {
  font-size: 14px;
  color: #909399;
  margin: 0 0 15px 0;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-price {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: 700;
}

.original-price {
  font-size: 14px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.product-actions {
  margin-top: auto;
}

@media (max-width: 768px) {
  .product-image {
    height: 180px;
  }
  
  .product-info {
    padding: 15px;
  }
  
  .product-name {
    font-size: 15px;
  }
  
  .current-price {
    font-size: 16px;
  }
}
</style>