<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧图片区域 -->
      <div class="image-section">
        <img src="/images/products/login_register_in.png" alt="登录图片" class="login-image" />
      </div>
      
      <!-- 中间分隔线 -->
      <div class="divider"></div>
      
      <!-- 右侧登录表单区域 -->
      <div class="form-section">
        <div class="form-header">
          <h2>用户登录</h2>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-width="80px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              @click="handleLogin"
              :loading="loading"
              style="width: 100%"
            >
              登录
            </el-button>
          </el-form-item>
          
          <el-form-item>
            <el-link type="primary" @click="$router.push('/register')">
              还没有账号？立即注册
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
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.userLogin(loginForm)
        if (success) {
          // 根据用户角色跳转到不同页面
          if (userStore.isAdmin) {
            router.push('/admin')
          } else {
            router.push('/home')
          }
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
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

.login-wrapper {
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

.login-image {
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
  max-width: 640px;
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