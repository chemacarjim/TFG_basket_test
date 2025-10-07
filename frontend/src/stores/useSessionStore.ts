import { defineStore } from 'pinia'
export const useSessionStore = defineStore('session', {
  state: () => ({ sessionId: null, responses: [] }),
})