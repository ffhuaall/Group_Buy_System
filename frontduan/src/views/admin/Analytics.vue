<template>
  <div class="analytics">
    <el-card class="page-header">
      <div class="header-content">
        <h2>数据分析</h2>
        <p>查看系统运营数据和业务分析报告</p>
      </div>
    </el-card>

    <!-- 时间筛选 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedPeriod" @change="handlePeriodChange">
            <el-option label="今日" value="today" />
            <el-option label="昨日" value="yesterday" />
            <el-option label="近7天" value="week" />
            <el-option label="近30天" value="month" />
            <el-option label="近90天" value="quarter" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </el-col>
        <el-col :span="4">
          
        </el-col>
      </el-row>
    </el-card>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon revenue">
              <el-icon><Money /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">¥{{ formatNumber(metrics.totalRevenue) }}</div>
              <div class="metric-label">总收入</div>
              <div class="metric-change" :class="getChangeClass(metrics.revenueChange)">
                <el-icon v-if="metrics.revenueChange > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="metrics.revenueChange < 0"><ArrowDown /></el-icon>
                {{ Math.abs(metrics.revenueChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon orders">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ formatNumber(metrics.totalOrders) }}</div>
              <div class="metric-label">总订单</div>
              <div class="metric-change" :class="getChangeClass(metrics.ordersChange)">
                <el-icon v-if="metrics.ordersChange > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="metrics.ordersChange < 0"><ArrowDown /></el-icon>
                {{ Math.abs(metrics.ordersChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon users">
              <el-icon><User /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ formatNumber(metrics.totalUsers) }}</div>
              <div class="metric-label">总用户</div>
              <div class="metric-change" :class="getChangeClass(metrics.usersChange)">
                <el-icon v-if="metrics.usersChange > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="metrics.usersChange < 0"><ArrowDown /></el-icon>
                {{ Math.abs(metrics.usersChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon products">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">{{ formatNumber(metrics.totalProducts) }}</div>
              <div class="metric-label">总商品</div>
              <div class="metric-change" :class="getChangeClass(metrics.productsChange)">
                <el-icon v-if="metrics.productsChange > 0"><ArrowUp /></el-icon>
                <el-icon v-else-if="metrics.productsChange < 0"><ArrowDown /></el-icon>
                {{ Math.abs(metrics.productsChange) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 订单状态分布 -->
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div class="chart-container">
            <div class="order-stats">
              <div class="stat-item">
                <div class="stat-circle pending"></div>
                <span class="stat-label">待付款</span>
                <span class="stat-value">{{ orderStats.pending }}</span>
              </div>
              <div class="stat-item">
                <div class="stat-circle paid"></div>
                <span class="stat-label">已付款</span>
                <span class="stat-value">{{ orderStats.paid }}</span>
              </div>
              <div class="stat-item">
                <div class="stat-circle shipped"></div>
                <span class="stat-label">已发货</span>
                <span class="stat-value">{{ orderStats.shipped }}</span>
              </div>
              <div class="stat-item">
                <div class="stat-circle completed"></div>
                <span class="stat-label">已完成</span>
                <span class="stat-value">{{ orderStats.completed }}</span>
              </div>
              <div class="stat-item">
                <div class="stat-circle cancelled"></div>
                <span class="stat-label">已取消</span>
                <span class="stat-value">{{ orderStats.cancelled }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 热销商品排行 -->
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>热销商品排行</span>
          </template>
          <div class="chart-container">
            <div class="product-ranking">
              <div
                v-for="(product, index) in topProducts"
                :key="product.id"
                class="ranking-item"
              >
                <div class="ranking-number" :class="getRankingClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="product-info">
                  <div class="product-name">{{ product.name }}</div>
                  <div class="product-sales">销量: {{ product.sales }}</div>
                </div>
                <div class="product-revenue">¥{{ product.revenue.toFixed(2) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 团长业绩排行 -->
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>团长业绩排行</span>
          </template>
          <div class="chart-container">
            <div class="leader-ranking">
              <div
                v-for="(leader, index) in topLeaders"
                :key="leader.id"
                class="ranking-item"
              >
                <div class="ranking-number" :class="getRankingClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="leader-info">
                  <div class="leader-name">{{ leader.name }}</div>
                  <div class="leader-community">{{ leader.community }}</div>
                </div>
                <div class="leader-performance">
                  <div class="performance-orders">{{ leader.orders }}单</div>
                  <div class="performance-revenue">¥{{ leader.revenue.toFixed(2) }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 地区分布 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="24">
        <el-card class="chart-card region-card">
          <template #header>
            <div class="chart-header">
              <span>地区分布</span>
              <span class="region-total">总订单数: {{ regionStats.reduce((sum, region) => sum + region.orders, 0) }}</span>
            </div>
          </template>
          <div class="chart-container region-container">
            <div class="region-stats-grid">
              <div
                v-for="region in regionStats"
                :key="region.name"
                class="region-card-item"
              >
                <div class="region-header">
                  <span class="region-name">{{ region.name }}</span>
                  <span class="region-percentage">{{ ((region.orders / regionStats.reduce((sum, r) => sum + r.orders, 1)) * 100).toFixed(1) }}%</span>
                </div>
                <div class="region-bar">
                  <div
                    class="region-progress"
                    :style="{ width: (region.orders / maxRegionOrders * 100) + '%' }"
                  ></div>
                </div>
                <div class="region-details">
                  <span class="region-orders">{{ region.orders }} 订单</span>
                  <span class="region-revenue">¥{{ region.revenue ? region.revenue.toFixed(2) : '0.00' }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>


  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  Money,
  ShoppingCart,
  User,
  Goods,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import {
  getAnalyticsData,
  getOrderStats,
  getTopProducts,
  getRegionStats,
  getTopLeaders
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const dateRange = ref([])
const selectedPeriod = ref('week')


// 用于清理的变量
let loadingTimer = null
let dataRefreshTimer = null

// 核心指标
const metrics = reactive({
  totalRevenue: 0,
  revenueChange: 0,
  totalOrders: 0,
  ordersChange: 0,
  totalUsers: 0,
  usersChange: 0,
  totalProducts: 0,
  productsChange: 0
})

// 订单状态统计
const orderStats = reactive({
  pending: 0,
  paid: 0,
  shipped: 0,
  completed: 0,
  cancelled: 0
})

// 热销商品
const topProducts = ref([])

// 地区统计
const regionStats = ref([])

// 团长排行
const topLeaders = ref([])



// 计算属性
const maxRegionOrders = computed(() => {
  return Math.max(...regionStats.value.map(r => r.orders), 1)
})

// 获取分析数据
const loadAnalyticsData = async () => {
  // 清除之前的定时器
  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }
  
  loading.value = true
  try {
    const params = {
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      period: selectedPeriod.value
    }
    
    // 获取核心指标
    const metricsResponse = await getAnalyticsData(params)
    if (metricsResponse && metricsResponse.data) {
      Object.assign(metrics, metricsResponse.data)
    }
    
    // 获取订单状态统计
    const orderStatsResponse = await getOrderStats(params)
    if (orderStatsResponse && orderStatsResponse.data) {
      Object.assign(orderStats, orderStatsResponse.data)
    }
    
    // 获取热销商品
    const productsResponse = await getTopProducts(params)
    if (productsResponse && productsResponse.data) {
      topProducts.value = productsResponse.data
    }
    
    // 获取地区统计
    const regionResponse = await getRegionStats(params)
    if (regionResponse && regionResponse.data) {
      regionStats.value = regionResponse.data
    }
    
    // 获取团长排行
    const leadersResponse = await getTopLeaders(params)
    if (leadersResponse && leadersResponse.data) {
      topLeaders.value = leadersResponse.data
    }
    

    
  } catch (error) {
    // 只在组件未卸载时显示错误消息
    if (loading.value !== null) {
      ElMessage.error('获取分析数据失败')
      console.error('Analytics data error:', error)
    }
  } finally {
    // 延迟设置loading状态，避免组件卸载时的错误
    loadingTimer = setTimeout(() => {
      if (loading.value !== null) {
        loading.value = false
      }
      loadingTimer = null
    }, 100)
  }
}



// 处理日期变化
const handleDateChange = () => {
  selectedPeriod.value = 'custom'
  loadAnalyticsData()
}

// 处理时间段变化
const handlePeriodChange = () => {
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  
  switch (selectedPeriod.value) {
    case 'today':
      dateRange.value = [formatDate(today), formatDate(today)]
      break
    case 'yesterday':
      const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)
      dateRange.value = [formatDate(yesterday), formatDate(yesterday)]
      break
    case 'week':
      const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
      dateRange.value = [formatDate(weekAgo), formatDate(today)]
      break
    case 'month':
      const monthAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
      dateRange.value = [formatDate(monthAgo), formatDate(today)]
      break
    case 'quarter':
      const quarterAgo = new Date(today.getTime() - 90 * 24 * 60 * 60 * 1000)
      dateRange.value = [formatDate(quarterAgo), formatDate(today)]
      break
  }
  
  if (selectedPeriod.value !== 'custom') {
    loadAnalyticsData()
  }
}

// 刷新数据
const refreshData = () => {
  // 清除之前的刷新定时器
  if (dataRefreshTimer) {
    clearTimeout(dataRefreshTimer)
    dataRefreshTimer = null
  }
  
  // 延迟执行刷新，避免频繁调用
  dataRefreshTimer = setTimeout(() => {
    if (loading.value !== null) {
      loadAnalyticsData()
    }
    dataRefreshTimer = null
  }, 300)
}



// 工具函数
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}

const formatDate = (date) => {
  return date.toISOString().split('T')[0]
}

const getChangeClass = (change) => {
  if (change > 0) return 'positive'
  if (change < 0) return 'negative'
  return 'neutral'
}

const getRankingClass = (index) => {
  if (index === 0) return 'first'
  if (index === 1) return 'second'
  if (index === 2) return 'third'
  return 'other'
}



// 初始化
onMounted(() => {
  handlePeriodChange() // 设置默认时间范围
})

// 组件卸载前清理资源
onBeforeUnmount(() => {
  // 清理所有定时器
  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }
  
  if (dataRefreshTimer) {
    clearTimeout(dataRefreshTimer)
    dataRefreshTimer = null
  }
  
  // 将响应式数据设置为null，标记组件已卸载
  loading.value = null
  
  // 清空数组和对象引用
  topProducts.value = []
  regionStats.value = []
  topLeaders.value = []
})
</script>

<style scoped>
.analytics {
  width: 100%;
  max-width: none;
}

.page-header {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.page-header :deep(.el-card__body) {
  padding: 30px;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.header-content p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.metrics-row {
  margin-bottom: 20px;
}

.metric-card {
  border-radius: 8px;
  overflow: hidden;
}

.metric-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.metric-icon {
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

.metric-icon.revenue {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.metric-icon.orders {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.metric-icon.users {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.metric-icon.products {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.metric-info {
  flex: 1;
}

.metric-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.metric-label {
  font-size: 14px;
  color: #909399;
  margin: 5px 0;
}

.metric-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.metric-change.positive {
  color: #67c23a;
}

.metric-change.negative {
  color: #f56c6c;
}

.metric-change.neutral {
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}

.chart-note {
  margin-top: 10px;
  font-size: 12px;
  color: #c0c4cc;
}

.order-stats {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  padding: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-circle {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.stat-circle.pending {
  background-color: #e6a23c;
}

.stat-circle.paid {
  background-color: #409eff;
}

.stat-circle.shipped {
  background-color: #909399;
}

.stat-circle.completed {
  background-color: #67c23a;
}

.stat-circle.cancelled {
  background-color: #f56c6c;
}

.stat-label {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-weight: 600;
  color: #303133;
}

.product-ranking,
.leader-ranking {
  padding: 20px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: white;
  margin-right: 15px;
}

.ranking-number.first {
  background-color: #ffd700;
}

.ranking-number.second {
  background-color: #c0c0c0;
}

.ranking-number.third {
  background-color: #cd7f32;
}

.ranking-number.other {
  background-color: #909399;
}

.product-info,
.leader-info {
  flex: 1;
  margin-right: 15px;
}

.product-name,
.leader-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.product-sales,
.leader-community {
  font-size: 12px;
  color: #909399;
}

.product-revenue {
  font-weight: 600;
  color: #67c23a;
}

.leader-performance {
  text-align: right;
}

.performance-orders {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.performance-revenue {
  font-weight: 600;
  color: #67c23a;
}

.region-card {
  height: auto;
}

.region-container {
  height: auto;
  padding: 20px;
}

.region-total {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.region-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  width: 100%;
}

.region-card-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.region-card-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.region-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.region-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.region-percentage {
  font-size: 14px;
  color: #409eff;
  font-weight: 600;
}

.region-bar {
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
}

.region-progress {
  height: 100%;
  background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
  transition: width 0.3s ease;
  border-radius: 4px;
}

.region-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.region-orders {
  font-size: 14px;
  color: #606266;
}

.region-revenue {
  font-size: 14px;
  font-weight: 600;
  color: #67c23a;
}



/* 按钮样式 */
.el-button + .el-button {
  margin-left: 8px;
}

/* 卡片样式 */
.el-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}
</style>