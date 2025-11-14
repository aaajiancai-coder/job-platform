import request from '@/utils/request'

export function fetchStudentDashboard(studentId) {
  return request({
    url: '/student/dashboard',
    method: 'get',
    params: { studentId }
  })
}

export function fetchStudentDetail(studentId) {
  return request({
    url: `/student/${studentId}`,
    method: 'get'
  })
}

export function updateStudentDetail(studentId, data) {
  return request({
    url: `/student/${studentId}`,
    method: 'put',
    data
  })
} 