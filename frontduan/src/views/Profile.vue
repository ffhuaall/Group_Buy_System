<template>
  <div class="profile-container">
    <el-row :gutter="30">
      <el-col :xl="6" :lg="8" :md="24">
        <el-card class="profile-sidebar">
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <p class="user-role">{{ userInfo.role === 'ADMIN' ? '管理员' : userInfo.role === 'SUPPLIER' ? '供应商' : '普通用户' }}</p>
          </div>
          
          <el-menu :default-active="activeTab" @select="handleTabChange" class="profile-menu">
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="address">
              <el-icon><Location /></el-icon>
              <span>收货地址</span>
            </el-menu-item>
            <el-menu-item index="orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :xl="18" :lg="16" :md="24">
        <el-card class="profile-content">
          <!-- 个人信息 -->
          <div v-if="activeTab === 'profile'">
            <div class="section-title">
              <h3>个人信息</h3>
            </div>
            <el-form
              ref="profileFormRef"
              :model="userInfo"
              :rules="rules"
              label-width="120px"
              size="large"
            >
              <el-row :gutter="30">
                <el-col :span="12">
                  <el-form-item label="用户名">
                    <el-input v-model="userInfo.username" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="真实姓名" prop="realName">
                    <el-input v-model="userInfo.realName" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="30">
                <el-col :span="12">
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="userInfo.email" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="手机号">
                    <el-input v-model="userInfo.phone" disabled />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="30">
                <el-col :span="12">
                  <el-form-item label="性别">
                    <el-radio-group v-model="userInfo.gender">
                      <el-radio :label="1">男</el-radio>
                      <el-radio :label="0">女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="生日">
                    <el-date-picker
                      v-model="userInfo.birthday"
                      type="date"
                      placeholder="选择日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item>
                <el-button type="primary" size="large" @click="handleUpdateProfile" :loading="loading">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 收货地址 -->
          <div v-else-if="activeTab === 'address'">
          <div class="address-header">
            <h3>收货地址管理</h3>
            <div class="address-header-actions">
              <el-button type="primary" size="large" @click="$router.push('/address')">
                <el-icon><Setting /></el-icon>
                地址管理
              </el-button>
              <el-button type="primary" size="large" @click="showAddressDialog = true">
                <el-icon><Plus /></el-icon>
                添加新地址
              </el-button>
            </div>
          </div>
            
            <div class="address-list">
              <el-row :gutter="20">
                <el-col :xl="12" :lg="24" v-for="address in addresses" :key="address.id">
                  <el-card class="address-item">
                    <div class="address-info">
                      <div class="address-main">
                        <span class="receiver">{{ address.receiverName }}</span>
                        <span class="phone">{{ address.receiverPhone }}</span>
                        <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
                      </div>
                      <div class="address-detail">{{ address.address }}</div>
                      <div class="address-actions">
                        <el-button size="small" @click="editAddress(address)">编辑</el-button>
                        <el-button size="small" type="danger" @click="deleteAddress(address.id)">删除</el-button>
                        <el-button v-if="!address.isDefault" size="small" type="primary" @click="setDefaultAddress(address.id)">设为默认</el-button>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </div>
          
          <!-- 我的订单 -->
          <div v-else-if="activeTab === 'orders'">
            <div class="section-title">
              <h3>我的订单</h3>
            </div>
            
            <div v-if="orders.length > 0">
              <el-table :data="orders" style="width: 100%">
                <el-table-column prop="id" label="订单号" width="120" />
                <el-table-column prop="totalAmount" label="订单金额" width="120">
                  <template #default="{ row }">
                    ¥{{ row.totalAmount }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="订单状态" width="120">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="创建时间" width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <el-button size="small" @click="viewOrderDetail(row.id)">查看详情</el-button>
                    <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="payOrder(row.id)">支付</el-button>
                    <el-button v-if="row.status === 'PENDING'" size="small" type="danger" @click="cancelOrder(row.id)">取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <div class="pagination-container">
                <el-pagination
                  v-model:current-page="orderPagination.current"
                  v-model:page-size="orderPagination.size"
                  :total="orderPagination.total"
                  layout="prev, pager, next"
                  @current-change="loadOrders"
                />
              </div>
            </div>
            
            <el-empty v-else description="暂无订单" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { getOrders } from '@/api/order'
import { getProfile } from '@/api/user'
import { getUserAddresses } from '@/api/address'
import { ElMessage } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'

const userStore = useUserStore()
const activeTab = ref('profile')
const loading = ref(false)
const orders = ref([])
const addresses = ref([])

const userInfo = reactive({
  username: userStore.user?.username || '',
  realName: userStore.user?.realName || '',
  email: userStore.user?.email || '',
  phone: userStore.user?.phone || '',
  gender: userStore.user?.gender || 1,
  birthday: userStore.user?.birthday || '',
  avatar: userStore.user?.avatar || '',
  role: userStore.user?.role || 'USER'
})

const orderPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const handleTabChange = (tab) => {
  activeTab.value = tab
  if (tab === 'orders') {
    loadOrders()
  } else if (tab === 'address') {
    loadAddresses()
  }
}

const loadOrders = async () => {
  if (!userStore.user?.id) {
    ElMessage.error('用户信息不完整')
    return
  }
  
  try {
    loading.value = true
    const result = await getOrders(userStore.user.id, {
      current: orderPagination.current,
      size: orderPagination.size
    })
    
    orders.value = result.records || []
    orderPagination.total = result.total || 0
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'PAID': 'success',
    'SHIPPED': 'info',
    'DELIVERED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const viewOrderDetail = (orderId) => {
  // 跳转到订单详情页面
  console.log('查看订单详情:', orderId)
}

const payOrder = async (orderId) => {
  // 支付订单逻辑
  console.log('支付订单:', orderId)
}



const cancelOrder = async (orderId) => {
  // 取消订单逻辑
  console.log('取消订单:', orderId)
}



const viewOrder = (order) => {
  // 查看订单详情
}

const loadUserInfo = async () => {
  try {
    const data = await getProfile()
    Object.assign(userInfo, data)
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const loadAddresses = async () => {
  if (!userStore.user?.id) {
    ElMessage.error('用户信息不完整')
    return
  }
  
  try {
    const data = await getUserAddresses()
    addresses.value = data
  } catch (error) {
    console.error('加载地址失败:', error)
    ElMessage.error('加载地址失败')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 30px 0;
  max-width: 1400px;
  margin: 0 auto;
}

.profile-sidebar {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  height: fit-content;
}

.avatar-section {
  text-align: center;
  padding: 30px 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.avatar-section h3 {
  margin: 20px 0 10px;
  font-size: 1.2rem;
  color: #303133;
}

.user-role {
  color: #909399;
  font-size: 14px;
}

.profile-menu {
  border: none;
}

.profile-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin-bottom: 5px;
  border-radius: 8px;
}

.profile-content {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  min-height: 600px;
}

.section-title {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f2f5;
}

.section-title h3 {
  font-size: 1.5rem;
  color: #303133;
  margin: 0;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.address-header-actions {
  display: flex;
  gap: 10px;
}

.address-header h3 {
  font-size: 1.5rem;
  color: #303133;
  margin: 0;
}

.address-list {
  margin-top: 20px;
}

.address-item {
  margin-bottom: 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.address-item:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.address-info {
  padding: 10px;
}

.address-main {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.receiver {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.phone {
  color: #606266;
  font-size: 14px;
}

.address-detail {
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 20px 10px;
  }
  
  .address-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .address-actions {
    flex-wrap: wrap;
  }
}
</style>