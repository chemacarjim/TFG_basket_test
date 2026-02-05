import type { ChoiceValue, AdminUser } from '../types/api';
import { api } from './client'

export interface LoginResponse {
  token: string;
  isSuperAdmin: boolean;
}
export async function adminLogin(email: string, password: string): Promise<LoginResponse> {
  const { data } = await api.post('/admin/auth/login', { email, password })
  return data as LoginResponse
}

export async function adminListTests(): Promise<Array<{id:number; title:string; description:string|null; isActive:boolean}>> {
  const { data } = await api.get('/admin/tests')
  return data
}

export async function adminCreateTest(dto: {title:string; description?:string; isActive?:boolean}) {
  const { data } = await api.post('/admin/tests', dto)
  return data
}

export async function adminUpdateTest(id:number, dto: {title?:string; description?:string; isActive?:boolean}) {
  const { data } = await api.put(`/admin/tests/${id}`, dto)
  return data
}

export async function adminDeleteTest(id:number) {
  await api.delete(`/admin/tests/${id}`)
}

export async function adminListQuestions(testId:number) {
  const { data } = await api.get(`/admin/tests/${testId}/questions`)
  return data
}

export async function adminCreateQuestion(testId:number, dto: {
  prompt:string
  possessionTime?:number
  imageUrl:string
  correctValue:ChoiceValue
}) {
  const { data } = await api.post(`/admin/tests/${testId}/questions`, dto)
  return data
}

export async function adminUpdateQuestion(testId:number, questionId:number, dto: {prompt?:string; possessionTime?:number; imageUrl?:string; correctValue:ChoiceValue }) {
  const { data } = await api.put(`/admin/tests/${testId}/questions/${questionId}`, dto)
  return data
}

export async function adminDeleteQuestion(testId:number, questionId:number) {
  await api.delete(`/admin/tests/${testId}/questions/${questionId}`)
}

export async function adminUploadImage(file: File): Promise<string> {
  const form = new FormData()
  form.append('file', file)
  const { data } = await api.post('/admin/assets/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
  return data // url
}

export async function adminListSessions(testId:number, limit:number, offset:number) {
  const { data } = await api.get(`/admin/tests/${testId}/sessions`, { params: { limit, offset } })
  return data as {
    items: Array<{sessionId:number; name:string; surname:string; finishedAt:string|null; score:number; total:number}>,
    nextOffset: number,
    hasMore: boolean
  }
}

export async function adminDownloadSessionReport(
  testId:number,
  sessionId:number
): Promise<Blob> {
  const { data } = await api.get(
    `/admin/tests/${testId}/sessions/${sessionId}/report.pdf`,
    { responseType: 'blob' }
  )
  return data
}

export async function adminListUsers(): Promise<AdminUser[]> {
  const { data } = await api.get('/admin/users')
  return data as AdminUser[]
}

export async function adminCreateUser(dto: { email: string; password: string; isSuperAdmin: boolean }): Promise<AdminUser> {
  const { data } = await api.post('/admin/users', dto)
  return data as AdminUser
}

export async function adminUpdateUser(id: number, dto: { email?: string; password?: string; isSuperAdmin?: boolean }): Promise<AdminUser> {
  const { data } = await api.put(`/admin/users/${id}`, dto)
  return data as AdminUser
}

export async function adminDeleteUser(id: number): Promise<void> {
  await api.delete(`/admin/users/${id}`)
}