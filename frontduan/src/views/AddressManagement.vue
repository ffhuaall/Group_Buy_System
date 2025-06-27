<template>
  <div class="address-management">
    <div class="page-header">
      <h2>地址管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        添加新地址
      </el-button>
    </div>

    <!-- 地址列表 -->
    <div class="address-list" v-loading="loading">
      <div v-if="addresses.length === 0" class="empty-state">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="showAddDialog = true">添加地址</el-button>
        </el-empty>
      </div>
      
      <div v-else class="address-grid">
        <el-card 
          v-for="address in addresses" 
          :key="address.id" 
          class="address-card"
          :class="{ 'default-address': address.isDefault }"
        >
          <template #header>
            <div class="card-header">
              <span class="receiver-info">
                {{ address.receiverName }} {{ address.receiverPhone }}
              </span>
              <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
            </div>
          </template>
          
          <div class="address-content">
            <p class="address-detail">
              {{ address.province }} {{ address.city }} {{ address.district }}
            </p>
            <p class="address-detail">
              {{ address.detailAddress }}
            </p>
          </div>
          
          <div class="address-actions">
            <el-button size="small" @click="editAddress(address)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              v-if="!address.isDefault" 
              size="small" 
              type="primary" 
              @click="setDefault(address.id)"
            >
              <el-icon><Star /></el-icon>
              设为默认
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteAddress(address.id)"
              :disabled="address.isDefault"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog 
      v-model="showAddDialog" 
      :title="isEditing ? '编辑地址' : '添加地址'" 
      width="500px"
      @close="resetForm"
    >
      <el-form 
        :model="addressForm" 
        :rules="addressRules" 
        ref="addressFormRef" 
        label-width="100px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input 
            v-model="addressForm.receiverName" 
            placeholder="请输入收货人姓名" 
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input 
            v-model="addressForm.receiverPhone" 
            placeholder="请输入手机号" 
            maxlength="11"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input 
                v-model="addressForm.province" 
                placeholder="省份" 
                maxlength="20"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input 
                v-model="addressForm.city" 
                placeholder="城市" 
                maxlength="20"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input 
                v-model="addressForm.district" 
                placeholder="区县" 
                maxlength="20"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input 
            v-model="addressForm.detailAddress" 
            type="textarea" 
            :rows="3"
            placeholder="请输入详细地址，如街道、门牌号等" 
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveAddress" :loading="saving">
            {{ isEditing ? '更新' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Star } from '@element-plus/icons-vue'
import { 
  getUserAddresses, 
  addAddress, 
  updateAddress, 
  deleteAddress as deleteAddressApi, 
  setDefaultAddress 
} from '@/api/address'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const addresses = ref([])
const showAddDialog = ref(false)
const isEditing = ref(false)
const addressFormRef = ref()

// 地址表单数据
const addressForm = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

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

// 方法
const loadAddresses = async () => {
  try {
    loading.value = true
    const data = await getUserAddresses()
    addresses.value = data
  } catch (error) {
    ElMessage.error('获取地址列表失败')
  } finally {
    loading.value = false
  }
}

const editAddress = (address) => {
  isEditing.value = true
  Object.assign(addressForm, address)
  showAddDialog.value = true
}

const saveAddress = async () => {
  try {
    await addressFormRef.value.validate()
    saving.value = true
    
    if (isEditing.value) {
      await updateAddress(addressForm.id, addressForm)
      ElMessage.success('地址更新成功')
    } else {
      await addAddress(addressForm)
      ElMessage.success('地址添加成功')
    }
    
    showAddDialog.value = false
    await loadAddresses()
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    saving.value = false
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个地址吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteAddressApi(id)
    ElMessage.success('地址删除成功')
    await loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除地址失败')
    }
  }
}

const setDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('默认地址设置成功')
    await loadAddresses()
  } catch (error) {
    ElMessage.error('设置默认地址失败')
  }
}

const resetForm = () => {
  isEditing.value = false
  Object.assign(addressForm, {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  })
  addressFormRef.value?.resetFields()
}

// 生命周期
onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.address-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.address-list {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.address-card {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.address-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.address-card.default-address {
  border-color: #67c23a;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.receiver-info {
  font-weight: 600;
  color: #303133;
}

.address-content {
  margin: 15px 0;
}

.address-detail {
  margin: 5px 0;
  color: #606266;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .address-management {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .address-grid {
    grid-template-columns: 1fr;
  }
  
  .address-actions {
    justify-content: center;
  }
}
</style>