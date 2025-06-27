<template>
  <div class="supplier-orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="header-actions">
            <el-button @click="refreshOrders" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="订单状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable @change="handleFilter">
              <el-option label="全部" value="" />
              <el-option label="待付款" value="PENDING_PAYMENT" />
              <el-option label="已付款" value="PAID" />
              <el-option label="已发货" value="SHIPPED" />
              <el-option label="已送达" value="DELIVERED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单号">
            <el-input v-model="filterForm.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleFilter" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilter">搜索</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 订单列表 -->
      <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="商品信息" min-width="200">
          <template #default="scope">
            <div v-for="item in scope.row.orderItems" :key="item.id" class="product-item">
              <span>{{ item.productName }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="userPhone" label="联系电话" width="130" />
        <el-table-column prop="shippingAddress" label="收货地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="下单时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewOrderDetail(scope.row)">
              查看详情
            </el-button>
            <el-button 
              v-if="scope.row.status === 'PAID'" 
              type="success" 
              size="small" 
              @click="shipOrder(scope.row)"
            >
              发货
            </el-button>
            <el-button 
              v-if="scope.row.status === 'SHIPPED'" 
              type="warning" 
              size="small" 
              @click="updateLogistics(scope.row)"
            >
              物流
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
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
    <el-dialog v-model="showDetailDialog" title="订单详情" width="800px">
      <div v-if="selectedOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(selectedOrder.status)">{{ getStatusText(selectedOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ selectedOrder.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ selectedOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="用户姓名">{{ selectedOrder.userName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedOrder.userPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ selectedOrder.shippingAddress }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 20px 0 10px 0;">商品清单</h4>
        <el-table :data="selectedOrder.orderItems" style="width: 100%">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="scope">
              ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog v-model="showShipDialog" title="订单发货" width="500px">
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-select v-model="shipForm.logisticsCompany" placeholder="请选择物流公司">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达速递" value="韵达速递" />
            <el-option label="百世快递" value="百世快递" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="trackingNumber">
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showShipDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmShip" :loading="shipping">确认发货</el-button>
      </template>
    </el-dialog>

    <!-- 更新物流对话框 -->
    <el-dialog v-model="showLogisticsDialog" title="更新物流信息" width="500px">
      <el-form ref="logisticsFormRef" :model="logisticsForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-select v-model="logisticsForm.logisticsCompany" placeholder="请选择物流公司">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达速递" value="韵达速递" />
            <el-option label="百世快递" value="百世快递" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="trackingNumber">
          <el-input v-model="logisticsForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLogisticsDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdateLogistics" :loading="updatingLogistics">更新物流</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCurrentSupplierInfo } from '@/api/supplier'
import request from '@/api/request'

const userStore = useUserStore()
const loading = ref(false)
const shipping = ref(false)
const updatingLogistics = ref(false)
const showDetailDialog = ref(false)
const showShipDialog = ref(false)
const showLogisticsDialog = ref(false)
const shipFormRef = ref()
const logisticsFormRef = ref()

const orders = ref([])
const selectedOrder = ref(null)

const filterForm = reactive({
  status: '',
  orderNo: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const shipForm = reactive({
  orderNo: '',
  logisticsCompany: '',
  trackingNumber: ''
})

const logisticsForm = reactive({
  orderNo: '',
  logisticsCompany: '',
  trackingNumber: ''
})

const shipRules = {
  logisticsCompany: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  trackingNumber: [{ required: true, message: '请输入快递单号', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING_PAYMENT': 'warning',
    'PAID': 'success',
    'SHIPPED': 'primary',
    'DELIVERED': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING_PAYMENT': '待付款',
    'PAID': '已付款',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

const loadOrders = async () => {
  try {
    loading.value = true
    const userId = userStore.user.id
    
    // 先获取供应商信息
    const supplierResponse = await getCurrentSupplierInfo()
    
    if (supplierResponse.code !== 200 || !supplierResponse.data) {
      ElMessage.error('获取供应商信息失败')
      return
    }
    
    const supplierId = supplierResponse.data.id
    
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    
    if (filterForm.status) {
      params.status = filterForm.status
    }
    
    const response = await request({
      url: `/order/supplier/${supplierId}`,
      method: 'get',
      params
    })
    
    if (response.code === 200) {
      orders.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '加载订单失败')
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const refreshOrders = () => {
  loadOrders()
}

const handleFilter = () => {
  pagination.current = 1
  loadOrders()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.orderNo = ''
  pagination.current = 1
  loadOrders()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadOrders()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadOrders()
}

const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailDialog.value = true
}

const shipOrder = (order) => {
  shipForm.orderNo = order.orderNo
  shipForm.logisticsCompany = ''
  shipForm.trackingNumber = ''
  showShipDialog.value = true
}

const confirmShip = async () => {
  try {
    await shipFormRef.value.validate()
    shipping.value = true
    
    const response = await request({
      url: `/order/logistics/${shipForm.orderNo}`,
      method: 'put',
      params: {
        logisticsCompany: shipForm.logisticsCompany,
        trackingNumber: shipForm.trackingNumber
      }
    })
    
    if (response.code === 200) {
      ElMessage.success('发货成功')
      showShipDialog.value = false
      loadOrders()
    } else {
      ElMessage.error(response.message || '发货失败')
    }
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败')
  } finally {
    shipping.value = false
  }
}

const updateLogistics = (order) => {
  logisticsForm.orderNo = order.orderNo
  logisticsForm.logisticsCompany = order.logisticsCompany || ''
  logisticsForm.trackingNumber = order.trackingNumber || ''
  showLogisticsDialog.value = true
}

const confirmUpdateLogistics = async () => {
  try {
    await logisticsFormRef.value.validate()
    updatingLogistics.value = true
    
    const response = await request({
      url: `/order/logistics/${logisticsForm.orderNo}`,
      method: 'put',
      params: {
        logisticsCompany: logisticsForm.logisticsCompany,
        trackingNumber: logisticsForm.trackingNumber
      }
    })
    
    if (response.code === 200) {
      ElMessage.success('物流信息更新成功')
      showLogisticsDialog.value = false
      loadOrders()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新物流信息失败:', error)
    ElMessage.error('更新失败')
  } finally {
    updatingLogistics.value = false
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.supplier-orders {
  padding: 0;
  width: 100%;
  max-width: none;
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

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.product-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.product-item:last-child {
  margin-bottom: 0;
}

.quantity {
  color: #909399;
  font-size: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
</style>