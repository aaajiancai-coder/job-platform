import request from '../utils/request'

export function fetchCompanies(params) {
  return request({
    url: '/common/companies',
    method: 'get',
    params
  })
}

export function fetchCompanyDetail(id) {
  return request({
    url: `/company/${id}`,
    method: 'get'
  })
}

export function fetchCompanyDashboard(companyId) {
  return request({
    url: '/company/dashboard',
    method: 'get',
    params: { companyId }
  })
}

export function updateCompanyDetail(id, data) {
  return request({
    url: `/company/${id}`,
    method: 'put',
    data
  })
} 