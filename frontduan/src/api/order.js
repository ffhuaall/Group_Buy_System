import request from './request'

// 获取订单列表 - 修复路径，添加userId参数
export const getOrders = (userId, params = {}) => {
  return request({
    url: `/order/list/${userId}`,
    method: 'get',
    params
  })
}

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 支付订单
export const payOrder = (orderId, paymentData = {}) => {
  return request({
    url: `/order/pay/${orderId}`,
    method: 'post',
    params: { paymentMethod: paymentData.paymentMethod }
  })
}

// 获取用户订单列表（简化版本）
export const getUserOrders = (status = '') => {
  const params = status ? { status } : {}
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 获取订单详情（别名）
export const getOrderById = (orderId) => {
  return request({
    url: `/order/${orderId}`,
    method: 'get'
  })
}

// 确认收货
export const confirmOrder = (orderNo) => {
  return request({
    url: `/order/confirm/${orderNo}`,
    method: 'put'
  })
}

// 取消订单
export const cancelOrder = (orderId) => {
  return request({
    url: `/order/${orderId}/cancel`,
    method: 'post'
  })
}

// 获取订单详情
export const getOrderDetail = (orderNo) => {
  return request({
    url: `/order/detail/${orderNo}`,
    method: 'get'
  })
}