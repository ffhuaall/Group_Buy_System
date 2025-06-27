import request from './request'

// 添加供应商
export const addSupplier = (data) => {
  return request({
    url: '/supplier/add',
    method: 'post',
    data
  })
}

// 获取供应商列表
export const getSupplierList = (params) => {
  return request({
    url: '/supplier/list',
    method: 'get',
    params
  })
}

// 更新供应商信息
export const updateSupplier = (data) => {
  return request({
    url: '/supplier/update',
    method: 'put',
    data
  })
}

// 更新供应商状态
export const updateSupplierStatus = (id, status) => {
  return request({
    url: `/supplier/status/${id}`,
    method: 'put',
    params: { status }
  })
}

// 获取供应商信息
export const getSupplierInfo = (id) => {
  return request({
    url: `/supplier/${id}`,
    method: 'get'
  })
}

// 获取当前登录供应商信息
export const getCurrentSupplierInfo = () => {
  return request({
    url: '/supplier/info',
    method: 'get'
  })
}