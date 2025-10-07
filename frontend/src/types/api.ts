export interface TestSummary { id: number; title: string; description: string }
export interface Question { id: number; prompt: string; possessionTime: number; imageUrl: string; orderIndex: number }
export interface TestDetail extends TestSummary { questions: Question[] }
export interface CreateSessionResponse { sessionId: number }
export interface FinishSessionResponse { score: number; total: number; durationMs: number }
