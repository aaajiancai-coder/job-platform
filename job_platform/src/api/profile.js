import request from '../utils/request'

// 获取企业信息
export function getCompanyProfile() {
  return request({
    url: '/company/profile',
    method: 'get'
  })
}

// 更新企业信息
export function updateCompanyProfile(data) {
  return request({
    url: '/company/profile',
    method: 'put',
    data
  })
}

// 获取学生信息
export function getStudentProfile() {
  return request({
    url: '/student/profile',
    method: 'get'
  })
}

// 更新学生信息
export function updateStudentProfile(data) {
  return request({
    url: '/student/profile',
    method: 'put',
    data
  })
} 