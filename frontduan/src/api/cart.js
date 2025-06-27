import request from './request'

export const getUserCart = (userId) => {
  return request.get(`/cart/list/${userId}`)
}

export const addToCart = (data) => {
  return request.post('/cart/add', data)
}

export const updateCartItem = (data) => {
  return request.put('/cart/update', data)
}

export const removeFromCart = (userId, productId) => {
  return request.delete(`/cart/remove/${userId}/${productId}`)
}

export const clearCart = (userId) => {
  return request.delete('/cart/clear', { params: { userId } })
}