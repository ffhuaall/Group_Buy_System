<template>
  <div class="checkout-container">
    <div class="page-header">
      <h2>确认订单</h2>
    </div>

    <div class="checkout-content">
      <!-- 收货地址 -->
      <el-card class="address-section" shadow="never">
        <template #header>
          <div class="section-header">
            <h3>收货地址</h3>
            <el-button type="primary" link @click="showAddressDialog = true">
              <el-icon><Plus /></el-icon>
              添加新地址
            </el-button>
          </div>
        </template>
        
        <div v-if="addresses.length === 0" class="no-address">
          <el-empty description="暂无收货地址">
            <el-button type="primary" @click="showAddressDialog = true">添加收货地址</el-button>
          </el-empty>
        </div>
        
        <div v-else class="address-list">
          <div 
            v-for="address in addresses" 
            :key="address.id" 
            class="address-item"
            :class="{ active: selectedAddress?.id === address.id }"
            @click="selectAddress(address)"
          >
            <div class="address-info">
        <div class="recipient">{{ address.receiverName }} {{ address.receiverPhone }}</div>
        <div class="address-detail">{{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}</div>
      </div>
            <div class="address-actions">
              <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
              <el-radio :value="address.id" :model-value="selectedAddress?.id" />
            </div>
          </div>
        </div>
      </el-card>

      <!-- 商品信息 -->
      <el-card class="products-section" shadow="never">
        <template #header>
          <h3>商品信息</h3>
        </template>
        
        <div class="product-list">
          <div v-for="item in cartItems" :key="item.id" class="product-item">
            <div class="product-image-placeholder" :style="{ backgroundColor: getProductColor(item.product?.id || item.productId) }">
              <span>{{ (item.product?.name || '商品').charAt(0) }}</span>
            </div>
            <div class="product-details">
              <div class="product-name">{{ item.product?.name || '商品名称' }}</div>
              <div class="product-spec">数量：{{ item.quantity }}</div>
            </div>
            <div class="product-price">¥{{ ((item.product?.groupPrice || item.product?.price || 0) * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
      </el-card>

      <!-- 支付方式 -->
      <el-card class="payment-section" shadow="never">
        <template #header>
          <h3>支付方式</h3>
        </template>
        
        <el-radio-group v-model="paymentMethod" class="payment-methods">
          <el-radio value="alipay" class="payment-option">
            <div class="payment-info">
              <el-icon size="24" color="#1677ff"><CreditCard /></el-icon>
              <span>支付宝</span>
            </div>
          </el-radio>
          <el-radio value="wechat" class="payment-option">
            <div class="payment-info">
              <el-icon size="24" color="#07c160"><Wallet /></el-icon>
              <span>微信支付</span>
            </div>
          </el-radio>
          <el-radio value="bank" class="payment-option">
            <div class="payment-info">
              <el-icon size="24" color="#f56565"><Money /></el-icon>
              <span>银行卡</span>
            </div>
          </el-radio>
        </el-radio-group>
      </el-card>

      <!-- 订单摘要 -->
      <el-card class="summary-section" shadow="never">
        <template #header>
          <h3>订单摘要</h3>
        </template>
        
        <div class="summary-details">
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>¥{{ shippingFee.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span>应付总额：</span>
            <span class="total-amount">¥{{ (totalPrice + shippingFee).toFixed(2) }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 底部操作栏 -->
    <div class="checkout-footer">
      <div class="footer-info">
        <span class="total-text">总计：<span class="total-price">¥{{ (totalPrice + shippingFee).toFixed(2) }}</span></span>
      </div>
      <div class="footer-actions">
        <el-button size="large" @click="$router.go(-1)">返回购物车</el-button>
        <el-button type="primary" size="large" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </div>
    </div>

    <!-- 添加地址对话框 -->
    <el-dialog v-model="showAddressDialog" title="添加收货地址" width="500px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
        <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
      </el-form-item>
      <el-form-item label="手机号" prop="receiverPhone">
        <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CreditCard, Wallet, Money } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { getUserAddresses, addAddress, getDefaultAddress } from '@/api/address'
import { createOrder } from '@/api/order'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 响应式数据
const addresses = ref([])
const selectedAddress = ref(null)
const paymentMethod = ref('alipay')
const shippingFee = ref(5.00)
const submitting = ref(false)
const showAddressDialog = ref(false)

// 地址表单数据
const addressForm = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const addressFormRef = ref()

// 表单验证规则
const addressRules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '收货人姓名长度在2到50个字符', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' },
    { max: 20, message: '省份名称不能超过20个字符', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' },
    { max: 20, message: '城市名称不能超过20个字符', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区县', trigger: 'blur' },
    { max: 20, message: '区县名称不能超过20个字符', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 200, message: '详细地址长度在5到200个字符', trigger: 'blur' }
  ]
}

// 计算属性
const cartItems = computed(() => cartStore.items || [])
const totalPrice = computed(() => {
  return cartItems.value.reduce((total, item) => {
    const price = item.product?.groupPrice || item.product?.price || 0
    return total + (price * item.quantity)
  }, 0)
})

// 方法
const getProductColor = (productId) => {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff']
  return colors[(productId || 0) % colors.length]
}

const selectAddress = (address) => {
  selectedAddress.value = address
}

const loadAddresses = async () => {
  try {
    const data = await getUserAddresses()
    addresses.value = data
    
    // 优先选择默认地址
    const defaultAddress = data.find(addr => addr.isDefault)
    if (defaultAddress) {
      selectedAddress.value = defaultAddress
    } else if (data.length > 0) {
      selectedAddress.value = data[0]
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error(error.message || '获取地址列表失败')
  }
}

// 快速选择默认地址
const selectDefaultAddress = async () => {
  try {
    const defaultAddr = await getDefaultAddress()
    if (defaultAddr) {
      selectedAddress.value = defaultAddr
      ElMessage.success('已选择默认地址')
    } else {
      ElMessage.info('您还没有设置默认地址')
    }
  } catch (error) {
    console.error('获取默认地址失败:', error)
  }
}

const saveAddress = async () => {
  try {
    await addressFormRef.value.validate()
    await addAddress(addressForm.value)
    ElMessage.success('地址添加成功')
    showAddressDialog.value = false
    // 重置表单
    addressForm.value = {
      receiverName: '',
      receiverPhone: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
      isDefault: false
    }
    // 重新加载地址列表
    await loadAddresses()
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error('保存地址失败')
  }
}

const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  try {
    submitting.value = true
    
    // 检查用户登录状态
    if (!userStore.isLoggedIn || !userStore.token) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    
    console.log('用户登录状态:', userStore.isLoggedIn)
    console.log('Token存在:', !!userStore.token)
    console.log('用户信息:', userStore.user)
    
    const orderData = {
      receiverName: selectedAddress.value.receiverName,
    receiverPhone: selectedAddress.value.receiverPhone,
    receiverAddress: `${selectedAddress.value.province} ${selectedAddress.value.city} ${selectedAddress.value.district} ${selectedAddress.value.detailAddress}`,
      paymentMethod: paymentMethod.value,
      items: cartItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        price: item.product?.groupPrice || item.product?.price || 0
      })),
      totalAmount: totalPrice.value + shippingFee.value,
      shippingFee: shippingFee.value
    }
    
    console.log('订单数据:', orderData)
    
    const result = await createOrder(orderData)
    
    ElMessage.success('订单创建成功')
    
    // 清空购物车
    await cartStore.clearCart()
    
    // 跳转到支付页面或订单详情页面
    router.push(`/payment/${result.id}`)
    
  } catch (error) {
    console.error('提交订单失败:', error)
    console.error('错误详情:', error.response?.data)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('提交订单失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

// 生命周期
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  // 加载购物车数据
  await cartStore.loadCart()
  
  // 检查购物车是否为空
  if (!cartItems.value || cartItems.value.length === 0) {
    ElMessage.warning('购物车为空，请先添加商品')
    router.push('/cart')
    return
  }
  
  // 加载收货地址
  await loadAddresses()
})
</script>

<style scoped>
.checkout-container {
  max-width: 1200px;
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

.checkout-content {
  margin-bottom: 80px;
}

.address-section,
.products-section,
.payment-section,
.summary-section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  margin: 0;
  color: #333;
}

.no-address {
  text-align: center;
  padding: 40px 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
}

.address-item.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.address-info {
  flex: 1;
}

.recipient {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.address-detail {
  color: #666;
  font-size: 14px;
}

.address-actions {
  display: flex;
  align-items: center;
  gap: 10px;
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
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px;
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
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin: 0;
}

.payment-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
}

.summary-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.summary-row.total {
  border-top: 1px solid #e4e7ed;
  padding-top: 15px;
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
}

.total-amount {
  color: #e6a23c;
}

.checkout-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #e4e7ed;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1000;
}

.footer-info {
  flex: 1;
}

.total-text {
  font-size: 16px;
  color: #333;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #e6a23c;
}

.footer-actions {
  display: flex;
  gap: 15px;
}

@media (max-width: 768px) {
  .checkout-container {
    padding: 10px;
  }
  
  .address-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .address-actions {
    align-self: flex-end;
  }
  
  .product-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .checkout-footer {
    flex-direction: column;
    gap: 10px;
    padding: 10px;
  }
  
  .footer-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>