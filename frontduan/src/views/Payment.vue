<template>
  <div class="payment-container">
    <div class="page-header">
      <h2>订单支付</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-loading text="加载中..." />
    </div>

    <div v-else-if="order" class="payment-content">
      <!-- 订单信息 -->
      <el-card class="order-info-section" shadow="never">
        <template #header>
          <h3>订单信息</h3>
        </template>
        
        <div class="order-details">
          <div class="order-row">
            <span class="label">订单号：</span>
            <span class="value">{{ order.orderNo }}</span>
          </div>
          <div class="order-row">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="order-row">
            <span class="label">收货地址：</span>
            <span class="value">{{ formatAddress(order.address) }}</span>
          </div>
          <div class="order-row total">
            <span class="label">订单金额：</span>
            <span class="amount">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </el-card>

      <!-- 商品列表 -->
      <el-card class="products-section" shadow="never">
        <template #header>
          <h3>商品清单</h3>
        </template>
        
        <div class="product-list">
          <div v-for="item in order.items" :key="item.id" class="product-item">
            <div class="product-image-placeholder" :style="{ backgroundColor: getProductColor(item.product?.id || item.productId) }">
              <span>{{ (item.product?.name || '商品').charAt(0) }}</span>
            </div>
            <div class="product-details">
              <div class="product-name">{{ item.product?.name || '商品名称' }}</div>
              <div class="product-spec">数量：{{ item.quantity }}</div>
            </div>
            <div class="product-price">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
      </el-card>

      <!-- 支付方式 -->
      <el-card class="payment-method-section" shadow="never">
        <template #header>
          <h3>选择支付方式</h3>
        </template>
        
        <el-radio-group v-model="selectedPaymentMethod" class="payment-methods">
          <el-radio value="alipay" class="payment-option">
            <div class="payment-info">
              <el-icon size="32" color="#1677ff"><CreditCard /></el-icon>
              <div class="payment-text">
                <div class="payment-name">支付宝</div>
                <div class="payment-desc">推荐使用支付宝快捷支付</div>
              </div>
            </div>
          </el-radio>
          <el-radio value="wechat" class="payment-option">
            <div class="payment-info">
              <el-icon size="32" color="#07c160"><Wallet /></el-icon>
              <div class="payment-text">
                <div class="payment-name">微信支付</div>
                <div class="payment-desc">微信安全支付</div>
              </div>
            </div>
          </el-radio>
          <el-radio value="bank" class="payment-option">
            <div class="payment-info">
              <el-icon size="32" color="#f56565"><Money /></el-icon>
              <div class="payment-text">
                <div class="payment-name">银行卡支付</div>
                <div class="payment-desc">支持各大银行卡</div>
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </el-card>

      <!-- 支付确认 -->
      <el-card class="payment-confirm-section" shadow="never">
        <div class="confirm-info">
          <div class="amount-info">
            <span class="amount-label">应付金额：</span>
            <span class="amount-value">¥{{ order.totalAmount }}</span>
          </div>
          <div class="payment-actions">
            <el-button size="large" @click="$router.go(-1)">返回</el-button>
            <el-button type="primary" size="large" :loading="paying" @click="handlePayment">
              {{ paying ? '支付中...' : '立即支付' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div v-else class="error-container">
      <el-result icon="error" title="订单不存在" sub-title="请检查订单号是否正确">
        <template #extra>
          <el-button type="primary" @click="$router.push('/orders')">查看我的订单</el-button>
        </template>
      </el-result>
    </div>

    <!-- 支付成功对话框 -->
    <el-dialog v-model="showSuccessDialog" title="支付成功" width="400px" :show-close="false" :close-on-click-modal="false">
      <div class="success-content">
        <el-icon size="64" color="#67c23a"><SuccessFilled /></el-icon>
        <h3>支付成功！</h3>
        <p>您的订单已支付成功，我们将尽快为您发货。</p>
      </div>
      <template #footer>
        <div class="success-actions">
          <el-button @click="goToOrders">查看订单</el-button>
          <el-button type="primary" @click="goToHome">继续购物</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CreditCard, Wallet, Money, SuccessFilled } from '@element-plus/icons-vue'
import { getOrderById, payOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(true)
const paying = ref(false)
const order = ref(null)
const selectedPaymentMethod = ref('alipay')
const showSuccessDialog = ref(false)

// 方法
const getProductColor = (productId) => {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff']
  return colors[(productId || 0) % colors.length]
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const formatAddress = (address) => {
  if (!address) return ''
  return `${address.province} ${address.city} ${address.district} ${address.detailAddress}`
}

const loadOrder = async () => {
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
    
    // 检查订单状态
    if (data.status !== 'PENDING_PAYMENT') {
      ElMessage.warning('该订单已支付或已取消')
      router.push('/orders')
      return
    }
    
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const handlePayment = async () => {
  if (!selectedPaymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认使用${getPaymentMethodName(selectedPaymentMethod.value)}支付 ¥${order.value.totalAmount} 吗？`,
      '确认支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    paying.value = true
    
    const paymentData = {
      paymentMethod: selectedPaymentMethod.value,
      amount: order.value.totalAmount
    }
    
    await payOrder(order.value.orderNo, paymentData)
    
    // 模拟支付处理时间
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    showSuccessDialog.value = true
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败，请重试')
    }
  } finally {
    paying.value = false
  }
}

const getPaymentMethodName = (method) => {
  const names = {
    alipay: '支付宝',
    wechat: '微信支付',
    bank: '银行卡支付'
  }
  return names[method] || method
}

const goToOrders = () => {
  showSuccessDialog.value = false
  router.push('/orders')
}

const goToHome = () => {
  showSuccessDialog.value = false
  router.push('/home')
}

// 生命周期
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  await loadOrder()
})
</script>

<style scoped>
.payment-container {
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

.payment-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-info-section h3,
.products-section h3,
.payment-method-section h3 {
  margin: 0;
  color: #333;
}

.order-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.order-row.total {
  border-top: 1px solid #e4e7ed;
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
}

.label {
  color: #666;
  font-size: 14px;
}

.value {
  color: #333;
  font-weight: 500;
}

.amount {
  color: #e6a23c;
  font-size: 20px;
  font-weight: bold;
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
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.product-image-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
  margin-right: 15px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.product-spec {
  color: #666;
  font-size: 14px;
}

.product-price {
  font-weight: bold;
  color: #e6a23c;
  font-size: 16px;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-option {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  margin: 0;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: #409eff;
}

.payment-option.is-checked {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.payment-info {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 100%;
}

.payment-text {
  flex: 1;
}

.payment-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.payment-desc {
  font-size: 14px;
  color: #666;
}

.payment-confirm-section {
  position: sticky;
  bottom: 20px;
}

.confirm-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.amount-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.amount-label {
  font-size: 16px;
  color: #333;
}

.amount-value {
  font-size: 24px;
  font-weight: bold;
  color: #e6a23c;
}

.payment-actions {
  display: flex;
  gap: 15px;
}

.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-content h3 {
  color: #67c23a;
  margin: 15px 0 10px;
}

.success-content p {
  color: #666;
  margin: 0;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

@media (max-width: 768px) {
  .payment-container {
    padding: 10px;
  }
  
  .confirm-info {
    flex-direction: column;
    gap: 15px;
  }
  
  .payment-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .amount-info {
    justify-content: center;
  }
}
</style>