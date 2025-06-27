<template>
  <div class="apply-container">
    <el-card class="apply-card">
      <template #header>
        <div class="card-header">
          <span>申请成为团长</span>
        </div>
      </template>
      
      <el-form
        ref="applyFormRef"
        :model="applyForm"
        :rules="rules"
        label-width="120px"
        class="apply-form"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="applyForm.name" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="applyForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="applyForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-form-item label="所在城市" prop="city">
          <el-input v-model="applyForm.city" placeholder="请输入所在城市" />
        </el-form-item>
        
        <el-form-item label="所在区域" prop="district">
          <el-input v-model="applyForm.district" placeholder="请输入所在区域" />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="applyForm.address"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        
        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请简述申请成为团长的理由"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitApplication" :loading="loading">
            提交申请
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { applyGroupLeader } from '@/api/groupleader'

const applyFormRef = ref()
const loading = ref(false)

const applyForm = reactive({
  name: '',
  phone: '',
  idCard: '',
  city: '',
  district: '',
  address: '',
  reason: ''
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入所在城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入所在区域', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
  ]
}

const submitApplication = async () => {
  try {
    await applyFormRef.value.validate()
    loading.value = true
    
    // 获取当前用户ID
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    // 添加userId到申请数据中
    const applicationData = {
      ...applyForm,
      userId: parseInt(userId)
    }
    
    await applyGroupLeader(applicationData)
    
    ElMessage.success('申请提交成功，请等待审核')
    resetForm()
  } catch (error) {
    console.error('申请失败:', error)
    // 检查是否是业务错误（后端返回的具体错误信息）
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('申请提交失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  applyFormRef.value?.resetFields()
}
</script>

<style scoped>
.apply-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
}

.apply-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.apply-form {
  margin-top: 20px;
}
</style>