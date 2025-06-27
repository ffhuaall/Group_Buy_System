<template>
  <div class="dashboard-container">
    <h2>管理员仪表板</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon product-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.productCount || 0 }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.orderCount || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">¥{{ stats.totalRevenue || 0 }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    

    
    <!-- 待处理事项 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="dashboard-card">
          <template #header>
            <span>待审核商品</span>
          </template>
          <div class="card-content">
            <el-table :data="pendingProducts" style="width: 100%" :height="300">
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="supplierName" label="供应商" />
              <el-table-column prop="createdAt" label="提交时间" />
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button size="small" type="success" @click="approveProduct(row.id)">
                    通过
                  </el-button>
                  <el-button size="small" type="danger" @click="rejectProduct(row.id)">
                    拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="pendingProducts.length === 0" class="empty-state">
              <el-empty description="暂无待审核商品" :image-size="80" />
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="dashboard-card">
          <template #header>
            <span>最新订单</span>
          </template>
          <div class="card-content">
            <el-table :data="recentOrders" style="width: 100%" :height="300">
              <el-table-column prop="orderNumber" label="订单号" />
              <el-table-column prop="userName" label="用户" />
              <el-table-column prop="totalAmount" label="金额" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="recentOrders.length === 0" class="empty-state">
              <el-empty description="暂无最新订单" :image-size="80" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardData, approveProduct as approveProductAPI, rejectProduct as rejectProductAPI } from '@/api/admin'
import { ElMessage } from 'element-plus'

const stats = ref({})
const pendingProducts = ref([])
const recentOrders = ref([])
const loading = ref(false)

const loadDashboardData = async () => {
  loading.value = true
  try {
    const response = await getDashboardData()
    stats.value = response.data.stats || {}
    pendingProducts.value = response.data.pendingProducts || []
    recentOrders.value = response.data.recentOrders || []
  } catch (error) {
    ElMessage.error('加载仪表板数据失败')
  } finally {
    loading.value = false
  }
}

const approveProduct = async (productId) => {
  try {
    await approveProductAPI(productId)
    ElMessage.success('商品审核通过')
    loadDashboardData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const rejectProduct = async (productId) => {
  try {
    await rejectProductAPI(productId)
    ElMessage.success('商品已拒绝')
    loadDashboardData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const getOrderStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PAID: 'info',
    SHIPPED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getOrderStatusText = (status) => {
  const textMap = {
    PENDING: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return textMap[status] || '未知状态'
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  max-width: none;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.product-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
  color: #666;
}

.dashboard-card {
  height: 400px;
}

.card-content {
  height: 320px;
  position: relative;
}

.empty-state {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  text-align: center;
}
</style>