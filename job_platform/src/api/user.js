import request from '../utils/request'

// 用户登录
export function login(username, password) {
  return request.post('/auth/login', { username, password })
}

// 用户注册
export function register({ username, password, email, phone, role }) {
  return request.post('/auth/register', { username, password, email, phone, role })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request.get('/auth/me')
}

//上传头像
export function uploadAvatar(userId, avatar) {
  return request.post(`/user/${userId}/avatar`, avatar, { headers: { 'Content-Type': 'multipart/form-data' } })
}

//获取用户头像
export function getAvatar(userId) {
  return request.get(`/user/${userId}/avatar`, { responseType: 'blob' })
}
