import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 确保baseURL包含/api前缀
const request = axios.create({
  baseURL: 'http://localhost:8888/api',  // 添加/api前缀
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code === 200) {
      // 检查是否是管理界面的API请求（包括供应商管理）
      if (response.config.url.includes('/admin/') || response.config.url.includes('/supplier/')) {
        return response.data  // 返回完整的响应数据，包含code、message、data
      } else {
        return data
      }
    } else {
      // 不在这里显示错误消息，让调用方处理
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  error => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request