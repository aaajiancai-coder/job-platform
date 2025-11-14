import request from '../utils/request'

export function fetchJobs(params) {
  return request({
    url: '/common/jobs',
    method: 'get',
    params
  })
}

export function fetchJobDetail(id) {
  return request({
    url: `/jobs/${id}`,
    method: 'get'
  })
}

// 企业端：分页获取本公司职位列表
export function fetchCompanyJobs(companyId, page = 1, pageSize = 10) {
  return request({
    url: '/company/jobs',
    method: 'get',
    params: { companyId, page, pageSize }
  })
}

// 企业端：新增职位
export function createJob(data) {
  return request({
    url: '/company/jobs',
    method: 'post',
    data
  })
}

// 企业端：编辑职位
export function updateJob(id, data) {
  return request({
    url: `/company/jobs/${id}`,
    method: 'put',
    data
  })
}

// 企业端：删除职位
export function deleteJob(id) {
  return request({
    url: `/company/jobs/${id}`,
    method: 'delete'
  })
}

// 企业端：上下架职位
export function changeJobStatus(id, status) {
  return request({
    url: `/company/jobs/${id}/status`,
    method: 'put',
    data: { status }
  })
} 