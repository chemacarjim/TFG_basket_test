<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTestDetail } from '../api/public'
import { useSessionStore } from '../stores/useSessionStore'
import QuestionView from '../components/QuestionView.vue'
import type { ChoiceValue } from '../types/api'

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
const finishInfo = ref<{ finishedAt?: string; score?: number | null; total?: number | null }>({})

// Control de la pregunta actual y número de respuestas
const currentIndex = ref<number>(0)
const responsesCount = ref<number>(0)

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
function onAnswered(payload: { questionId: number; selectedValue: ChoiceValue; responseTimeMs: number }) {
  sessionStore.addLocalResponse(payload)
  responsesCount.value++
  if (test.value && responsesCount.value < test.value.questions.length) {
    currentIndex.value++
  } else {
    finishSessionWithStore()
  }
}

// Finalizar sesión y mostrar datos
async function finishSessionWithStore() {
  try {
    const res = await sessionStore.finish()
    currentSessionId.value = sessionStore.sessionId
    session.value.finished = true
    finishInfo.value = {
      finishedAt: (res as any).finishedAt,
      score: (res as any).score ?? null,
      total: (res as any).total ?? null
    }
  } catch (e: any) {
    formError.value = e?.response?.data?.message || e?.message || 'Error finalizando la sesión'
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
  } catch (e: any) {
    loadError.value = e?.message || 'No se pudo cargar el test'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-3xl mx-auto p-6 space-y-6">
    <!-- Cabecera -->
    <div>
      <h1 class="text-2xl font-bold">Test {{ test?.title ?? testId }}</h1>
      <p v-if="loadError" class="text-red-600 mt-2">{{ loadError }}</p>
    </div>

    <!-- Estado de carga -->
    <div v-if="loading" class="text-gray-600">Cargando…</div>

    <!-- Formulario previo -->
    <div v-if="!loading && !sessionStore.sessionId" class="space-y-3 p-4 bg-white rounded-xl shadow">
      <h2 class="text-lg font-semibold">Datos del participante</h2>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="text-sm">Nombre *</label>
          <input v-model="pre.name" :class="['w-full p-2 rounded border', errors.name && 'border-red-500']" placeholder="Nombre" />
        </div>
        <div>
          <label class="text-sm">Apellidos *</label>
          <input v-model="pre.surname" :class="['w-full p-2 rounded border', errors.surname && 'border-red-500']" placeholder="Apellidos" />
        </div>
        <div>
          <label class="text-sm">Fecha nacimiento</label>
          <input type="date" v-model="pre.birthDate" class="w-full p-2 rounded border" />
        </div>
        <div>
          <label class="text-sm">País</label>
          <input v-model="pre.country" class="w-full p-2 rounded border" placeholder="España" />
        </div>
        <div>
          <label class="text-sm">Género</label>
          <select v-model="pre.gender" class="w-full p-2 rounded border">
            <option value="">Seleccione género</option>
            <option value="FEMENINO">Femenino</option>
            <option value="MASCULINO">Masculino</option>
            <option value="OTRO">Otro</option>
          </select>
        </div>
        <div>
          <label class="text-sm">Mano dominante</label>
          <select v-model="pre.dominantHand" class="w-full p-2 rounded border">
            <option value="">Seleccione mano dominante</option>
            <option value="DERECHA">Derecha</option>
            <option value="IZQUIERDA">Izquierda</option>
          </select>
        </div>
        <div>
          <label class="text-sm">Posición</label>
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
          <label class="text-sm">INASIDNR</label>
          <input v-model="pre.inasidnr" class="w-full p-2 rounded border" />
        </div>
        <div>
          <label class="text-sm">Evento</label>
          <input v-model="pre.event" class="w-full p-2 rounded border" placeholder="Torneo/Competición" />
        </div>
        <div>
          <label class="text-sm">Instructor</label>
          <input v-model="pre.instructor" class="w-full p-2 rounded border" placeholder="Nombre del entrenador" />
        </div>
      </div>
      <p v-if="formError" class="text-red-600 text-sm">{{ formError }}</p>
      <div class="flex gap-3">
        <button class="px-4 py-2 rounded-xl shadow bg-gray-100" @click="router.back()">Cancelar</button>
        <button class="px-4 py-2 rounded-xl shadow" @click="startSession">Comenzar test</button>
      </div>
    </div>

    <!-- Flujo de preguntas -->
    <div v-if="!loading && sessionStore.sessionId && !session.finished" class="space-y-4 p-4 bg-white rounded-xl shadow">
      <h2 class="text-lg font-semibold">Sesión #{{ sessionStore.sessionId }}</h2>
      <QuestionView :question="test.questions[currentIndex]" @answered="onAnswered" />
      <p class="text-gray-500">Pregunta {{ currentIndex + 1 }} de {{ test.questions.length }}</p>
      
    </div>

    <!-- Sesión finalizada -->
    <div v-if="!loading && sessionStore.sessionId && session.finished" class="space-y-2 p-4 bg-white rounded-xl shadow">
      <p class="text-green-700">
        ¡Sesión finalizada!
        <span v-if="finishInfo.finishedAt" class="ml-2 text-gray-600">
          ({{ new Date(finishInfo.finishedAt).toLocaleString('es-ES') }})
        </span>
      </p>
      <p>Resultado: {{ finishInfo.score ?? '—' }} / {{ finishInfo.total ?? '—' }}</p>
      <div class="flex gap-3">
        <button class="px-4 py-2 rounded-xl shadow" @click="downloadPdf">Descargar PDF</button>
        <button class="px-4 py-2 rounded-xl shadow bg-gray-100" @click="router.push('/')">Volver al inicio</button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
