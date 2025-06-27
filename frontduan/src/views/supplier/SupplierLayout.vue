<template>
  <div class="supplier-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="250px" class="sidebar">
        <div class="logo">
          <h3>供应商后台</h3>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          router
          unique-opened
        >
          <el-menu-item index="/supplier/dashboard">
            <el-icon><House /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          
          <el-menu-item index="/supplier/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          
          <el-menu-item index="/supplier/orders">
            <el-icon><Document /></el-icon>
            <span>订单处理</span>
          </el-menu-item>
          
          <el-menu-item index="/supplier/shipping">
            <el-icon><Van /></el-icon>
            <span>发货管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-content">
            <div class="breadcrumb">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/supplier/dashboard' }">供应商后台</el-breadcrumb-item>
                <el-breadcrumb-item v-if="currentPageName">{{ currentPageName }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            
            <div class="user-actions">
              <el-dropdown @command="handleCommand">
                <span class="user-info">
                  <el-avatar :size="32" :src="userStore.user?.avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <span class="username">{{ userStore.user?.realName || userStore.user?.username }}</span>
                  <el-icon class="arrow-down"><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="home">返回首页</el-dropdown-item>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <!-- 主要内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前页面名称
const currentPageName = computed(() => {
  const routeMap = {
    '/supplier/dashboard': '仪表盘',
    '/supplier/products': '商品管理',
    '/supplier/orders': '订单处理',
    '/supplier/shipping': '发货管理'
  }
  return routeMap[route.path] || ''
})

// 处理用户操作
const handleCommand = async (command) => {
  switch (command) {
    case 'home':
      router.push('/')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.logout()
        router.push('/')
      } catch {
        // 用户取消
      }
      break
  }
}
</script>

<style scoped>
.supplier-layout {
  height: 100vh;
  background: #f5f5f5;
}

.sidebar {
  background: #001529;
  overflow: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #002140;
  color: white;
  margin-bottom: 1px;
}

.logo h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
  background: #001529;
}

.sidebar-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.65);
  border: none;
}

.sidebar-menu .el-menu-item:hover {
  background: #1890ff;
  color: white;
}

.sidebar-menu .el-menu-item.is-active {
  background: #1890ff;
  color: white;
}

.header {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.breadcrumb {
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

.arrow-down {
  font-size: 12px;
  color: #999;
}

.main-content {
  padding: 24px;
  background: #f5f5f5;
  min-height: calc(100vh - 64px);
  max-width: none;
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px !important;
  }
  
  .logo h3 {
    font-size: 16px;
  }
  
  .main-content {
    padding: 16px;
  }
}
</style>