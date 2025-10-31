export interface TestSummary { id: number; title: string; description: string | null }
export interface Question { id: number; prompt: string; possessionTime: number | null; imageUrl: string | null }
export interface TestDetail extends TestSummary { questions: Question[] }
export interface CreateSessionResponse { sessionId: number }
export interface FinishSessionResponse { score: number; total: number; durationMs: number | null }

export type ChoiceValue = 'DRIBBLE' | 'PASS' | 'SHOOT'

export const choiceValues: ChoiceValue[] = ['DRIBBLE', 'PASS', 'SHOOT']