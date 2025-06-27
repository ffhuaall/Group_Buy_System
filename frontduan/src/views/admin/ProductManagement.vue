<template>
  <div class="product-management">
    <div class="page-header">
      <h2>商品管理</h2>
      <div class="header-actions">
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已上架" value="ON_SALE" />
          <el-option label="已下架" value="OFF_SALE" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="loadProducts"
        >
          <template #append>
            <el-button @click="loadProducts">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-card>
      <el-table :data="products" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="getProductImageUrl(row.name)"
              :alt="row.name"
              style="width: 60px; height: 60px; border-radius: 4px;"
              fit="cover"
              :preview-src-list="[getProductImageUrl(row.name)]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" width="200" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="supplierName" label="供应商" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewProduct(row)">
              查看
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="success"
              @click="approveProduct(row.id)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="danger"
              @click="rejectProduct(row.id)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'ON_SALE'"
              size="small"
              type="warning"
              @click="offShelfProduct(row.id)"
            >
              下架
            </el-button>
            <el-button
              v-if="row.status === 'OFF_SALE'"
              size="small"
              type="success"
              @click="onShelfProduct(row.id)"
            >
              上架
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadProducts"
          @current-change="loadProducts"
        />
      </div>
    </el-card>
    
    <!-- 商品详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="商品详情" width="800px">
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-image
              :src="getProductImageUrl(selectedProduct.name)"
              :alt="selectedProduct.name"
              style="width: 100%; border-radius: 8px;"
              fit="cover"
              :preview-src-list="[getProductImageUrl(selectedProduct.name)]"
            />
          </el-col>
          <el-col :span="16">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="商品名称">{{ selectedProduct.name }}</el-descriptions-item>
              <el-descriptions-item label="价格">¥{{ selectedProduct.price }}</el-descriptions-item>
              <el-descriptions-item label="库存">{{ selectedProduct.stock }}</el-descriptions-item>
              <el-descriptions-item label="分类">{{ selectedProduct.categoryName }}</el-descriptions-item>
              <el-descriptions-item label="供应商">{{ selectedProduct.supplierName }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="getStatusType(selectedProduct.status)">
                  {{ getStatusText(selectedProduct.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="销量">{{ selectedProduct.salesCount }}</el-descriptions-item>
              <el-descriptions-item label="浏览量">{{ selectedProduct.viewCount }}</el-descriptions-item>
              <el-descriptions-item label="创建时间" :span="2">
                {{ formatDate(selectedProduct.createdAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="商品描述" :span="2">
                {{ selectedProduct.description }}
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  getProductList, 
  approveProduct as approveProductAPI, 
  rejectProduct as rejectProductAPI,
  updateProductStatus 
} from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const products = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const statusFilter = ref('')
const showDetailDialog = ref(false)
const selectedProduct = ref(null)

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await getProductList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      status: statusFilter.value
    })
    products.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const viewProduct = (product) => {
  selectedProduct.value = product
  showDetailDialog.value = true
}

const approveProduct = async (productId) => {
  try {
    await approveProductAPI(productId)
    ElMessage.success('商品审核通过')
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const rejectProduct = async (productId) => {
  try {
    await ElMessageBox.prompt('请输入拒绝原因', '拒绝商品', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await rejectProductAPI(productId)
    ElMessage.success('商品已拒绝')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const offShelfProduct = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要下架该商品吗？', '确认下架', {
      type: 'warning'
    })
    await updateProductStatus(productId, 'OFF_SALE')
    ElMessage.success('商品已下架')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const onShelfProduct = async (productId) => {
  try {
    await updateProductStatus(productId, 'ON_SALE')
    ElMessage.success('商品已上架')
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    ON_SALE: 'success',
    OFF_SALE: 'info',
    REJECTED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    PENDING: '待审核',
    ON_SALE: '已上架',
    OFF_SALE: '已下架',
    REJECTED: '已拒绝'
  }
  return textMap[status] || '未知状态'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

// 根据商品名称获取对应的图片URL
const getProductImageUrl = (productName) => {
  // 商品名称到图片文件名的映射
  const imageMap = {
    '有机苹果': 'apple.png',
    '新鲜香蕉': 'banana.png',
    '优质大米': 'rice.png',
    '农家鸡蛋': 'eggs.png',
    '纯牛奶': 'milk.png',
    '新鲜蔬菜': 'vegetables.png',
    '土豆': 'potato.png',
    '胡萝卜': 'carrot.png',
    '白菜': 'cabbage.png',
    '番茄': 'tomato.png',
    '黄瓜': 'cucumber.png',
    '洋葱': 'onion.png',
    '青椒': 'pepper.png',
    '茄子': 'eggplant.png',
    '豆腐': 'tofu.png',
    '面条': 'noodles.png',
    '食用油': 'oil.png',
    '酱油': 'soy_sauce.png',
    '醋': 'vinegar.png',
    '盐': 'salt.png',
    '糖': 'sugar.png',
    '面粉': 'flour.png',
    '绿茶': 'green_tea.png',
    '咖啡': 'coffee.png',
    '蜂蜜': 'honey.png',
    '坚果': 'nuts.png',
    '干果': 'dried_fruit.png',
    '饼干': 'cookies.png',
    '巧克力': 'chocolate.png',
    '果汁': 'juice.png'
  }
  
  // 根据商品名称查找对应的图片文件名
  const fileName = imageMap[productName] || 'default.png'
  
  // 返回完整的图片URL
  return `/images/products/${fileName}`
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-management {
  width: 100%;
  max-width: none;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.product-detail {
  padding: 20px;
}
</style>