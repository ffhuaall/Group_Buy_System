import request from './request'

export const getCategories = () => {
  return request.get('/category/list')
}
