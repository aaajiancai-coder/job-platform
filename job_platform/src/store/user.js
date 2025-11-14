import { defineStore } from 'pinia'
import { login as loginApi, getCurrentUser } from '../api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: sessionStorage.getItem('token') || '',
    user: JSON.parse(sessionStorage.getItem('user')) || null // 持久化user
  }),
  actions: {
    async login(username, password) {
      const data = await loginApi(username, password)
      this.token = data.token
      this.user = data.user // user对象已包含verified/status字段
      sessionStorage.setItem('token', this.token)
      sessionStorage.setItem('user', JSON.stringify(this.user)) // 持久化user
      return true
    },
    async fetchUser() {
      if (!this.token) return
      const data = await getCurrentUser()
      this.user = data
      sessionStorage.setItem('user', JSON.stringify(this.user)) // 持久化user
    },
    logout() {
      this.token = ''
      this.user = null
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('user')
    },
    getRole() {
      return this.user?.role || null
    }
  }
})