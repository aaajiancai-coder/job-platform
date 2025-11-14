// 在 user.js 末尾添加
import request from '../utils/request'

// 获取热门职位推荐
export function getHotJobs() {
    return request.get('/common/hot-jobs')
}

// 获取知名企业
export function getHotCompanies() {
    return request.get('/common/hot-companies')
}