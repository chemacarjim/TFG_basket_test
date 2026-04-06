<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTestDetail } from '../api/public'
import { useSessionStore } from '../stores/useSessionStore'
import QuestionView from '../components/QuestionView.vue'
import type { ChoiceValue, FinishSessionItemResponse } from '../types/api'

// Leer parámetro de ruta
const route = useRoute()
const router = useRouter()
const testId = Number(route.params.id)

// Store para gestionar la sesión
const sessionStore = useSessionStore()

// Estado para el test
const test = ref<any>(null)
const loading = ref(true)
const loadError = ref<string | null>(null)
const currentSessionId = ref<number | null>(null)

// Estado de la sesión en la UI
const session = ref<{ finished?: boolean }>({})

// Datos del formulario previo
const pre = ref({
  name: '',
  surname: '',
  birthDate: '',
  country: '',
  gender: '',
  dominantHand: '',
  position: '',
  inasidnr: '',
  event: '',
  instructor: ''
})
const errors = ref<Record<string, boolean>>({})
const formError = ref<string | null>(null)

// Información de la sesión finalizada
const finishInfo = ref<{
  finishedAt?: string
  score?: number | null
  total?: number | null
  durationMs?: number | null
  items?: FinishSessionItemResponse[]
}>({})
const localResponses = ref<Array<{ questionId: number; selectedValue: ChoiceValue; responseTimeMs: number }>>([])

// Control de la pregunta actual y número de respuestas
const currentIndex = ref<number>(0)
const responsesCount = ref<number>(0)
const isSubmittingAnswer = ref(false)
const isFinishing = ref(false)

const questionsCount = computed(() => test.value?.questions?.length ?? 0)
const currentQuestion = computed(() => {
  if (!test.value?.questions?.length) return null
  return test.value.questions[currentIndex.value] ?? null
})
const progressPercent = computed(() => {
  if (!questionsCount.value) return 0
  return Math.round(((currentIndex.value + 1) / questionsCount.value) * 100)
})
const responseByQuestionId = computed(() => {
  const map = new Map<number, { selectedValue: ChoiceValue; responseTimeMs: number }>()
  for (const item of localResponses.value) {
    map.set(item.questionId, { selectedValue: item.selectedValue, responseTimeMs: item.responseTimeMs })
  }
  return map
})
const totalTimeMs = computed(() => {
  if (typeof finishInfo.value.durationMs === 'number' && finishInfo.value.durationMs >= 0) {
    return finishInfo.value.durationMs
  }
  return localResponses.value.reduce((sum, item) => sum + item.responseTimeMs, 0)
})
const totalTimeLabel = computed(() => {
  const ms = totalTimeMs.value
  const totalSeconds = Math.max(0, Math.round(ms / 1000))
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  return `${minutes}m ${seconds.toString().padStart(2, '0')}s`
})
const resultRows = computed(() => {
  const backendItems = finishInfo.value.items ?? []
  if (backendItems.length > 0) {
    return backendItems.map((item, index) => ({
      id: item.questionId,
      index: index + 1,
      prompt: item.questionPrompt || `Pregunta ${index + 1}`,
      selectedValue: item.selectedValue ?? null,
      correctValue: item.correctValue ?? null,
      isCorrect: item.isCorrect ?? null,
    }))
  }

  // Fallback visual si no llegan ítems desde backend.
  const questions = test.value?.questions ?? []
  return questions.map((q: any, index: number) => {
    const userResponse = responseByQuestionId.value.get(q.id)
    return {
      id: q.id,
      index: index + 1,
      prompt: q.prompt || `Pregunta ${index + 1}`,
      selectedValue: userResponse?.selectedValue ?? null,
      correctValue: null,
      isCorrect: null,
    }
  })
})

function choiceLabel(value: ChoiceValue | string | null | undefined) {
  if (!value) return 'No disponible'
  if (value === 'DRIBBLE') return 'Botar'
  if (value === 'PASS') return 'Pasar'
  if (value === 'SHOOT') return 'Tirar'
  return String(value)
}

function outcomeLabel(isCorrect: boolean | null) {
  if (isCorrect === true) return 'Acierto'
  if (isCorrect === false) return 'Fallo'
  return 'No disponible'
}

function outcomeEmoji(isCorrect: boolean | null) {
  if (isCorrect === true) return '🟢'
  if (isCorrect === false) return '🔴'
  return '⚪'
}

// Validación básica del formulario
function validatePre() {
  errors.value = {}
  let ok = true
  if (!pre.value.name.trim()) { errors.value.name = true; ok = false }
  if (!pre.value.surname.trim()) { errors.value.surname = true; ok = false }
  if (!ok) formError.value = 'Existen datos obligatorios vacíos'
  return ok
}

// Crear sesión y preparar índices
async function startSession() {
  try {
    formError.value = null
    if (!validatePre()) return
    localResponses.value = []
    finishInfo.value = {}
    await sessionStore.start(testId, { ...pre.value })
    currentSessionId.value = sessionStore.sessionId
    session.value.finished = false
    currentIndex.value = 0
    responsesCount.value = 0
  } catch (e: any) {
    const msg = e?.response?.data?.message || e?.message || 'Error creando la sesión'
    formError.value = msg
    const fields = e?.response?.data?.fields
    if (fields) {
      Object.keys(fields).forEach(k => (errors.value[k] = true))
    }
  }
}

// Recibir la respuesta de QuestionView
async function onAnswered(payload: { questionId: number; selectedValue: ChoiceValue; responseTimeMs: number }) {
  if (isSubmittingAnswer.value || isFinishing.value || session.value.finished) return
  if (!questionsCount.value) return

  isSubmittingAnswer.value = true
  localResponses.value.push(payload)
  sessionStore.addLocalResponse(payload)
  responsesCount.value++
  if (responsesCount.value < questionsCount.value) {
    currentIndex.value++
    await nextTick()
    isSubmittingAnswer.value = false
  } else {
    await finishSessionWithStore()
    isSubmittingAnswer.value = false
  }
}

// Finalizar sesión y mostrar datos
async function finishSessionWithStore() {
  if (isFinishing.value) return
  isFinishing.value = true
  try {
    const res = await sessionStore.finish()
    currentSessionId.value = sessionStore.sessionId
    session.value.finished = true
    finishInfo.value = {
      finishedAt: res.finishedAt,
      score: res.score ?? null,
      total: res.total ?? null,
      durationMs: res.durationMs ?? null,
      items: res.items ?? [],
    }
  } catch (e: any) {
    formError.value = e?.response?.data?.message || e?.message || 'Error finalizando la sesión'
  } finally {
    isFinishing.value = false
  }
}

// Descargar el PDF de la sesión finalizada
function downloadPdf() {
  const sid = currentSessionId.value || sessionStore.sessionId
  if(!sid) return
  const base = import.meta.env.VITE_API_URL
  window.open(`${base}/sessions/${sid}/report.pdf`, '_blank', 'noopener')
}

// Cargar los detalles del test al montar el componente
onMounted(async () => {
  try {
    test.value = await getTestDetail(testId)
    if (sessionStore.sessionId) {
      currentSessionId.value = sessionStore.sessionId
      session.value.finished = false
    }
  } catch (e: any) {
    loadError.value = e?.message || 'No se pudo cargar el test'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-full bg-gray-900 text-gray-100 px-6 pt-10 pb-12">
    <div class="w-[90%] mx-auto space-y-6">
      <!-- Cabecera -->
      <div class="bg-gray-800 rounded-2xl border border-gray-700 p-6 shadow-lg">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h1 class="text-3xl font-bold text-white">Test {{ test?.title ?? testId }}</h1>
            <p class="text-gray-300 mt-1">Runner de evaluación</p>
          </div>
          <button class="px-4 py-2 rounded-xl bg-gray-700 hover:bg-gray-600 text-white" @click="router.push('/tests')">
            Volver a tests
          </button>
        </div>
        <p v-if="loadError" class="text-red-400 mt-3">{{ loadError }}</p>
      </div>
      <hr v-if="!loading && sessionStore.sessionId && !session.finished" class="border-0 border-t border-gray-700/80" />

      <!-- Estado de carga -->
      <div v-if="loading" class="text-gray-300 text-center py-10">Cargando…</div>

      <!-- Formulario previo -->
      <div v-if="!loading && !sessionStore.sessionId" class="space-y-4 p-6 bg-white rounded-2xl shadow-xl">
        <h2 class="text-xl font-semibold text-gray-900">Datos del participante</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div>
            <label class="text-sm text-gray-700">Nombre *</label>
            <input v-model="pre.name" :class="['w-full p-2 rounded border', errors.name && 'border-red-500']" placeholder="Nombre" />
          </div>
          <div>
            <label class="text-sm text-gray-700">Apellidos *</label>
            <input v-model="pre.surname" :class="['w-full p-2 rounded border', errors.surname && 'border-red-500']" placeholder="Apellidos" />
          </div>
          <div>
            <label class="text-sm text-gray-700">Fecha nacimiento</label>
            <input type="date" v-model="pre.birthDate" class="w-full p-2 rounded border" />
          </div>
          <div>
            <label class="text-sm text-gray-700">País</label>
            <input v-model="pre.country" class="w-full p-2 rounded border" placeholder="España" />
          </div>
          <div>
            <label class="text-sm text-gray-700">Género</label>
            <select v-model="pre.gender" class="w-full p-2 rounded border">
              <option value="">Seleccione género</option>
              <option value="FEMENINO">Femenino</option>
              <option value="MASCULINO">Masculino</option>
              <option value="OTRO">Otro</option>
            </select>
          </div>
          <div>
            <label class="text-sm text-gray-700">Mano dominante</label>
            <select v-model="pre.dominantHand" class="w-full p-2 rounded border">
              <option value="">Seleccione mano dominante</option>
              <option value="DERECHA">Derecha</option>
              <option value="IZQUIERDA">Izquierda</option>
            </select>
          </div>
          <div>
            <label class="text-sm text-gray-700">Posición</label>
            <select v-model="pre.position" class="w-full p-2 rounded border">
              <option value="">Seleccione posición</option>
              <option value="BASE">Base</option>
              <option value="ESCOLTA">Escolta</option>
              <option value="ALERO">Alero</option>
              <option value="ALA-PIVOT">Ala-Pivot</option>
              <option value="PIVOT">Pivot</option>
            </select>
          </div>
          <div>
            <label class="text-sm text-gray-700">INASIDNR</label>
            <input v-model="pre.inasidnr" class="w-full p-2 rounded border" />
          </div>
          <div>
            <label class="text-sm text-gray-700">Evento</label>
            <input v-model="pre.event" class="w-full p-2 rounded border" placeholder="Torneo/Competición" />
          </div>
          <div>
            <label class="text-sm text-gray-700">Instructor</label>
            <input v-model="pre.instructor" class="w-full p-2 rounded border" placeholder="Nombre del entrenador" />
          </div>
        </div>
        <p v-if="formError" class="text-red-600 text-sm">{{ formError }}</p>
        <div class="flex gap-3">
          <button class="px-4 py-2 rounded-xl shadow bg-gray-100" @click="router.back()">Cancelar</button>
          <button class="px-4 py-2 rounded-xl shadow bg-indigo-600 text-white hover:bg-indigo-700" @click="startSession">Comenzar test</button>
        </div>
      </div>

      <!-- Flujo de preguntas -->
      <div v-if="!loading && sessionStore.sessionId && !session.finished" class="w-[70%] mx-auto space-y-4 p-6 bg-white rounded-2xl shadow-xl">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <h2 class="text-xl font-semibold text-gray-900">Sesión #{{ sessionStore.sessionId }}</h2>
          <p class="text-gray-600 font-medium">Pregunta {{ currentIndex + 1 }} de {{ questionsCount }}</p>
        </div>
        <div class="h-2 w-full bg-gray-200 rounded-full overflow-hidden">
          <div class="h-full bg-indigo-600 transition-all duration-300" :style="{ width: `${progressPercent}%` }"></div>
        </div>
        <QuestionView v-if="currentQuestion" :question="currentQuestion" @answered="onAnswered" />
        <p v-if="isSubmittingAnswer || isFinishing" class="text-sm text-gray-500 text-center">Guardando respuesta…</p>
        <p v-if="currentQuestion" class="text-gray-500 text-center">Avance: {{ progressPercent }}%</p>
        <p v-else class="text-red-600 text-sm text-center">Este test no tiene preguntas disponibles.</p>
      </div>

      <!-- Sesión finalizada -->
      <div v-if="!loading && sessionStore.sessionId && session.finished" class="space-y-4 p-6 bg-white rounded-2xl shadow-xl">
        <div class="flex flex-wrap items-start justify-between gap-3">
          <div>
            <p class="text-green-700 font-semibold text-lg">
              ¡Sesión finalizada!
              <span v-if="finishInfo.finishedAt" class="ml-2 text-gray-600 text-sm">
                ({{ new Date(finishInfo.finishedAt).toLocaleString('es-ES') }})
              </span>
            </p>
            <p class="text-gray-700 mt-1">
              Nota general: <strong>{{ finishInfo.score ?? '—' }} / {{ finishInfo.total ?? '—' }}</strong>
            </p>
            <p class="text-gray-700">Tiempo total: <strong>{{ totalTimeLabel }}</strong></p>
          </div>
          <div class="flex gap-3">
            <button class="px-4 py-2 rounded-xl shadow bg-indigo-600 text-white hover:bg-indigo-700" @click="downloadPdf">Descargar PDF</button>
          </div>
        </div>

        <hr class="border-0 border-t border-gray-300/80" />

        <div class="space-y-3">
          <article
            v-for="row in resultRows"
            :key="row.id"
            class="p-4 rounded-xl border border-gray-200 bg-gray-50"
          >
            <div class="flex flex-wrap items-start justify-between gap-2">
              <h3 class="font-semibold text-gray-900">Pregunta {{ row.index }}</h3>
            </div>
            <p class="text-gray-700 mt-2">{{ row.prompt }}</p>
            <div class="mt-3 text-sm space-y-1">
              <p class="text-gray-700">
                <span class="text-gray-500">Respuesta del usuario:</span>
                <span class="font-medium text-gray-900 ml-1">{{ choiceLabel(row.selectedValue) }}</span>
              </p>
              <p class="text-gray-700">
                <span class="text-gray-500">Respuesta correcta:</span>
                <span class="font-medium text-gray-900 ml-1">{{ choiceLabel(row.correctValue) }}</span>
              </p>
              <p class="text-gray-700">
                <span class="text-gray-500">Acierto/Fallo:</span>
                <span class="font-medium text-gray-900 ml-1">{{ outcomeLabel(row.isCorrect) }}</span>
                <span class="ml-1">{{ outcomeEmoji(row.isCorrect) }}</span>
              </p>
            </div>
          </article>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
