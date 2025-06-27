<template>
  <div class="register-container">
    <div class="register-wrapper">
      <!-- 左侧图片区域 -->
      <div class="image-section">
        <img src="/images/products/login_register_in.png" alt="注册图片" class="register-image" />
      </div>
      
      <!-- 中间分隔线 -->
      <div class="divider"></div>
      
      <!-- 右侧注册表单区域 -->
      <div class="form-section">
        <div class="form-header">
          <h2>用户注册</h2>
        </div>
        
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
              clearable
            />
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="registerForm.realName"
              placeholder="请输入真实姓名"
              clearable
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              @click="handleRegister"
              :loading="loading"
              style="width: 100%"
            >
              注册
            </el-button>
          </el-form-item>
          
          <el-form-item>
            <el-link type="primary" @click="$router.push('/login')">
              已有账号？立即登录
            </el-link>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
  realName: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 20px;
  background-image: url('/images/products/login_register.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.register-wrapper {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 24px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  max-width: 1800px;
  width: 100%;
}

.image-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px;
  background: #f8f9fa;
}

.register-image {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
}

.divider {
  width: 2px;
  height: 800px;
  background: #e0e0e0;
  margin: 0;
}

.form-section {
  flex: 1;
  padding: 80px;
  background: white;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  color: #333;
  font-size: 36px;
  font-weight: 600;
  margin: 0;
}

.form-section .el-form {
  max-width: 720px;
  margin: 0 auto;
  font-size: 16px;
}

.form-section .el-input {
  font-size: 16px;
}

.form-section .el-button {
  font-size: 18px;
  height: 48px;
}

.form-section .el-form-item__label {
  font-size: 16px;
}

.form-section a {
  font-size: 14px;
}

.form-section .el-form-item:last-child {
  margin-bottom: 0;
  text-align: center;
}
</style>