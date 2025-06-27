<template>
  <div class="supplier-products">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              添加商品
            </el-button>
            <el-button @click="refreshProducts" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="商品状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable @change="handleFilter">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已上架" value="ACTIVE" />
              <el-option label="已下架" value="INACTIVE" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品名称">
            <el-input v-model="filterForm.name" placeholder="请输入商品名称" clearable @keyup.enter="handleFilter" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilter">搜索</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品列表 -->
      <el-table :data="products" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editProduct(scope.row)">
              编辑
            </el-button>
            <el-button 
              v-if="scope.row.status === 'INACTIVE'" 
              type="success" 
              size="small" 
              @click="updateProductStatus(scope.row.id, 'ACTIVE')"
            >
              上架
            </el-button>
            <el-button 
              v-if="scope.row.status === 'ACTIVE'" 
              type="warning" 
              size="small" 
              @click="updateProductStatus(scope.row.id, 'INACTIVE')"
            >
              下架
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteProduct(scope.row)"
            >
              删除
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

    <!-- 添加/编辑商品对话框 -->
    <el-dialog v-model="showAddDialog" :title="isEdit ? '编辑商品' : '添加商品'" width="600px">
      <el-form ref="productFormRef" :model="productForm" :rules="productRules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择商品分类">
            <el-option label="生鲜蔬菜" value="生鲜蔬菜" />
            <el-option label="水果" value="水果" />
            <el-option label="肉类" value="肉类" />
            <el-option label="海鲜" value="海鲜" />
            <el-option label="粮油调料" value="粮油调料" />
            <el-option label="日用品" value="日用品" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="商品图片" prop="imageUrl">
          <el-input v-model="productForm.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCurrentSupplierInfo } from '@/api/supplier'
import request from '@/api/request'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const isEdit = ref(false)
const productFormRef = ref()

const products = ref([])
const filterForm = reactive({
  status: '',
  name: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const productForm = reactive({
  id: null,
  name: '',
  category: '',
  price: 0,
  stock: 0,
  description: '',
  imageUrl: ''
})

const productRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

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
    'ACTIVE': '已上架',
    'PENDING': '待审核',
    'INACTIVE': '已下架'
  }
  return textMap[status] || status
}

const loadProducts = async () => {
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
    if (filterForm.name) {
      params.name = filterForm.name
    }
    
    const response = await request({
      url: '/product/supplier/list',
      method: 'get',
      params
    })
    
    if (response.code === 200) {
      products.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '加载商品失败')
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.current = 1
  loadProducts()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.name = ''
  handleFilter()
}

const refreshProducts = () => {
  loadProducts()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadProducts()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadProducts()
}

const editProduct = (product) => {
  isEdit.value = true
  productForm.id = product.id
  productForm.name = product.name
  productForm.category = product.category
  productForm.price = product.price
  productForm.stock = product.stock
  productForm.description = product.description
  productForm.imageUrl = product.imageUrl
  showAddDialog.value = true
}

const resetProductForm = () => {
  productForm.id = null
  productForm.name = ''
  productForm.category = ''
  productForm.price = 0
  productForm.stock = 0
  productForm.description = ''
  productForm.imageUrl = ''
  isEdit.value = false
}

const saveProduct = async () => {
  try {
    await productFormRef.value.validate()
    saving.value = true
    
    // 先获取供应商信息
    const supplierResponse = await getCurrentSupplierInfo()
    
    if (supplierResponse.code !== 200 || !supplierResponse.data) {
      ElMessage.error('获取供应商信息失败')
      return
    }
    
    const supplierId = supplierResponse.data.id
    
    const productData = {
      ...productForm,
      supplierId: supplierId
    }
    
    const response = await request({
      url: isEdit.value ? '/product/update' : '/product/add',
      method: isEdit.value ? 'put' : 'post',
      data: productData
    })
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '商品更新成功' : '商品添加成功')
      showAddDialog.value = false
      resetProductForm()
      loadProducts()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('保存商品失败:', error)
    ElMessage.error('保存商品失败')
  } finally {
    saving.value = false
  }
}

const updateProductStatus = async (productId, status) => {
  try {
    const response = await request({
      url: `/product/status/${productId}`,
      method: 'put',
      params: { status }
    })
    
    if (response.code === 200) {
      ElMessage.success('商品状态更新成功')
      loadProducts()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新商品状态失败:', error)
    ElMessage.error('更新失败')
  }
}

const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.name}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await request({
      url: `/product/${product.id}`,
      method: 'delete'
    })
    
    if (response.code === 200) {
      ElMessage.success('商品删除成功')
      loadProducts()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.supplier-products {
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

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>