<template>
  <div class="supplier-management">
    <el-card class="page-header">
      <div class="header-content">
        <h2>供应商管理</h2>
        <p>管理系统中的所有供应商信息</p>
      </div>
    </el-card>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索供应商名称或联系人"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="审核状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已禁用" value="DISABLED" />
          </el-select>
        </el-col>
        <el-col :span="6">
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



    <!-- 供应商列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>供应商列表</span>
          <div class="header-actions">
            <el-button type="success" @click="batchApprove" :disabled="selectedSuppliers.length === 0">
              <el-icon><Check /></el-icon>
              批量通过
            </el-button>
            <el-button type="danger" @click="batchReject" :disabled="selectedSuppliers.length === 0">
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
            
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="supplierList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="供应商logo" width="100">
          <template #default="{ row }">
            <el-avatar :size="50" :src="row.logo">
              <el-icon><Shop /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="公司名称" width="200" show-overflow-tooltip />
        <el-table-column label="联系人" width="120">
          <template #default="{ row }">
            <div class="contact-info">
              <div class="name">{{ row.contactName }}</div>
              <div class="phone">{{ row.contactPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" width="200" show-overflow-tooltip />
        <el-table-column label="商品数量" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row.productCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewSupplier(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="success"
              size="small"
              @click="approveSupplier(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="rejectSupplier(row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'APPROVED'"
              type="warning"
              size="small"
              @click="disableSupplier(row)"
            >
              禁用
            </el-button>
            <el-button
              v-if="row.status === 'DISABLED'"
              type="success"
              size="small"
              @click="enableSupplier(row)"
            >
              启用
            </el-button>
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

    <!-- 供应商详情对话框 -->
    <el-dialog
      v-model="supplierDetailVisible"
      title="供应商详情"
      width="800px"
      :before-close="closeSupplierDetail"
    >
      <div v-if="currentSupplier" class="supplier-detail">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="logo-section">
              <el-avatar :size="120" :src="currentSupplier.logo">
                <el-icon><Shop /></el-icon>
              </el-avatar>
            </div>
          </el-col>
          <el-col :span="18">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="供应商ID">{{ currentSupplier.id }}</el-descriptions-item>
              <el-descriptions-item label="公司名称">{{ currentSupplier.companyName }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ currentSupplier.contactName }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ currentSupplier.contactPhone }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ currentSupplier.email }}</el-descriptions-item>
              <el-descriptions-item label="营业执照号">{{ currentSupplier.businessLicense || '未提供' }}</el-descriptions-item>
              <el-descriptions-item label="审核状态">
                <el-tag :type="getStatusTagType(currentSupplier.status)">{{ getStatusText(currentSupplier.status) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="商品数量">{{ currentSupplier.productCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="申请时间">{{ currentSupplier.createTime }}</el-descriptions-item>
              <el-descriptions-item label="审核时间">{{ currentSupplier.auditTime || '未审核' }}</el-descriptions-item>
              <el-descriptions-item label="公司地址" :span="2">
                {{ currentSupplier.address || '未提供' }}
              </el-descriptions-item>
              <el-descriptions-item label="公司简介" :span="2">
                <div class="company-description">{{ currentSupplier.description || '暂无简介' }}</div>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeSupplierDetail">关闭</el-button>
          <el-button
            v-if="currentSupplier?.status === 'PENDING'"
            type="success"
            @click="approveSupplier(currentSupplier)"
          >
            通过审核
          </el-button>
          <el-button
            v-if="currentSupplier?.status === 'PENDING'"
            type="danger"
            @click="rejectSupplier(currentSupplier)"
          >
            拒绝审核
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
  Shop
} from '@element-plus/icons-vue'
import {
  getSupplierList,
  approveSupplier as approveSupplierApi,
  rejectSupplier as rejectSupplierApi,
  updateSupplierStatus,
  getSupplierStats
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const supplierList = ref([])
const selectedSuppliers = ref([])
const supplierDetailVisible = ref(false)
const currentSupplier = ref(null)

// 统计数据
const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 获取供应商列表
const loadSupplierList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status,
      startDate: searchForm.dateRange?.[0],
      endDate: searchForm.dateRange?.[1]
    }
    
    const response = await getSupplierList(params)
    supplierList.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取供应商列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const loadStats = async () => {
  try {
    const response = await getSupplierStats()
    Object.assign(stats, response.data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadSupplierList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    dateRange: []
  })
  pagination.page = 1
  loadSupplierList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadSupplierList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadSupplierList()
}

// 选择处理
const handleSelectionChange = (selection) => {
  selectedSuppliers.value = selection
}

// 查看供应商详情
const viewSupplier = (supplier) => {
  currentSupplier.value = supplier
  supplierDetailVisible.value = true
}

// 关闭供应商详情
const closeSupplierDetail = () => {
  supplierDetailVisible.value = false
  currentSupplier.value = null
}

// 审核通过
const approveSupplier = async (supplier) => {
  try {
    await ElMessageBox.confirm(`确定要通过供应商 "${supplier.companyName}" 的审核吗？`, '确认审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await approveSupplierApi(supplier.id)
    ElMessage.success('审核通过')
    loadSupplierList()
    loadStats()
    closeSupplierDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 审核拒绝
const rejectSupplier = async (supplier) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `请输入拒绝供应商 "${supplier.companyName}" 的原因：`,
      '拒绝审核',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入拒绝原因'
          }
          return true
        }
      }
    )
    
    await rejectSupplierApi(supplier.id, reason)
    ElMessage.success('已拒绝审核')
    loadSupplierList()
    loadStats()
    closeSupplierDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 禁用供应商
const disableSupplier = async (supplier) => {
  try {
    await ElMessageBox.confirm(`确定要禁用供应商 "${supplier.companyName}" 吗？`, '确认禁用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updateSupplierStatus(supplier.id, 'DISABLED')
    ElMessage.success('禁用成功')
    loadSupplierList()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 启用供应商
const enableSupplier = async (supplier) => {
  try {
    await ElMessageBox.confirm(`确定要启用供应商 "${supplier.companyName}" 吗？`, '确认启用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await updateSupplierStatus(supplier.id, 'APPROVED')
    ElMessage.success('启用成功')
    loadSupplierList()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 批量审核通过
const batchApprove = async () => {
  const pendingSuppliers = selectedSuppliers.value.filter(s => s.status === 'PENDING')
  if (pendingSuppliers.length === 0) {
    ElMessage.warning('请选择待审核的供应商')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要批量通过 ${pendingSuppliers.length} 个供应商的审核吗？`, '批量审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    for (const supplier of pendingSuppliers) {
      await approveSupplierApi(supplier.id)
    }
    
    ElMessage.success('批量审核通过成功')
    loadSupplierList()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}

// 批量审核拒绝
const batchReject = async () => {
  const pendingSuppliers = selectedSuppliers.value.filter(s => s.status === 'PENDING')
  if (pendingSuppliers.length === 0) {
    ElMessage.warning('请选择待审核的供应商')
    return
  }
  
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `请输入批量拒绝 ${pendingSuppliers.length} 个供应商的原因：`,
      '批量拒绝审核',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入拒绝原因'
          }
          return true
        }
      }
    )
    
    for (const supplier of pendingSuppliers) {
      await rejectSupplierApi(supplier.id, reason)
    }
    
    ElMessage.success('批量拒绝成功')
    loadSupplierList()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}



// 工具函数
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'DISABLED': '已禁用'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'DISABLED': 'info'
  }
  return typeMap[status] || ''
}

// 初始化
onMounted(() => {
  loadSupplierList()
  loadStats()
})
</script>

<style scoped>
.supplier-management {
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
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

.supplier-detail {
  padding: 20px 0;
}

.logo-section {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.company-description {
  max-height: 100px;
  overflow-y: auto;
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.contact-info {
  line-height: 1.4;
}

.name {
  font-weight: 600;
  color: #303133;
}

.phone {
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

/* 头像样式 */
.el-avatar {
  border: 2px solid #f0f0f0;
}
</style>