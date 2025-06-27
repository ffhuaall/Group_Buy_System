<template>
  <div class="supplier-shipping">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发货管理</span>
          <div class="header-actions">
            <el-button @click="refreshShipping" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="发货状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable @change="handleFilter">
              <el-option label="全部" value="" />
              <el-option label="待发货" value="PAID" />
              <el-option label="已发货" value="SHIPPED" />
              <el-option label="已送达" value="DELIVERED" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单号">
            <el-input v-model="filterForm.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleFilter" />
          </el-form-item>
          <el-form-item label="物流公司">
            <el-select v-model="filterForm.logisticsCompany" placeholder="全部物流公司" clearable @change="handleFilter">
              <el-option label="全部" value="" />
              <el-option label="顺丰速运" value="顺丰速运" />
              <el-option label="圆通速递" value="圆通速递" />
              <el-option label="中通快递" value="中通快递" />
              <el-option label="申通快递" value="申通快递" />
              <el-option label="韵达速递" value="韵达速递" />
              <el-option label="百世快递" value="百世快递" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilter">搜索</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 发货列表 -->
      <el-table :data="shippingList" v-loading="loading" style="width: 100%">
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
        <el-table-column prop="logisticsCompany" label="物流公司" width="120" />
        <el-table-column prop="trackingNumber" label="快递单号" width="150" />
        <el-table-column prop="shippedAt" label="发货时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="viewShippingDetail(scope.row)">
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
                更新物流
              </el-button>
              <el-button 
                v-if="scope.row.status === 'SHIPPED'" 
                type="info" 
                size="small" 
                @click="trackOrder(scope.row)"
              >
                物流跟踪
              </el-button>
            </div>
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

    <!-- 发货详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="发货详情" width="800px">
      <el-descriptions v-if="selectedOrder" :column="2" border>
        <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(selectedOrder.status)">{{ getStatusText(selectedOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ selectedOrder.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ selectedOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="用户姓名">{{ selectedOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ selectedOrder.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ selectedOrder.shippingAddress }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ selectedOrder.logisticsCompany || '未发货' }}</el-descriptions-item>
        <el-descriptions-item label="快递单号">{{ selectedOrder.trackingNumber || '未发货' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ selectedOrder.shippedAt || '未发货' }}</el-descriptions-item>
        <el-descriptions-item label="预计送达">{{ selectedOrder.estimatedDelivery || '未知' }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin: 20px 0 10px 0;">商品清单</h4>
      <el-table :data="selectedOrder?.orderItems" style="width: 100%">
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
        <el-form-item label="预计送达">
          <el-date-picker
            v-model="shipForm.estimatedDelivery"
            type="datetime"
            placeholder="选择预计送达时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
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
        <el-form-item label="预计送达">
          <el-date-picker
            v-model="logisticsForm.estimatedDelivery"
            type="datetime"
            placeholder="选择预计送达时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showLogisticsDialog = false">取消</el-button>
        <el-button type="primary" @click="updateLogisticsInfo" :loading="updatingLogistics">更新</el-button>
      </template>
    </el-dialog>

    <!-- 物流跟踪对话框 -->
    <el-dialog v-model="showTrackDialog" title="物流跟踪" width="600px">
      <div v-if="trackingInfo">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ trackingInfo.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="物流公司">{{ trackingInfo.logisticsCompany }}</el-descriptions-item>
          <el-descriptions-item label="快递单号">{{ trackingInfo.trackingNumber }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ trackingInfo.currentStatus }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 10px 0;">物流轨迹</h4>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in trackingInfo.trackingHistory"
            :key="index"
            :timestamp="item.time"
            :type="index === 0 ? 'primary' : 'info'"
          >
            {{ item.description }}
          </el-timeline-item>
        </el-timeline>
      </div>
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
const showTrackDialog = ref(false)
const shipFormRef = ref()
const logisticsFormRef = ref()

const shippingList = ref([])
const selectedOrder = ref(null)
const trackingInfo = ref(null)

const filterForm = reactive({
  status: '',
  orderNo: '',
  logisticsCompany: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const shipForm = reactive({
  orderNo: '',
  logisticsCompany: '',
  trackingNumber: '',
  estimatedDelivery: ''
})

const logisticsForm = reactive({
  orderNo: '',
  logisticsCompany: '',
  trackingNumber: '',
  estimatedDelivery: ''
})

const shipRules = {
  logisticsCompany: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  trackingNumber: [{ required: true, message: '请输入快递单号', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const typeMap = {
    'PAID': 'warning',
    'SHIPPED': 'primary',
    'DELIVERED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PAID': '待发货',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

const loadShippingList = async () => {
  try {
    loading.value = true
    
    // 先获取供应商信息
    const supplierResponse = await getCurrentSupplierInfo()
    
    if (supplierResponse.code !== 200 || !supplierResponse.data) {
      ElMessage.error('获取供应商信息失败')
      return
    }
    
    const supplierId = supplierResponse.data.id
    
    const params = {
      current: pagination.current,
      size: pagination.size,
      supplierId: supplierId
    }
    
    if (filterForm.status) {
      params.status = filterForm.status
    }
    if (filterForm.orderNo) {
      params.orderNo = filterForm.orderNo
    }
    if (filterForm.logisticsCompany) {
      params.logisticsCompany = filterForm.logisticsCompany
    }
    
    const response = await request({
      url: `/order/supplier/${supplierId}`,
      method: 'get',
      params: {
        current: pagination.current,
        size: pagination.size,
        status: filterForm.status,
        orderNo: filterForm.orderNo
      }
    })
    
    if (response.code === 200) {
      shippingList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '加载发货信息失败')
    }
  } catch (error) {
    console.error('加载发货信息失败:', error)
    ElMessage.error('加载发货信息失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.current = 1
  loadShippingList()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.orderNo = ''
  filterForm.logisticsCompany = ''
  handleFilter()
}

const refreshShipping = () => {
  loadShippingList()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadShippingList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadShippingList()
}

const viewShippingDetail = (order) => {
  selectedOrder.value = order
  showDetailDialog.value = true
}

const shipOrder = (order) => {
  shipForm.orderNo = order.orderNo
  shipForm.logisticsCompany = ''
  shipForm.trackingNumber = ''
  shipForm.estimatedDelivery = ''
  showShipDialog.value = true
}

const confirmShip = async () => {
  try {
    await shipFormRef.value.validate()
    shipping.value = true
    
    const response = await request({
      url: `/order/ship/${shipForm.orderNo}`,
      method: 'put',
      params: {
        logisticsCompany: shipForm.logisticsCompany,
        trackingNumber: shipForm.trackingNumber,
        estimatedDelivery: shipForm.estimatedDelivery
      }
    })
    
    if (response.code === 200) {
      ElMessage.success('发货成功')
      showShipDialog.value = false
      loadShippingList()
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
  logisticsForm.estimatedDelivery = order.estimatedDelivery || ''
  showLogisticsDialog.value = true
}

const updateLogisticsInfo = async () => {
  try {
    await logisticsFormRef.value.validate()
    updatingLogistics.value = true
    
    const response = await request({
      url: `/order/logistics/${logisticsForm.orderNo}`,
      method: 'put',
      params: {
        logisticsCompany: logisticsForm.logisticsCompany,
        trackingNumber: logisticsForm.trackingNumber,
        estimatedDelivery: logisticsForm.estimatedDelivery
      }
    })
    
    if (response.code === 200) {
      ElMessage.success('物流信息更新成功')
      showLogisticsDialog.value = false
      loadShippingList()
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

const trackOrder = async (order) => {
  try {
    const response = await request({
      url: `/order/track/${order.orderNo}`,
      method: 'get'
    })
    
    if (response.code === 200) {
      trackingInfo.value = response.data
      showTrackDialog.value = true
    } else {
      ElMessage.error(response.message || '获取物流信息失败')
    }
  } catch (error) {
    console.error('获取物流信息失败:', error)
    ElMessage.error('获取物流信息失败')
  }
}

onMounted(() => {
  loadShippingList()
})
</script>

<style scoped>
.supplier-shipping {
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
  margin-bottom: 5px;
}

.quantity {
  color: #909399;
  font-size: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.action-buttons .el-button {
  margin: 0;
}
</style>