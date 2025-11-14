import request from '@/utils/request'

// 获取当前学生的简历分页列表
export function fetchResumeList(studentId, page = 1, pageSize = 10) {
  return request({
    url: '/student/resumes',
    method: 'get',
    params: { studentId, page, pageSize }
  })
}

// 新建简历
export function createResume(data) {
  return request({
    url: '/student/resume',
    method: 'post',
    data
  })
}

// 编辑简历
export function updateResume(data) {
  return request({
    url: '/student/resume',
    method: 'put',
    data
  })
}

// 删除简历
export function deleteResume(resumeId) {
  return request({
    url: `/student/resume/${resumeId}`,
    method: 'delete'
  })
}

// 企业端：分页获取本公司收到的简历列表
export function fetchCompanyResumes({ companyId, jobId, status, page = 1, pageSize = 10 }) {
  return request({
    url: '/company/resumes',
    method: 'get',
    params: { companyId, jobId, status, page, pageSize }
  })
}

// 企业端：更新简历状态
export function updateResumeStatus(applicationId, status) {
  return request({
    url: `/company/resume/${applicationId}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取投递详情
export function fetchApplicationDetail(applicationId) {
  return request({
    url: `/company/resume/${applicationId}/detail`,
    method: 'get'
  })
} 


// 发送offer
export function sendOfferToStudent(applicationId) {
  return request({
    url: `/company/resume/${applicationId}/send-offer`,
    method: 'post'
  })
} 