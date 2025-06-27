<template>
  <div id="app">
    <Header v-if="!isAdminRoute" />
    <main :class="['main-content', { 'admin-route': isAdminRoute }]">
      <router-view />
    </main>
    <Footer v-if="!isAdminRoute" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin') || route.path.startsWith('/supplier') || route.path === '/login' || route.path === '/register')
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  width: 100%;
}

/* 非管理后台页面的容器限制 */
.main-content:not(.admin-route) {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 管理后台页面全宽显示 */
.main-content.admin-route {
  max-width: none;
  margin: 0;
  padding: 0;
}

/* 通用容器样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}
</style>