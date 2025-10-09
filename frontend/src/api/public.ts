import { api } from './client'
import type { TestSummary, TestDetail, CreateSessionResponse } from '../types/api'

export async function listActiveTests(): Promise<TestSummary[]> {
  const { data } = await api.get('/tests/active')
  return data
}

export async function getTestDetail(id: number): Promise<TestDetail> {
  const { data } = await api.get(`/tests/${id}`)
  return data
}

export async function createSession(testId: number, anonUserCode?: string): Promise<number> {
  const { data } = await api.post<CreateSessionResponse>(`/tests/${testId}/sessions`, { anonUserCode })
  return data.sessionId
}

export async function submitResponses(sessionId: number, items: Array<{questionId:number; selectedValue:'DRIBBLE'|'PASS'|'SHOOT'; responseTimeMs:number}>): Promise<void> {
  await api.post(`/sessions/${sessionId}/responses`, { items })
}

export async function finishSession(sessionId: number): Promise<{score:number; total:number; durationMs:number|null}> {
  const { data } = await api.post(`/sessions/${sessionId}/finish`)
  return data
}
