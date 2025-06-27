<template>
  <div class="order-management">
    <el-card class="page-header">
      <div class="header-content">
        <h2>订单管理</h2>
        <p>管理系统中的所有订单信息</p>
      </div>
    </el-card>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="5">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索订单号或用户名"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="订单状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待付款" value="PENDING" />
            <el-option label="已付款" value="PAID" />
            <el-option label="已发货" value="SHIPPED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.paymentMethod" placeholder="支付方式" clearable>
            <el-option label="全部" value="" />
            <el-option label="微信支付" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="银行卡" value="BANK_CARD" />
          </el-select>
        </el-col>
        <el-col :span="7">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </el-card>



    <!-- 订单列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <div class="header-actions">
            
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="orderList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <div class="username">{{ row.username }}</div>
              <div class="phone">{{ row.userPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商品信息" width="200">
          <template #default="{ row }">
            <div class="product-info">
              <div class="product-name">{{ row.productName }}</div>
              <div class="product-spec">数量: {{ row.quantity }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            <div class="amount">
              <div class="total">¥{{ row.totalAmount }}</div>
              <div class="unit">单价: ¥{{ row.unitPrice }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentTagType(row.paymentMethod)">{{ getPaymentText(row.paymentMethod) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column prop="payTime" label="支付时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewOrder(row)"
            >
              查看
            </el-button>
            <el-dropdown @command="(command) => handleOrderAction(command, row)">
              <el-button type="info" size="small">
                操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="row.status === 'PAID'" command="ship">发货</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 'SHIPPED'" command="complete">完成</el-dropdown-item>
                  <el-dropdown-item v-if="['PENDING', 'PAID'].includes(row.status)" command="cancel">取消</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 'PAID'" command="refund">退款</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailVisible"
      title="订单详情"
      width="800px"
      :before-close="closeOrderDetail"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentOrder.username }}</el-descriptions-item>
          <el-descriptions-item label="用户手机">{{ currentOrder.userPhone }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ currentOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="商品数量">{{ currentOrder.quantity }}</el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ currentOrder.unitPrice }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">
            <el-tag :type="getPaymentTagType(currentOrder.paymentMethod)">{{ getPaymentText(currentOrder.paymentMethod) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '未支付' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ currentOrder.shipTime || '未发货' }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ currentOrder.shippingAddress || '暂无地址' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ currentOrder.remark || '无备注' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="closeOrderDetail">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'
import {
  getOrderList,
  updateOrderStatus
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const orderList = ref([])
const selectedOrders = ref([])
const orderDetailVisible = ref(false)
const currentOrder = ref(null)



// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  paymentMethod: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 获取订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status,
      paymentMethod: searchForm.paymentMethod,
      startDate: searchForm.dateRange?.[0],
      endDate: searchForm.dateRange?.[1]
    }
    
    const response = await getOrderList(params)
    orderList.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}



// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadOrderList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    paymentMethod: '',
    dateRange: []
  })
  pagination.page = 1
  loadOrderList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadOrderList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadOrderList()
}

// 选择处理
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 查看订单详情
const viewOrder = (order) => {
  currentOrder.value = order
  orderDetailVisible.value = true
}

// 关闭订单详情
const closeOrderDetail = () => {
  orderDetailVisible.value = false
  currentOrder.value = null
}

// 处理订单操作
const handleOrderAction = async (command, order) => {
  const actions = {
    ship: () => shipOrder(order),
    complete: () => completeOrder(order),
    cancel: () => cancelOrder(order),
    refund: () => refundOrder(order)
  }
  
  if (actions[command]) {
    await actions[command]()
  }
}

// 发货
const shipOrder = async (order) => {
  try {
    await ElMessageBox.confirm(`确定要发货订单 "${order.orderNumber}" 吗？`, '确认发货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await updateOrderStatus(order.id, 'SHIPPED')
    ElMessage.success('发货成功')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

// 完成订单
const completeOrder = async (order) => {
  try {
    await ElMessageBox.confirm(`确定要完成订单 "${order.orderNumber}" 吗？`, '确认完成', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await updateOrderStatus(order.id, 'COMPLETED')
    ElMessage.success('订单已完成')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `请输入取消订单 "${order.orderNumber}" 的原因：`,
      '取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入取消原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入取消原因'
          }
          return true
        }
      }
    )
    
    await updateOrderStatus(order.id, 'CANCELLED', reason)
    ElMessage.success('订单已取消')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 退款
const refundOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要退款订单 "${order.orderNumber}" 吗？退款金额：¥${order.totalAmount}`,
      '确认退款',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateOrderStatus(order.id, 'REFUNDED')
    ElMessage.success('退款成功')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退款失败')
    }
  }
}



// 工具函数
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待付款',
    'PAID': '已付款',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'PAID': 'success',
    'SHIPPED': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger',
    'REFUNDED': 'info'
  }
  return typeMap[status] || ''
}

const getPaymentText = (method) => {
  const methodMap = {
    'WECHAT': '微信支付',
    'ALIPAY': '支付宝',
    'BANK_CARD': '银行卡'
  }
  return methodMap[method] || method
}

const getPaymentTagType = (method) => {
  const typeMap = {
    'WECHAT': 'success',
    'ALIPAY': 'primary',
    'BANK_CARD': 'info'
  }
  return typeMap[method] || ''
}

// 初始化
onMounted(() => {
  loadOrderList()
})
</script>

<style scoped>
.order-management {
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

.search-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 20px;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.paid {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.shipped {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-icon.cancelled {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.table-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.order-detail {
  padding: 20px 0;
}

.user-info {
  line-height: 1.4;
}

.username {
  font-weight: 600;
  color: #303133;
}

.phone {
  font-size: 12px;
  color: #909399;
}

.product-info {
  line-height: 1.4;
}

.product-name {
  font-weight: 600;
  color: #303133;
}

.product-spec {
  font-size: 12px;
  color: #909399;
}

.amount {
  line-height: 1.4;
}

.total {
  font-weight: 600;
  color: #f56c6c;
  font-size: 16px;
}

.unit {
  font-size: 12px;
  color: #909399;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background-color: #f8f9fa;
}

:deep(.el-table th) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 按钮样式 */
.el-button + .el-button {
  margin-left: 8px;
}

/* 标签样式 */
.el-tag {
  border-radius: 12px;
  padding: 0 8px;
  font-size: 12px;
}
</style>