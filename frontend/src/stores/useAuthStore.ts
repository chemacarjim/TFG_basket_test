import { defineStore } from 'pinia'
import { adminLogin } from '../api/admin'
import { api } from '../api/client'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null as string | null,
    email: null as string | null,
    isSuperAdmin: false as boolean,
  }),
  getters: {
    roles(state): string[] {
      if(!state.token) return []
      try {
        const payload = JSON.parse(atob(state.token.split('.')[1]!))
        return payload.roles || []
      } catch {
        return []
      }
    },
    isSuperAdmin(): boolean {
      return this.roles.includes('SUPER_ADMIN')
    }
  },
  actions: {
    async login(email: string, password: string) {
      const { token, isSuperAdmin } = await adminLogin(email, password)
      localStorage.setItem('tfg_token', token)
      localStorage.setItem('tfg_email', email)
      localStorage.setItem('tfg_isSuperAdmin', JSON.stringify(isSuperAdmin))
      this.token = token
      this.email = email
      this.isSuperAdmin = isSuperAdmin
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },
    logout() {
      localStorage.removeItem('tfg_token')
      localStorage.removeItem('tfg_email')
      localStorage.removeItem('tfg_isSuperAdmin')
      this.token = null
      this.email = null
      delete api.defaults.headers.common['Authorization']
    },
    restoreFromStorage() {
      const token = localStorage.getItem('tfg_token')
      const email = localStorage.getItem('tfg_email')
      const isSuperAdmin = localStorage.getItem('tfg_isSuperAdmin')
      if (token) {
        this.token = token
        this.email = email
        this.isSuperAdmin = isSuperAdmin ? JSON.parse(isSuperAdmin) : false
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      }
    }
  }
})