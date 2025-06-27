<template>
  <div class="order-detail-container">
    <div class="page-header">
      <h2>订单详情</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-loading-directive />
    </div>

    <div v-else-if="order" class="order-detail-content">
      <!-- 订单基本信息 -->
      <el-card class="order-info-section" shadow="never">
        <template #header>
          <h3>订单信息</h3>
        </template>
        
        <div class="order-info-grid">
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ order.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="info-row">
            <span class="label">订单状态：</span>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
          <div class="info-row">
            <span class="label">支付方式：</span>
            <span class="value">{{ order.paymentMethod || '未选择' }}</span>
          </div>
          <div class="info-row">
            <span class="label">订单金额：</span>
            <span class="value amount">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </el-card>

      <!-- 收货地址信息 -->
      <el-card class="address-section" shadow="never">
        <template #header>
          <h3>收货信息</h3>
        </template>
        
        <div class="address-info">
          <div class="receiver-info">
            <span class="name">{{ order.receiverName }}</span>
            <span class="phone">{{ order.receiverPhone }}</span>
          </div>
          <div class="address">{{ order.receiverAddress }}</div>
        </div>
      </el-card>

      <!-- 商品信息 -->
      <el-card class="products-section" shadow="never">
        <template #header>
          <h3>商品信息</h3>
        </template>
        
        <div v-if="order.orderItems && order.orderItems.length > 0" class="product-list">
          <div v-for="item in order.orderItems" :key="item.id" class="product-item">
            <div class="product-image">
              <img :src="item.productImage || '/images/default-product.jpg'" :alt="item.productName" />
            </div>
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-spec">数量：{{ item.quantity }}</div>
              <div class="product-price">单价：¥{{ item.price }}</div>
            </div>
            <div class="product-total">
              <span class="total-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <div v-else class="no-products">
          <el-empty description="暂无商品信息" />
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button @click="$router.go(-1)">返回</el-button>
        <el-button v-if="order.status === 'PENDING_PAYMENT'" type="primary" @click="goToPayment">
          去支付
        </el-button>
        <el-button v-if="order.status === 'SHIPPED'" type="success" @click="confirmOrder">
          确认收货
        </el-button>
      </div>
    </div>

    <div v-else class="error-container">
      <el-result icon="error" title="订单不存在" sub-title="请检查订单号是否正确">
        <template #extra>
          <el-button type="primary" @click="$router.push('/orders')">返回订单列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderById, confirmOrder as confirmOrderAPI } from '@/api/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(true)
const order = ref(null)

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PENDING_PAYMENT: 'warning',
    PAID: 'info',
    SHIPPED: 'primary',
    COMPLETED: 'success',
    DELIVERED: 'success',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    PENDING: '待处理',
    PENDING_PAYMENT: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    DELIVERED: '已送达',
    CANCELLED: '已取消'
  }
  return textMap[status] || '未知状态'
}

// 加载订单详情
const loadOrderDetail = async () => {
  try {
    loading.value = true
    const orderId = route.params.orderId
    if (!orderId) {
      ElMessage.error('订单ID不能为空')
      router.push('/orders')
      return
    }
    
    const data = await getOrderById(orderId)
    order.value = data
    
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 去支付
const goToPayment = () => {
  router.push(`/payment/${order.value.id}`)
}

// 确认收货
const confirmOrder = async () => {
  try {
    await confirmOrderAPI(order.value.orderNo)
    ElMessage.success('确认收货成功')
    await loadOrderDetail()
  } catch (error) {
    console.error('确认收货失败:', error)
    ElMessage.error('确认收货失败')
  }
}

// 生命周期
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  await loadOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  color: #333;
  font-size: 24px;
  margin: 0;
}

.loading-container,
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.order-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-info-section,
.address-section,
.products-section {
  background: white;
  border-radius: 8px;
}

.order-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.info-row {
  display: flex;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #666;
  margin-right: 10px;
  min-width: 80px;
}

.value {
  color: #333;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.address-info {
  padding: 10px 0;
}

.receiver-info {
  margin-bottom: 10px;
}

.receiver-info .name {
  font-weight: 500;
  margin-right: 15px;
}

.receiver-info .phone {
  color: #666;
}

.address {
  color: #333;
  line-height: 1.5;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fafafa;
}

.product-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  border-radius: 4px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
  color: #333;
}

.product-spec,
.product-price {
  font-size: 14px;
  color: #666;
  margin-bottom: 3px;
}

.product-total {
  text-align: right;
}

.total-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.no-products {
  text-align: center;
  padding: 40px 0;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

.action-buttons .el-button {
  min-width: 120px;
}

@media (max-width: 768px) {
  .order-detail-container {
    padding: 15px;
  }
  
  .order-info-grid {
    grid-template-columns: 1fr;
  }
  
  .product-item {
    flex-direction: column;
    text-align: center;
  }
  
  .product-image {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>