import { api } from './client'
import type { TestSummary, TestDetail, CreateSessionResponse } from '../types/api'

export async function listActiveTests(): Promise<TestSummary[]> {
  const { data } = await api.get('/tests/active')
  return data
}

export async function getTestDetail(id: number) {
  const { data } = await api.get(`/tests/${id}`)
  return data
}

export async function createSession(testId: number, payload: {
  name: string;
  surname: string;
  birthDate?: string;
  country?: string;
  gender?: string;
  dominantHand?: string;
  position?: string;
  inasidnr?: string;
  event?: string;
  instructor?: string;
}) {
  const { data } = await api.post(`/tests/${testId}/sessions`, payload)
  return data
}

export async function submitResponses(sessionId: number, body: any) {
  const { data } = await api.post(`/sessions/${sessionId}/responses`, body)
  return data
}

export async function finishSession(sessionId: number) {
  const { data } = await api.post(`/sessions/${sessionId}/finish`)
  return data
}
