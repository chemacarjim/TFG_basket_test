export interface TestSummary { id: number; title: string; description: string | null }
export interface Question { id: number; prompt: string; possessionTime: number | null; imageUrl: string | null }
export interface TestDetail extends TestSummary { questions: Question[] }
export interface PublicAverageScore {
  averageScorePercent: number
}
export interface CreateSessionResponse { sessionId: number }
export interface FinishSessionItemResponse {
  questionId: number
  questionPrompt: string
  selectedValue: ChoiceValue
  correctValue: ChoiceValue | null
  isCorrect: boolean | null
  responseTimeMs: number | null
}
export interface FinishSessionResponse {
  score: number
  total: number
  durationMs: number | null
  finishedAt: string
  items: FinishSessionItemResponse[]
}

export type ChoiceValue = 'DRIBBLE' | 'PASS' | 'SHOOT'
export const choiceValues: ChoiceValue[] = ['DRIBBLE', 'PASS', 'SHOOT']

export interface AdminUser {
    id: number;
    email: string;
    isSuperAdmin: boolean;
    createdAt: string;
    lastLoginAt: string | null;
}