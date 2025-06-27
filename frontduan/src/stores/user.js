import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isSupplier = computed(() => user.value?.role === 'SUPPLIER')
  const isGroupLeader = computed(() => user.value?.role === 'GROUP_LEADER')
  
  // 在用户登录成功后，确保保存完整的用户信息
  const userLogin = async (loginForm) => {
    try {
      const response = await loginApi(loginForm)
      if (response && response.token) {
        token.value = response.token
        user.value = response.user // 确保包含用户的完整信息，包括id
        localStorage.setItem('token', response.token)
        localStorage.setItem('user', JSON.stringify(response.user))
        localStorage.setItem('userId', response.user.id.toString()) // 单独保存userId
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }
  
  const logout = () => {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('userId')
  }
  
  const initUser = () => {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('token')
    
    if (savedUser && savedToken) {
      try {
        const userData = JSON.parse(savedUser)
        user.value = userData
        token.value = savedToken
        // 确保userId也存在于localStorage中
        if (userData.id && !localStorage.getItem('userId')) {
          localStorage.setItem('userId', userData.id.toString())
        }
      } catch (error) {
        console.error('解析用户信息失败:', error)
        logout()
      }
    }
  }
  
  // 在store初始化时调用
  initUser()
  
  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    isSupplier,
    isGroupLeader,
    userLogin,
    logout,
    initUser
  }
})