<template>
  <div class="category-management">
    <el-card class="page-header">
      <div class="header-content">
        <h2>分类管理</h2>
        <p>管理商品分类信息</p>
      </div>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索分类名称"
            clearable
            @keyup.enter="loadCategories"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchStatus" placeholder="分类状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="loadCategories">
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

    <!-- 分类列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>分类列表</span>
          <div class="header-actions">
            
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="categoryList"
        stripe
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="分类图标" width="100">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.icon">
              <el-icon><Menu /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="商品数量" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row.productCount || 0 }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="editCategory(row)"
            >
              编辑
            </el-button>

            <el-button
              type="danger"
              size="small"
              @click="deleteCategory(row)"
              :disabled="row.productCount > 0"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑分类对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      title="编辑分类"
      width="600px"
      :before-close="closeCategoryDialog"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryFormRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-upload
            class="icon-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleIconSuccess"
            :before-upload="beforeIconUpload"
          >
            <el-avatar v-if="categoryForm.icon" :size="60" :src="categoryForm.icon" />
            <el-icon v-else class="icon-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="categoryForm.sort"
            :min="0"
            :max="999"
            placeholder="排序值"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="closeCategoryDialog">取消</el-button>
        <el-button type="primary" @click="saveCategory" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Download,
  Menu
} from '@element-plus/icons-vue'
import {
  getCategoryList,
  updateCategory,
  deleteCategory as deleteCategoryApi
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const categoryList = ref([])
const categoryDialogVisible = ref(false)
const currentCategory = ref(null)
const searchKeyword = ref('')
const searchStatus = ref('')
const categoryFormRef = ref()

// 上传配置
const uploadUrl = ref('http://localhost:8888/api/upload/image')

// 分类表单
const categoryForm = reactive({
  name: '',
  description: '',
  icon: '',
  sort: 0
})

// 表单验证规则
const categoryFormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '分类名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}



// 获取分类列表
const loadCategories = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value,
      status: searchStatus.value,
      current: 1,
      size: 1000  // 获取所有数据，不分页
    }
    
    const response = await getCategoryList(params)
    // 处理分页数据，提取records数组
    categoryList.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  searchStatus.value = ''
  loadCategories()
}

// 编辑分类
const editCategory = (category) => {
  currentCategory.value = category
  Object.assign(categoryForm, {
    name: category.name,
    description: category.description,
    icon: category.icon,
    sort: category.sort
  })
  categoryDialogVisible.value = true
}

// 保存分类
const saveCategory = async () => {
  if (!categoryFormRef.value) return
  
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await updateCategory(currentCategory.value.id, categoryForm)
        ElMessage.success('更新成功')
        
        categoryDialogVisible.value = false
        loadCategories()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        saving.value = false
      }
    }
  })
}

// 关闭分类对话框
const closeCategoryDialog = () => {
  categoryDialogVisible.value = false
  resetCategoryForm()
}

// 重置分类表单
const resetCategoryForm = () => {
  Object.assign(categoryForm, {
    name: '',
    description: '',
    icon: '',
    sort: 0
  })
  categoryFormRef.value?.resetFields()
}



// 删除分类
const deleteCategory = async (category) => {
  if (category.productCount > 0) {
    ElMessage.warning('该分类下还有商品，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${category.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteCategoryApi(category.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 图标上传成功处理
const handleIconSuccess = (response) => {
  if (response.code === 200) {
    categoryForm.icon = response.data.url
    ElMessage.success('图标上传成功')
  } else {
    ElMessage.error('图标上传失败')
  }
}

// 图标上传前验证
const beforeIconUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}



// 初始化
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-management {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 0;
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

.action-card {
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

/* 图标上传样式 */
.icon-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.icon-uploader:hover {
  border-color: #409eff;
}

.icon-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 60px;
  height: 60px;
  text-align: center;
  line-height: 60px;
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