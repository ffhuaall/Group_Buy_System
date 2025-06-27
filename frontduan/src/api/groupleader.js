import request from './request'

// 申请成为团长
export const applyGroupLeader = (data) => {
  return request({
    url: '/groupleader/apply',
    method: 'post',
    data
  })
}

// 获取团长列表
export const getGroupLeaderList = (params) => {
  return request({
    url: '/groupleader/list',
    method: 'get',
    params
  })
}

// 审批团长申请
export const approveGroupLeader = (id) => {
  return request({
    url: `/groupleader/approve/${id}`,
    method: 'put'
  })
}

// 拒绝团长申请
export const rejectGroupLeader = (id) => {
  return request({
    url: `/groupleader/reject/${id}`,
    method: 'put'
  })
}

// 获取附近团长
export const getNearbyGroupLeaders = (params) => {
  return request({
    url: '/groupleader/nearby',
    method: 'get',
    params
  })
}