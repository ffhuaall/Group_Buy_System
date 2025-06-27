<template>
  <div class="product-list-container">
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-row :gutter="20" align="middle">
        <el-col :xl="8" :lg="8" :md="12" :sm="24">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索商品"
            clearable
            size="large"
            @keyup.enter="searchProducts"
          >
            <template #append>
              <el-button @click="searchProducts">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xl="5" :lg="5" :md="6" :sm="12">
          <el-select 
            v-model="searchForm.categoryId" 
            placeholder="选择分类" 
            clearable 
            size="large"
            style="width: 100%"
            @change="searchProducts"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-col>
        <el-col :xl="5" :lg="5" :md="6" :sm="12">
          <el-select 
            v-model="searchForm.sortBy" 
            placeholder="排序方式" 
            size="large"
            style="width: 100%"
            @change="searchProducts"
          >
            <el-option label="默认排序" value="" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="销量最高" value="sales_desc" />
          </el-select>
        </el-col>
        <el-col :xl="6" :lg="6" :md="24" :sm="24">
          <el-button type="primary" size="large" style="width: 100%" @click="searchProducts">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 商品列表 -->
    <div class="products-grid">
      <el-row :gutter="30">
        <el-col :xl="6" :lg="8" :md="12" :sm="24" v-for="product in products" :key="product.id">
          <ProductCard :product="product" />
        </el-col>
      </el-row>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[12, 24, 48]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { getProductList } from '@/api/product'
import { getCategories } from '@/api/category'

const route = useRoute()
const products = ref([])
const categories = ref([])
const loading = ref(false)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  sortBy: ''
})

const pagination = reactive({
  current: 1,
  size: 12,
  total: 0
})

const searchProducts = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword,
      categoryId: searchForm.categoryId
    }
    
    const data = await getProductList(params)
    products.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('搜索商品失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    categories.value = await getCategories()
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  searchProducts()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  searchProducts()
}

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  if (newQuery.categoryId) {
    searchForm.categoryId = parseInt(newQuery.categoryId)
  }
  if (newQuery.keyword) {
    searchForm.keyword = newQuery.keyword
  }
  searchProducts()
}, { immediate: true })

onMounted(() => {
  loadCategories()
  searchProducts()
})
</script>

<style scoped>
.product-list-container {
  padding: 30px 0;
}

.search-card {
  margin-bottom: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.search-card :deep(.el-card__body) {
  padding: 30px;
}

.products-grid {
  margin-bottom: 50px;
  min-height: 400px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 30px 0;
  background: #f8f9fa;
  border-radius: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-list-container {
    padding: 20px 0;
  }
  
  .search-card :deep(.el-card__body) {
    padding: 20px;
  }
  
  .products-grid {
    margin-bottom: 30px;
  }
}
</style>