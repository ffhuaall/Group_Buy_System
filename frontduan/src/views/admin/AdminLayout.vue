<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside width="250px" class="admin-sidebar">
      <div class="logo">
        <h3>管理后台</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        class="admin-menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表板</span>
        </el-menu-item>
        
        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户列表</span>
          </el-menu-item>
          <el-menu-item index="/admin/group-leaders">
            <el-icon><Avatar /></el-icon>
            <span>团长管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="product">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/admin/products">
            <el-icon><Box /></el-icon>
            <span>商品列表</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <el-icon><Menu /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/suppliers">
            <el-icon><Shop /></el-icon>
            <span>供应商管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="order">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/admin/orders">
            <el-icon><List /></el-icon>
            <span>订单列表</span>
          </el-menu-item>
          <el-menu-item index="/admin/refunds">
            <el-icon><RefreshLeft /></el-icon>
            <span>退款管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/admin/analytics">
          <el-icon><TrendCharts /></el-icon>
          <span>数据分析</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container class="admin-main">
      <!-- 顶部导航 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumbItems.length > 0" v-for="item in breadcrumbItems" :key="item.path" :to="{ path: item.path }">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="admin-user">
              <el-avatar :size="32" :src="userInfo.avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ userInfo.username || '管理员' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主要内容 -->
      <el-main class="admin-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Odometer,
  User,
  UserFilled,
  Avatar,
  Goods,
  Box,
  Menu,
  Shop,
  Document,
  List,
  RefreshLeft,
  TrendCharts,
  Setting,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({
  username: '管理员',
  avatar: ''
})

// 面包屑导航
const breadcrumbItems = computed(() => {
  const pathMap = {
    '/admin/dashboard': '仪表板',
    '/admin/users': '用户管理',
    '/admin/group-leaders': '团长管理',
    '/admin/products': '商品管理',
    '/admin/categories': '分类管理',
    '/admin/suppliers': '供应商管理',
    '/admin/orders': '订单管理',
    '/admin/refunds': '退款管理',
    '/admin/analytics': '数据分析',
    '/admin/settings': '系统设置'
  }
  
  const currentPath = route.path
  const items = []
  
  if (currentPath !== '/admin/dashboard' && pathMap[currentPath]) {
    items.push({
      name: pathMap[currentPath],
      path: currentPath
    })
  }
  
  return items
})

// 下拉菜单处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中')
      break
    case 'settings':
      router.push('/admin/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch {
    // 用户取消
  }
}

// 监听路由变化，验证管理员权限
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/admin') && userStore.user?.role !== 'ADMIN') {
    ElMessage.error('权限不足，请使用管理员账号登录')
    router.push('/login')
  }
}, { immediate: true })
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
}

.admin-sidebar {
  background-color: #304156;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
  color: white;
  margin-bottom: 0;
}

.logo h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.admin-menu {
  border: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.admin-menu .el-menu-item,
.admin-menu .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-user {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.admin-user:hover {
  background-color: #f5f5f5;
}

.username {
  margin: 0 8px;
  font-size: 14px;
  color: #606266;
}

.admin-content {
  background-color: #f0f2f5;
  padding: 12px;
  overflow-y: auto;
  flex: 1;
  min-height: calc(100vh - 60px);
  max-width: none;
  width: 100%;
}

/* 滚动条样式 */
.admin-menu::-webkit-scrollbar {
  width: 6px;
}

.admin-menu::-webkit-scrollbar-track {
  background: #2b3a4b;
}

.admin-menu::-webkit-scrollbar-thumb {
  background: #4a5568;
  border-radius: 3px;
}

.admin-menu::-webkit-scrollbar-thumb:hover {
  background: #5a6578;
}
</style>