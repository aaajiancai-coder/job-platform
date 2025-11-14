import request from '@/utils/request'

// 获取当前学生的投递记录分页列表
export function fetchApplicationList(studentId, page = 1, pageSize = 10) {
  return request({
    url: '/student/applications',
    method: 'get',
    params: { studentId, page, pageSize }
  })
}

// 撤回投递
export function withdrawApplication(applicationId) {
  return request({
    url: `/student/application/${applicationId}/withdraw`,
    method: 'post'
  })
}

// 投递简历
export function applyJob(data) {
  return request({
    url: '/student/apply',
    method: 'post',
    data
  })
} 