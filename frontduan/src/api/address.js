import request from './request'

// 获取用户地址列表
export const getUserAddresses = () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    throw new Error('用户未登录')
  }
  return request.get(`/address/user/${userId}`)
}

// 添加地址
export const addAddress = (addressData) => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    throw new Error('用户未登录')
  }
  
  // 数据验证
  if (!addressData.receiverName || !addressData.receiverPhone || !addressData.detailAddress) {
    throw new Error('请填写完整的地址信息')
  }
  
  return request.post('/address/add', { 
    ...addressData, 
    userId: parseInt(userId),
    isDefault: addressData.isDefault || false
  })
}

// 更新地址
export const updateAddress = (id, addressData) => {
  if (!id) {
    throw new Error('地址ID不能为空')
  }
  
  return request.put(`/address/update`, { 
    ...addressData, 
    id,
    isDefault: addressData.isDefault || false
  })
}

// 删除地址
export const deleteAddress = (id) => {
  if (!id) {
    throw new Error('地址ID不能为空')
  }
  return request.delete(`/address/delete/${id}`)
}

// 设置默认地址
export const setDefaultAddress = (id) => {
  if (!id) {
    throw new Error('地址ID不能为空')
  }
  return request.put(`/address/setDefault/${id}`)
}

// 根据ID获取地址详情
export const getAddressById = (id) => {
  if (!id) {
    throw new Error('地址ID不能为空')
  }
  return request.get(`/address/${id}`)
}

// 获取默认地址
export const getDefaultAddress = () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    throw new Error('用户未登录')
  }
  return request.get(`/address/default/${userId}`)
}