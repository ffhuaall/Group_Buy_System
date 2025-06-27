import request from './request'

export const getProductList = (params) => {
  return request.get('/product/list', { params })
}

export const getProductDetail = (id) => {
  return request.get(`/product/${id}`)
}

export const getHotProducts = (limit = 10) => {
  return request.get('/product/hot', { params: { limit } })
}

export const getRecommendProducts = (limit = 10) => {
  return request.get('/product/recommend', { params: { limit } })
}