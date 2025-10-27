<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getTestDetail, createSession, finishSession } from '../api/public'

// --- Parámetros de ruta
const route = useRoute()
const testId = Number(route.params.id)

// --- Estado principal
const test = ref<any>(null)
const loading = ref(true)
const loadError = ref<string | null>(null)

// --- Sesión creada en backend
const session = ref<{ sessionId?: number; finished?: boolean }>({})

// --- Formulario previo (datos del participante)
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

function validatePre() {
  errors.value = {}
  let ok = true
  if (!pre.value.name.trim()) { errors.value.name = true; ok = false }
  if (!pre.value.surname.trim()) { errors.value.surname = true; ok = false }
  if (!ok) formError.value = 'Existen datos obligatorios vacíos'
  return ok
}

async function startSession() {
  try {
    formError.value = null
    if (!validatePre()) return
    const payload = { ...pre.value }
    const res = await createSession(testId, payload) // { sessionId }
    session.value.sessionId = res.sessionId
  } catch (e: any) {
    // Si el backend devuelve 400 con VALIDATION_ERROR, mostramos el mensaje
    const msg = e?.response?.data?.message || e?.message || 'Error creando la sesión'
    formError.value = msg
    // Si vienen campos indicados desde el backend, márcalos en rojo
    const fields = e?.response?.data?.fields
    if (fields) {
      Object.keys(fields).forEach(k => errors.value[k] = true)
    }
  }
}

// --- Finalizar sesión (runner mínimo para probar backend)
const finishInfo = ref<{ finishedAt?: string; score?: number | null; total?: number | null }>({})

async function onFinish() {
  if (!session.value.sessionId) return
  const res = await finishSession(session.value.sessionId)
  // res: { sessionId, finishedAt, score, total }
  session.value.finished = true
  finishInfo.value = {
    finishedAt: res.finishedAt,
    score: res.score ?? null,
    total: res.total ?? null
  }
}

// --- Descargar PDF
function downloadPdf() {
  if (!session.value.sessionId) return
  const base = import.meta.env.VITE_API_URL
  const url = `${base}/sessions/${session.value.sessionId}/report.pdf`
  window.open(url, '_blank', 'noopener')
}

// --- Carga del test
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

    <!-- Formulario previo (solo si NO hay sesión creada) -->
    <div v-if="!loading && !session.sessionId" class="space-y-3 p-4 bg-white rounded-xl shadow">
      <h2 class="text-lg font-semibold">Datos del participante</h2>

      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="text-sm">Nombre *</label>
          <input
            v-model="pre.name"
            :class="['w-full p-2 rounded border', errors.name && 'border-red-500']"
            placeholder="Nombre"
          />
        </div>
        <div>
          <label class="text-sm">Apellidos *</label>
          <input
            v-model="pre.surname"
            :class="['w-full p-2 rounded border', errors.surname && 'border-red-500']"
            placeholder="Apellidos"
          />
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
          <input v-model="pre.gender" class="w-full p-2 rounded border" placeholder="F/M/..." />
        </div>
        <div>
          <label class="text-sm">Mano dominante</label>
          <input v-model="pre.dominantHand" class="w-full p-2 rounded border" placeholder="Derecha/Izquierda" />
        </div>

        <div>
          <label class="text-sm">Posición</label>
          <input v-model="pre.position" class="w-full p-2 rounded border" placeholder="Base/Escolta/Alero/..." />
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
        <button class="px-4 py-2 rounded-xl shadow bg-gray-100" @click="$router.back()">Cancelar</button>
        <button class="px-4 py-2 rounded-xl shadow" @click="startSession">Comenzar test</button>
      </div>
    </div>

    <!-- Runner mínimo (para pruebas end-to-end) -->
    <div v-if="!loading && session.sessionId" class="space-y-4 p-4 bg-white rounded-xl shadow">
      <h2 class="text-lg font-semibold">Sesión #{{ session.sessionId }}</h2>

      <div v-if="!session.finished" class="space-y-3">
        <p class="text-gray-700">
          (Runner mínimo para probar backend)  
          Aquí iría tu flujo real de preguntas.  
          Para esta prueba, pulsa “Finalizar sesión” y luego “Descargar PDF”.
        </p>

        <button class="px-4 py-2 rounded-xl shadow" @click="onFinish">
          Finalizar sesión
        </button>
      </div>

      <div v-else class="space-y-2">
        <p class="text-green-700">
          ¡Sesión finalizada!
          <span v-if="finishInfo.finishedAt" class="ml-2 text-gray-600">
            ({{ new Date(finishInfo.finishedAt).toLocaleString('es-ES') }})
          </span>
        </p>
        <p>Resultado: {{ finishInfo.score ?? '—' }} / {{ finishInfo.total ?? '—' }}</p>

        <div class="flex gap-3">
          <button class="px-4 py-2 rounded-xl shadow" @click="downloadPdf">
            Descargar PDF
          </button>
          <button class="px-4 py-2 rounded-xl shadow bg-gray-100" @click="$router.push('/')">
            Volver al inicio
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* estilos mínimos; Tailwind ya aporta gran parte */
</style>
