<template>
  <div class="order-container">
    <h2>我的订单</h2>
    
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部订单" name="all" />
      <el-tab-pane label="待付款" name="pending" />
      <el-tab-pane label="待发货" name="paid" />
      <el-tab-pane label="待收货" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
    </el-tabs>
    
    <div class="order-list">
      <el-card v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNo }}</span>
            <span class="order-date">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
        </div>
        
        <div class="order-products">
          <div v-for="item in order.items" :key="item.id" class="product-item">
            <el-image
              :src="item.product.imageUrl"
              :alt="item.product.name"
              style="width: 60px; height: 60px"
              fit="cover"
            />
            <div class="product-details">
              <div class="product-name">{{ item.product.name }}</div>
              <div class="product-spec">数量：{{ item.quantity }}</div>
            </div>
            <div class="product-price">¥{{ item.price }}</div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            总计：¥{{ order.totalAmount }}
          </div>
          <div class="order-actions">
            <el-button v-if="order.status === 'PENDING_PAYMENT'" type="primary" @click="payOrder(order.orderNo)">
            立即支付
          </el-button>
          <el-button v-if="order.status === 'SHIPPED'" type="success" @click="confirmOrder(order.orderNo)">
            确认收货
          </el-button>
            <el-button @click="viewOrderDetail(order.id)">查看详情</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-empty v-if="orders.length === 0" description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrders, payOrder as payOrderAPI, confirmOrder as confirmOrderAPI } from '@/api/order'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)

const loadOrders = async (status = null) => {
  loading.value = true
  try {
    const userStore = useUserStore()
    console.log('加载订单，用户ID:', userStore.user.id, '状态:', status)
    const response = await getOrders(userStore.user.id, { status })
    console.log('订单API响应:', response)
    // 处理分页数据格式
    orders.value = response?.records || []
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败: ' + (error.message || '未知错误'))
    orders.value = []
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab) => {
  const statusMap = {
    all: null,
    pending: 'PENDING_PAYMENT',
    paid: 'PAID',
    shipped: 'SHIPPED',
    completed: 'COMPLETED'
  }
  loadOrders(statusMap[tab])
}

const payOrder = async (orderNo) => {
  try {
    await payOrderAPI(orderNo, { paymentMethod: 'alipay' })
    ElMessage.success('支付成功')
    await loadOrders()
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  }
}

const confirmOrder = async (orderNo) => {
  try {
    await confirmOrderAPI(orderNo)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    ElMessage.error('确认收货失败')
  }
}

const viewOrderDetail = (orderId) => {
  // 跳转到订单详情页
  console.log('查看订单详情:', orderId)
  router.push(`/order-detail/${orderId}`)
}

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

const getStatusText = (status) => {
  const textMap = {
    PENDING: '待付款',
    PENDING_PAYMENT: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    DELIVERED: '已完成',
    CANCELLED: '已取消'
  }
  return textMap[status] || '未知状态'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.order-item {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-number {
  font-weight: bold;
}

.order-date {
  color: #666;
}

.order-products {
  margin-bottom: 15px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.product-item:last-child {
  border-bottom: none;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.product-spec {
  color: #666;
  font-size: 14px;
}

.product-price {
  color: #e74c3c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.order-total {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.order-actions {
  display: flex;
  gap: 10px;
}
</style>