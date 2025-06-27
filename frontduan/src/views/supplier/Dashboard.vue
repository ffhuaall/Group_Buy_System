<template>
  <div class="supplier-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalProducts }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ stats.pendingProducts }}</div>
            <div class="stat-label">待审核商品</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">¥{{ stats.totalRevenue }}</div>
            <div class="stat-label">总收入</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 供应商信息 -->
    <el-card class="info-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>供应商信息</span>
          <el-button type="primary" @click="showEditDialog = true">编辑信息</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="供应商名称">{{ supplierInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ supplierInfo.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ supplierInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ supplierInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ supplierInfo.address }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(supplierInfo.status)">{{ getStatusText(supplierInfo.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ supplierInfo.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- 最近订单 -->
    <el-card class="orders-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>最近订单</span>
        </div>
      </template>
      
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
      </el-table>
    </el-card>
    
    <!-- 编辑供应商信息对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑供应商信息" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="供应商名称" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="editForm.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="editForm.address" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updateSupplierInfo" :loading="updating">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSupplierInfo, updateSupplier } from '@/api/supplier'

const showEditDialog = ref(false)
const editFormRef = ref()
const updating = ref(false)

const stats = reactive({
  totalProducts: 0,
  pendingProducts: 0,
  totalOrders: 0,
  totalRevenue: 0
})

const supplierInfo = reactive({
  id: null,
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  status: '',
  createTime: ''
})

const editForm = reactive({
  id: null,
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: ''
})

const editRules = {
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

const recentOrders = ref([])

const getStatusType = (status) => {
  const typeMap = {
    'ACTIVE': 'success',
    'PENDING': 'warning',
    'INACTIVE': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'ACTIVE': '正常',
    'PENDING': '待审核',
    'INACTIVE': '停用'
  }
  return textMap[status] || status
}

const getOrderStatusType = (status) => {
  const typeMap = {
    '待付款': 'warning',
    '已付款': 'success',
    '已发货': 'primary',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return typeMap[status] || 'info'
}

const loadDashboardData = async () => {
  try {
    // 模拟数据，实际应该调用API
    stats.totalProducts = 25
    stats.pendingProducts = 3
    stats.totalOrders = 156
    stats.totalRevenue = 12580.50
    
    // 模拟供应商信息
    Object.assign(supplierInfo, {
      id: 1,
      name: '优质食品供应商',
      contactPerson: '张经理',
      phone: '13800138000',
      email: 'supplier@example.com',
      address: '北京市朝阳区xxx街道xxx号',
      status: 'ACTIVE',
      createTime: '2024-01-15 10:30:00'
    })
    
    // 模拟最近订单
    recentOrders.value = [
      {
        orderNo: 'ORD20241201001',
        productName: '有机苹果',
        quantity: 50,
        amount: 299.50,
        status: '已付款',
        createTime: '2024-12-01 14:30:00'
      },
      {
        orderNo: 'ORD20241201002',
        productName: '新鲜蔬菜包',
        quantity: 30,
        amount: 450.00,
        status: '已发货',
        createTime: '2024-12-01 16:20:00'
      }
    ]
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const updateSupplierInfo = async () => {
  try {
    await editFormRef.value.validate()
    updating.value = true
    
    await updateSupplier(editForm)
    
    // 更新本地数据
    Object.assign(supplierInfo, editForm)
    
    ElMessage.success('信息更新成功')
    showEditDialog.value = false
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 监听编辑对话框打开，复制当前信息到编辑表单
const openEditDialog = () => {
  Object.assign(editForm, {
    id: supplierInfo.id,
    name: supplierInfo.name,
    contactPerson: supplierInfo.contactPerson,
    phone: supplierInfo.phone,
    email: supplierInfo.email,
    address: supplierInfo.address
  })
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.supplier-dashboard {
  padding: 0;
  width: 100%;
  max-width: none;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-card, .orders-card {
  margin-top: 20px;
}
</style>