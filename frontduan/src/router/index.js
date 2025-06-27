import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/ProductList.vue')
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/Checkout.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/payment/:orderId',
    name: 'Payment',
    component: () => import('@/views/Payment.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/address',
    name: 'AddressManagement',
    component: () => import('@/views/AddressManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Order.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order-detail/:orderId',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue')
      },
      {
        path: 'products',
        name: 'ProductManagement',
        component: () => import('@/views/admin/ProductManagement.vue')
      },
      {
        path: 'categories',
        name: 'CategoryManagement',
        component: () => import('@/views/admin/CategoryManagement.vue')
      },
      {
        path: 'orders',
        name: 'OrderManagement',
        component: () => import('@/views/admin/OrderManagement.vue')
      },
      {
        path: 'refunds',
        name: 'RefundManagement',
        component: () => import('@/views/admin/RefundManagement.vue')
      },
      {
        path: 'suppliers',
        name: 'SupplierManagement',
        component: () => import('@/views/admin/SupplierManagement.vue')
      },
      {
        path: 'group-leaders',
        name: 'GroupLeaderManagement',
        component: () => import('@/views/admin/GroupLeaderManagement.vue')
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: () => import('@/views/admin/Analytics.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/admin/Settings.vue')
      }
    ]
  },
  {
    path: '/groupleader/apply',
    name: 'GroupLeaderApply',
    component: () => import('@/views/groupleader/Apply.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/supplier',
    component: () => import('@/views/supplier/SupplierLayout.vue'),
    redirect: '/supplier/dashboard',
    meta: { requiresAuth: true, requiresSupplier: true },
    children: [
      {
        path: 'dashboard',
        name: 'SupplierDashboard',
        component: () => import('@/views/supplier/Dashboard.vue')
      },
      {
        path: 'products',
        name: 'SupplierProducts',
        component: () => import('@/views/supplier/Products.vue')
      },
      {
        path: 'orders',
        name: 'SupplierOrders',
        component: () => import('@/views/supplier/Orders.vue')
      },
      {
        path: 'shipping',
        name: 'SupplierShipping',
        component: () => import('@/views/supplier/Shipping.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/home')
  } else if (to.path.startsWith('/admin') && userStore.user?.role !== 'ADMIN') {
    // 检查admin路由权限
    next('/login')
  } else {
    next()
  }
})

export default router