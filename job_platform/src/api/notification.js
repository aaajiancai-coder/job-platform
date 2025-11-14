import request from '@/utils/request'

// 分页获取当前用户的通知列表
export function fetchNotificationList(userId, page = 1, pageSize = 10) {
  return request({
    url: '/notification/list',
    method: 'get',
    params: { userId, page, pageSize }
  })
}

// 标记已读
export function markNotificationRead(notificationId) {
  return request({
    url: `/notification/read/${notificationId}`,
    method: 'post'
  })
}

// 删除通知
export function deleteNotification(notificationId) {
  return request({
    url: `/notification/${notificationId}`,
    method: 'delete'
  })
}

// 获取未读消息数
export function getUnreadNotificationCount(userId) {
  return request({
    url: '/notification/unread-count',
    method: 'get',
    params: { userId }
  })
}
