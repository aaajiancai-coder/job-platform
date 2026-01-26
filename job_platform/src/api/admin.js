import request from '../utils/request'

// 获取用户列表
export function fetchUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}


// 更新用户状态
export function updateUserStatus(userId, status) {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'put',
    params: { status }
  })
}

// 删除用户
export function deleteUser(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'delete'
  })
}

// 重置用户密码
export function resetUserPassword(userId) {
  return request({
    url: `/admin/users/${userId}/reset-password`,
    method: 'post'
  })
}

// 获取待审核的企业列表
export function getCompanyAuditList(params) {
  return request({
    url: '/admin/company/audit/list',
    method: 'get',
    params
  })
}

// 审核企业
export function auditCompany(companyId, status, reason) {
  return request({
    url: `/admin/company/audit/${companyId}`,
    method: 'post',
    data: {
      status,
      reason
    }
  })
}

// 获取企业详情
export function getCompanyDetail(companyId) {
  return request({
    url: `/admin/company/${companyId}/detail`,
    method: 'get'
  })
}

// 分页获取企业列表（支持名称模糊、状态筛选，未认证排前）
export function fetchCompanyAuditList(params) {
  return request({
    url: '/admin/companies',
    method: 'get',
    params
  })
}

// 修改企业认证状态
export function changeCompanyVerifyStatus(companyId, status) {
  return request({
    url: `/admin/companies/${companyId}/verify`,
    method: 'put',
    params: { status }
  })
}

// 获取企业+用户详情
export function fetchCompanyDetailWithUser(companyId) {
  return request({
    url: `/admin/companies/${companyId}/detail`,
    method: 'get'
  })
}

// 用户增长趋势
export function fetchUserGrowth() {
  return request({
    url: '/admin/stats/user-growth',
    method: 'get'
  })
}

// 职位投递趋势
export function fetchJobApplicationTrend() {
  return request({
    url: '/admin/stats/job-application',
    method: 'get'
  })
}

// 企业入驻统计
export function fetchCompanyPie() {
  return request({
    url: '/admin/stats/company-pie',
    method: 'get'
  })
}

// 简历投递趋势
export function fetchResumeTrend() {
  return request({
    url: '/admin/stats/resume-trend',
    method: 'get'
  })
}
