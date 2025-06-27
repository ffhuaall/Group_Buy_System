<template>
  <div class="refund-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>退款管理</h2>
      <p>管理用户退款申请和处理退款流程</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon pending">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.pending }}</div>
                <div class="stat-label">待处理</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon approved">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.approved }}</div>
                <div class="stat-label">已同意</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon rejected">
                <el-icon><Close /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.rejected }}</div>
                <div class="stat-label">已拒绝</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon completed">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.completed }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和操作区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入订单号或用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="退款状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="待处理" value="pending" />
            <el-option label="已同意" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">
            搜索
          </el-button>
          <el-button @click="handleReset" :icon="Refresh">
            重置
          </el-button>
          
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 退款列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>退款列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              @click="handleBatchApprove"
              :disabled="selectedRefunds.length === 0"
            >
              批量同意
            </el-button>
            <el-button
              type="danger"
              @click="handleBatchReject"
              :disabled="selectedRefunds.length === 0"
            >
              批量拒绝
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="refundList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="退款ID" width="100" />
        <el-table-column prop="orderNo" label="订单号" width="150" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="productName" label="商品" width="200" show-overflow-tooltip />
        <el-table-column prop="refundAmount" label="退款金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.refundAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column prop="processTime" label="处理时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              同意
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'approved'"
              type="warning"
              size="small"
              @click="handleComplete(row)"
            >
              完成退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
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

    <!-- 退款详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="退款详情"
      width="800px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentRefund" class="refund-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="退款ID">{{ currentRefund.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentRefund.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentRefund.userName }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ currentRefund.productName }}</el-descriptions-item>
          <el-descriptions-item label="退款金额">
            <span class="amount">¥{{ currentRefund.refundAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentRefund.status)">
              {{ getStatusText(currentRefund.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentRefund.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="处理时间">{{ currentRefund.processTime || '未处理' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-section">
          <h4>退款原因</h4>
          <p>{{ currentRefund.reason }}</p>
        </div>
        
        <div v-if="currentRefund.images && currentRefund.images.length > 0" class="detail-section">
          <h4>凭证图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(image, index) in currentRefund.images"
              :key="index"
              :src="image"
              :preview-src-list="currentRefund.images"
              :initial-index="index"
              fit="cover"
              class="refund-image"
            />
          </div>
        </div>
        
        <div v-if="currentRefund.adminNote" class="detail-section">
          <h4>处理备注</h4>
          <p>{{ currentRefund.adminNote }}</p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseDetail">关闭</el-button>
          <el-button
            v-if="currentRefund && currentRefund.status === 'pending'"
            type="success"
            @click="handleApprove(currentRefund)"
          >
            同意退款
          </el-button>
          <el-button
            v-if="currentRefund && currentRefund.status === 'pending'"
            type="danger"
            @click="handleReject(currentRefund)"
          >
            拒绝退款
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理退款对话框 -->
    <el-dialog
      v-model="processDialogVisible"
      :title="processType === 'approve' ? '同意退款' : '拒绝退款'"
      width="500px"
    >
      <el-form :model="processForm" label-width="80px">
        <el-form-item label="处理备注">
          <el-input
            v-model="processForm.note"
            type="textarea"
            :rows="4"
            :placeholder="processType === 'approve' ? '请输入同意退款的备注' : '请输入拒绝退款的原因'"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="processDialogVisible = false">取消</el-button>
          <el-button
            :type="processType === 'approve' ? 'success' : 'danger'"
            @click="handleConfirmProcess"
          >
            确认{{ processType === 'approve' ? '同意' : '拒绝' }}
          </el-button>
        </div>
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
  Download,
  Clock,
  Check,
  Close,
  Money
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const processDialogVisible = ref(false)
const processType = ref('approve') // 'approve' | 'reject'
const currentRefund = ref(null)
const selectedRefunds = ref([])

// 统计数据
const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  completed: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  dateRange: []
})

// 处理表单
const processForm = reactive({
  note: ''
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 退款列表
const refundList = ref([])

// 模拟数据
const mockRefunds = [
  {
    id: 1,
    orderNo: 'ORD202312150001',
    userName: '张三',
    productName: '新鲜苹果 5斤装',
    refundAmount: 29.90,
    reason: '商品质量问题',
    status: 'pending',
    applyTime: '2023-12-15 10:30:00',
    processTime: null,
    images: ['/images/refund1.jpg'],
    adminNote: null
  },
  {
    id: 2,
    orderNo: 'ORD202312150002',
    userName: '李四',
    productName: '有机蔬菜套餐',
    refundAmount: 45.00,
    reason: '收到商品与描述不符',
    status: 'approved',
    applyTime: '2023-12-14 15:20:00',
    processTime: '2023-12-14 16:30:00',
    images: ['/images/refund2.jpg', '/images/refund3.jpg'],
    adminNote: '同意退款，已核实商品问题'
  },
  {
    id: 3,
    orderNo: 'ORD202312150003',
    userName: '王五',
    productName: '进口牛奶 12盒',
    refundAmount: 68.00,
    reason: '个人原因不需要了',
    status: 'rejected',
    applyTime: '2023-12-13 09:15:00',
    processTime: '2023-12-13 14:20:00',
    images: [],
    adminNote: '个人原因不符合退款条件'
  }
]

// 加载退款列表
const loadRefundList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟分页和搜索
    let filteredData = [...mockRefunds]
    
    if (searchForm.keyword) {
      filteredData = filteredData.filter(item => 
        item.orderNo.includes(searchForm.keyword) || 
        item.userName.includes(searchForm.keyword)
      )
    }
    
    if (searchForm.status) {
      filteredData = filteredData.filter(item => item.status === searchForm.status)
    }
    
    pagination.total = filteredData.length
    const start = (pagination.current - 1) * pagination.size
    const end = start + pagination.size
    refundList.value = filteredData.slice(start, end)
    
  } catch (error) {
    ElMessage.error('加载退款列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 300))
    
    // 模拟统计数据
    stats.pending = mockRefunds.filter(item => item.status === 'pending').length
    stats.approved = mockRefunds.filter(item => item.status === 'approved').length
    stats.rejected = mockRefunds.filter(item => item.status === 'rejected').length
    stats.completed = mockRefunds.filter(item => item.status === 'completed').length
    
  } catch (error) {
    ElMessage.error('加载统计数据失败: ' + error.message)
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  loadRefundList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    dateRange: []
  })
  pagination.current = 1
  loadRefundList()
}



// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadRefundList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadRefundList()
}

// 选择处理
const handleSelectionChange = (selection) => {
  selectedRefunds.value = selection
}

// 查看退款详情
const handleView = (refund) => {
  currentRefund.value = refund
  detailDialogVisible.value = true
}

// 关闭详情对话框
const handleCloseDetail = () => {
  detailDialogVisible.value = false
  currentRefund.value = null
}

// 同意退款
const handleApprove = (refund) => {
  currentRefund.value = refund
  processType.value = 'approve'
  processForm.note = ''
  processDialogVisible.value = true
}

// 拒绝退款
const handleReject = (refund) => {
  currentRefund.value = refund
  processType.value = 'reject'
  processForm.note = ''
  processDialogVisible.value = true
}

// 完成退款
const handleComplete = async (refund) => {
  try {
    await ElMessageBox.confirm(
      `确认完成退款 ¥${refund.refundAmount} 吗？`,
      '确认完成',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success('退款已完成')
    loadRefundList()
    loadStats()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('完成退款失败: ' + error.message)
    }
  }
}

// 确认处理退款
const handleConfirmProcess = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const action = processType.value === 'approve' ? '同意' : '拒绝'
    ElMessage.success(`${action}退款成功`)
    
    processDialogVisible.value = false
    detailDialogVisible.value = false
    loadRefundList()
    loadStats()
    
  } catch (error) {
    ElMessage.error('处理退款失败: ' + error.message)
  }
}

// 批量同意
const handleBatchApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确认批量同意 ${selectedRefunds.value.length} 个退款申请吗？`,
      '批量同意',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('批量同意成功')
    loadRefundList()
    loadStats()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量同意失败: ' + error.message)
    }
  }
}

// 批量拒绝
const handleBatchReject = async () => {
  try {
    await ElMessageBox.confirm(
      `确认批量拒绝 ${selectedRefunds.value.length} 个退款申请吗？`,
      '批量拒绝',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('批量拒绝成功')
    loadRefundList()
    loadStats()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量拒绝失败: ' + error.message)
    }
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待处理',
    approved: '已同意',
    rejected: '已拒绝',
    completed: '已完成'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    completed: 'info'
  }
  return typeMap[status] || ''
}

// 页面加载时获取数据
onMounted(() => {
  loadRefundList()
  loadStats()
})
</script>

<style scoped>
.refund-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-container {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
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

.stat-icon.pending {
  background: linear-gradient(135deg, #f39c12, #e67e22);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.search-card {
  margin-bottom: 20px;
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

.amount {
  color: #e6a23c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.refund-detail {
  padding: 20px 0;
}

.detail-section {
  margin-top: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.detail-section p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.image-gallery {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.refund-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>