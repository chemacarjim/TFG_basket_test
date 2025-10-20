import { defineStore } from 'pinia'
import { adminLogin } from '../api/admin'
import { api } from '../api/client'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null as string | null,
    email: null as string | null,
  }),
  actions: {
    async login(email: string, password: string) {
      const token = await adminLogin(email, password)
      localStorage.setItem('tfg_token', token)
      localStorage.setItem('tfg_email', email)
      this.token = token
      this.email = email
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },
    logout() {
      localStorage.removeItem('tfg_token')
      localStorage.removeItem('tfg_email')
      this.token = null
      this.email = null
      delete api.defaults.headers.common['Authorization']
    },
    restoreFromStorage() {
      const token = localStorage.getItem('tfg_token')
      const email = localStorage.getItem('tfg_email')
      if (token) {
        this.token = token
        this.email = email
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      }
    }
  }
})