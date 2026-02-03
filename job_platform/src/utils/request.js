import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 使用相对路径，通过vite代理转发
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是二进制数据，直接返回
    if (response.data instanceof Blob) {
      return response.data
    }
    if (response.status === 200 && response.data.code === 200) {
      return response.data.data
    } else {
      ElMessage.error(response.data.message || '请求失败')
      return Promise.reject(response.data)
    }
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 401:
          ElMessage.error('未登录或登录已过期')
          sessionStorage.removeItem('token')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default service
