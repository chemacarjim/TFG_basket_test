import { defineStore } from 'pinia'
import type { ChoiceValue } from '../types/api'
import { createSession, submitResponses, finishSession } from '../api/public'

type ResponseItem = { questionId: number; selectedValue: ChoiceValue; responseTimeMs: number }

export const useSessionStore = defineStore('session', {
  state: () => ({
    sessionId: null as number | null,
    responses: [] as ResponseItem[],
    startedAt: null as number | null, // ms epoch
  }),
  actions: {
    async start(testId: number, anonUserCode?: string) {
      this.sessionId = await createSession(testId, anonUserCode)
      this.responses = []
      this.startedAt = Date.now()
    },
    addLocalResponse(item: ResponseItem) {
      this.responses.push(item)
    },
    async flushBatch() {
      if (!this.sessionId || this.responses.length === 0) return
      await submitResponses(this.sessionId, this.responses)
      this.responses = []
    },
    async finish() {
      await this.flushBatch()
      if (!this.sessionId) throw new Error('No session')
      return await finishSession(this.sessionId)
    },
  },
})
