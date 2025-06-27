<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="banner">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
          <div class="banner-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    <!-- 商品分类 -->
    <div class="categories section">
      <div class="section-header">
        <h3>商品分类</h3>
      </div>
      <el-row :gutter="30">
        <el-col :xl="4" :lg="6" :md="8" :sm="12" :xs="24" v-for="category in categories" :key="category.id">
          <el-card class="category-card" @click="goToProducts(category.id)">
            <div class="category-icon">
              <el-icon size="50"><ShoppingBag /></el-icon>
            </div>
            <h4>{{ category.name }}</h4>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 热门商品 -->
    <div class="hot-products section">
      <div class="section-header">
        <h3>热门商品</h3>
      </div>
      <el-row :gutter="30">
        <el-col :xl="6" :lg="8" :md="12" :sm="24" v-for="product in hotProducts" :key="product.id">
          <ProductCard :product="product" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { getHotProducts } from '@/api/product'
import { getCategories } from '@/api/category'

const router = useRouter()
const hotProducts = ref([])
const categories = ref([])
const banners = ref([
  {
    id: 1,
    title: '新鲜水果',
    description: '产地直供，新鲜到家',
    image: '/images/products/login_register_in.png'
  },
  {
    id: 2,
    title: '优质蔬菜',
    description: '绿色健康，营养丰富',
    image: '/images/products/login_register.png'
  }
])

const goToProducts = (categoryId) => {
  router.push({ name: 'ProductList', query: { categoryId } })
}

const loadData = async () => {
  try {
    const [productsData, categoriesData] = await Promise.all([
      getHotProducts(),
      getCategories()
    ])
    hotProducts.value = productsData
    categories.value = categoriesData
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home {
  padding: 0;
}

.banner {
  margin-bottom: 60px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.banner-item {
  height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
}

.banner-content {
  text-align: center;
  color: white;
  z-index: 1;
}

.banner-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.banner-content p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.section {
  margin-bottom: 80px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h3 {
  font-size: 2rem;
  color: #303133;
  font-weight: 600;
  position: relative;
  display: inline-block;
  padding-bottom: 10px;
}

.section-header h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #409eff, #67c23a);
  border-radius: 2px;
}

.category-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-radius: 12px;
  margin-bottom: 20px;
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(64, 158, 255, 0.2);
}

.category-icon {
  margin-bottom: 15px;
  color: #409eff;
}

.category-card h4 {
  font-size: 1.1rem;
  color: #303133;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner {
    margin-bottom: 40px;
  }
  
  .banner-item {
    height: 250px;
  }
  
  .banner-content h2 {
    font-size: 1.8rem;
  }
  
  .section {
    margin-bottom: 50px;
  }
  
  .section-header h3 {
    font-size: 1.5rem;
  }
}
</style>