import request from './request'

// 获取仪表板数据
export const getDashboardData = () => {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

// 商品审核
export const approveProduct = (productId) => {
  return request({
    url: `/admin/product/approve/${productId}`,
    method: 'put'
  })
}

export const rejectProduct = (productId) => {
  return request({
    url: `/admin/product/reject/${productId}`,
    method: 'put'
  })
}

// 用户管理
export const getUserList = (params) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export const createUser = (data) => {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

export const updateUser = (userId, data) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'put',
    data
  })
}

export const deleteUser = (userId) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'delete'
  })
}

export const updateUserStatus = (userId, status) => {
  return request({
    url: `/admin/user/status/${userId}`,
    method: 'put',
    params: { status }
  })
}

// 商品管理
export const getProductList = (params) => {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

export const updateProductStatus = (productId, status) => {
  return request({
    url: `/admin/products/${productId}/status`,
    method: 'put',
    params: { status }
  })
}

// 订单管理
export const getOrderList = (params) => {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

export const updateOrderStatus = (orderId, status, reason = null) => {
  const data = { status }
  if (reason) {
    data.reason = reason
  }
  return request({
    url: `/admin/orders/${orderId}/status`,
    method: 'put',
    data
  })
}

export const getOrderStats = () => {
  return request({
    url: '/admin/orders/stats',
    method: 'get'
  })
}

export const getOrderStatistics = (params) => {
  return request({
    url: '/admin/analytics/orders',
    method: 'get',
    params
  })
}

// 系统设置相关API
export const getSystemSettings = () => {
  return request({
    url: '/admin/settings',
    method: 'get'
  })
}

export const saveSystemSettings = (type, settings) => {
  return request({
    url: `/admin/settings/${type}`,
    method: 'post',
    data: settings
  })
}

// 系统日志相关API
export const getSystemLogs = (params) => {
  return request({
    url: '/admin/logs',
    method: 'get',
    params
  })
}

export const clearSystemLogs = () => {
  return request({
    url: '/admin/logs',
    method: 'delete'
  })
}

// 数据备份相关API
export const getBackupList = () => {
  return request({
    url: '/admin/backups',
    method: 'get'
  })
}

export const createSystemBackup = () => {
  return request({
    url: '/admin/backup',
    method: 'post'
  })
}

export const downloadSystemBackup = (id) => {
  return request({
    url: `/admin/backup/download/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

export const restoreSystemBackup = (id) => {
  return request({
    url: `/admin/backup/restore/${id}`,
    method: 'post'
  })
}

export const deleteSystemBackup = (id) => {
  return request({
    url: `/admin/backup/${id}`,
    method: 'delete'
  })
}

// 分类管理相关API
export const getCategoryList = (params) => {
  return request({
    url: '/category/admin/list',
    method: 'get',
    params
  })
}

export const createCategory = (data) => {
  return request({
    url: '/category/create',
    method: 'post',
    data
  })
}

export const updateCategory = (id, data) => {
  return request({
    url: `/category/admin/update/${id}`,
    method: 'put',
    data
  })
}

export const deleteCategory = (id) => {
  return request({
    url: `/category/admin/delete/${id}`,
    method: 'delete'
  })
}

export const updateCategoryStatus = (id, status) => {
  return request({
    url: `/category/status/${id}`,
    method: 'put',
    data: { status }
  })
}

export const getCategoryStats = () => {
  return request({
    url: '/admin/categories/stats',
    method: 'get'
  })
}

// 供应商管理相关API
export const getSupplierList = (params) => {
  return request({
    url: '/supplier/list',
    method: 'get',
    params
  })
}

export const createSupplier = (data) => {
  return request({
    url: '/supplier/create',
    method: 'post',
    data
  })
}

export const updateSupplier = (id, data) => {
  return request({
    url: `/supplier/update/${id}`,
    method: 'put',
    data
  })
}

export const deleteSupplier = (id) => {
  return request({
    url: `/supplier/delete/${id}`,
    method: 'delete'
  })
}

export const approveSupplier = (id) => {
  return request({
    url: `/supplier/approve/${id}`,
    method: 'put'
  })
}

export const rejectSupplier = (id) => {
  return request({
    url: `/supplier/reject/${id}`,
    method: 'put'
  })
}

export const getSupplierStats = () => {
  return request({
    url: '/admin/suppliers/stats',
    method: 'get'
  })
}

export const updateSupplierStatus = (id, status) => {
  return request({
    url: `/supplier/status/${id}`,
    method: 'put',
    data: { status }
  })
}

// 团长管理相关API
export const getGroupLeaderList = (params) => {
  return request({
    url: '/admin/group-leaders/page',
    method: 'get',
    params
  })
}

export const approveGroupLeader = (id) => {
  return request({
    url: `/groupleader/approve/${id}`,
    method: 'put'
  })
}

export const rejectGroupLeader = (id) => {
  return request({
    url: `/groupleader/reject/${id}`,
    method: 'put'
  })
}

export const assignCommunity = (id, communityId) => {
  return request({
    url: `/groupleader/assign/${id}`,
    method: 'put',
    data: { communityId }
  })
}

export const assignCommunityToLeader = (leaderId, communityId) => {
  return request({
    url: `/groupleader/assign/${leaderId}`,
    method: 'put',
    data: { communityId }
  })
}

export const updateGroupLeaderStatus = (id, status) => {
  return request({
    url: `/groupleader/status/${id}`,
    method: 'put',
    data: { status }
  })
}

export const createGroupLeader = (data) => {
  return request({
    url: '/groupleader/create',
    method: 'post',
    data
  })
}

export const updateGroupLeader = (id, data) => {
  return request({
    url: `/groupleader/update/${id}`,
    method: 'put',
    data
  })
}

export const getGroupLeaders = (params) => {
  return request({
    url: '/groupleader/list',
    method: 'get',
    params
  })
}

// 获取团长统计数据
export const getGroupLeaderStats = () => {
  return request({
    url: '/admin/group-leaders/stats',
    method: 'get'
  })
}

// 获取社区列表
export const getCommunityList = () => {
  return request({
    url: '/admin/communities',
    method: 'get'
  })
}

// 数据分析相关
export const getAnalyticsData = (params) => {
  return request({
    url: '/admin/analytics',
    method: 'get',
    params
  })
}

export const getTopProducts = (params) => {
  return request({
    url: '/admin/analytics/products/top',
    method: 'get',
    params
  })
}

export const getRegionStats = (params) => {
  return request({
    url: '/admin/analytics/regions',
    method: 'get',
    params
  })
}

export const getTopLeaders = (params) => {
  return request({
    url: '/admin/analytics/leaders/top',
    method: 'get',
    params
  })
}

export const getTableData = (params) => {
  return request({
    url: '/admin/analytics/table',
    method: 'get',
    params
  })
}

// 退款管理相关
export const getRefundList = (params) => {
  return request({
    url: '/admin/refunds',
    method: 'get',
    params
  })
}

export const getRefundStats = () => {
  return request({
    url: '/admin/refunds/stats',
    method: 'get'
  })
}

export const approveRefund = (id, data) => {
  return request({
    url: `/admin/refunds/${id}/approve`,
    method: 'post',
    data
  })
}

export const rejectRefund = (id, data) => {
  return request({
    url: `/admin/refunds/${id}/reject`,
    method: 'post',
    data
  })
}

export const completeRefund = (id) => {
  return request({
    url: `/admin/refunds/${id}/complete`,
    method: 'post'
  })
}

export const batchApproveRefunds = (ids, data) => {
  return request({
    url: '/admin/refunds/batch-approve',
    method: 'post',
    data: { ids, ...data }
  })
}

export const batchRejectRefunds = (ids, data) => {
  return request({
    url: '/admin/refunds/batch-reject',
    method: 'post',
    data: { ids, ...data }
  })
}

export const exportRefunds = (params) => {
  return request({
    url: '/admin/refunds/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export const getOrderAnalytics = (params) => {
  return request({
    url: '/admin/analytics/orders',
    method: 'get',
    params
  })
}

export const getUserAnalytics = (params) => {
  return request({
    url: '/admin/analytics/users',
    method: 'get',
    params
  })
}

export const getProductAnalytics = (params) => {
  return request({
    url: '/admin/analytics/products',
    method: 'get',
    params
  })
}

// 导出报告
export const exportReport = (type, params) => {
  return request({
    url: `/admin/export/${type}`,
    method: 'get',
    params,
    responseType: 'blob'
  })
}