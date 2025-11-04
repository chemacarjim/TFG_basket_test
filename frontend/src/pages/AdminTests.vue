<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useAuthStore } from '../stores/useAuthStore'
import { adminListTests, adminCreateTest, adminUpdateTest, adminDeleteTest, adminListQuestions, adminCreateQuestion, adminUpdateQuestion, adminDeleteQuestion, adminUploadImage } from '../api/admin'
import { choiceValues, type ChoiceValue } from '../types/api'

const auth = useAuthStore()
const loading = ref(true)
const error = ref<string|null>(null)
const tests = ref<Array<{id:number; title:string; description:string|null; isActive:boolean}>>([])

const form = ref({ title:'', description:'', isActive:false })
const selectedTestId = ref<number|null>(null)
const questions = ref<any[]>([])
const qform = ref({ prompt:'', possessionTime:0, imageUrl:'', correctValue:choiceValues[0] })
const uploading = ref(false)

onMounted(async () => {
  auth.restoreFromStorage()
  await load()
})

async function load() {
  loading.value = true
  error.value = null
  try { tests.value = await adminListTests() }
  catch(e:any){ error.value = e?.message ?? 'Error cargando tests' }
  finally { loading.value = false }
}

async function createTest() {
  try{
    await adminCreateTest({ ...form.value })
    form.value = { title:'', description:'', isActive:false }
    await load()
  } catch (e:any) {
    const msg = e?.response?.data?.message || e?.message || 'Error creando test'
    error.value = msg 
  }
  
}

async function toggleActive(t:any) {
  try {
    await adminUpdateTest(t.id, { isActive: !t.isActive })
    await load()
  } catch (e:any) {
    const msg = e?.response?.data?.message || e?.message || 'No se pudo actualizar el test'
    error.value = msg 
  }
  
}

async function removeTest(t:any) {
  await adminDeleteTest(t.id)
  if (selectedTestId.value === t.id) { selectedTestId.value = null; questions.value = [] }
  await load()
}

async function selectTest(id:number) {
  selectedTestId.value = id
  questions.value = await adminListQuestions(id)
}

async function createQuestion() {
  if (!selectedTestId.value) return
  await adminCreateQuestion(selectedTestId.value, { ...qform.value })
  qform.value = { prompt:'', possessionTime:0, imageUrl:'', correctValue:choiceValues[0] }
  await selectTest(selectedTestId.value)
}

async function updateQuestion(q:any, patch:any) {
  if (!selectedTestId.value) return
  await adminUpdateQuestion(selectedTestId.value, q.id, patch)
  await selectTest(selectedTestId.value)
}

async function removeQuestion(q:any) {
  if (!selectedTestId.value) return
  await adminDeleteQuestion(selectedTestId.value, q.id)
  await selectTest(selectedTestId.value)
}

async function onUpload(e: Event) {
  const input = e.target as HTMLInputElement
  if (!input.files || !input.files[0]) return
  uploading.value = true
  try {
    const url = await adminUploadImage(input.files[0])
    qform.value.imageUrl = url
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div class="max-w-6xl mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Panel admin</h1>

    <div v-if="!auth.token" class="text-red-600 mb-4">
      Debes iniciar sesión para gestionar contenidos.
    </div>

    <div class="grid grid-cols-2 gap-6">
      <!-- TESTS -->
      <section>
        <h2 class="text-lg font-semibold mb-2">Tests</h2>
        <div class="space-y-2 mb-4">
          <input v-model="form.title" placeholder="Título" class="w-full p-2 rounded border" />
          <input v-model="form.description" placeholder="Descripción" class="w-full p-2 rounded border" />
          <label class="flex items-center gap-2 text-sm">
            <input type="checkbox" v-model="form.isActive"> Activo
          </label>
          <button class="px-4 py-2 rounded-xl shadow" @click="createTest">Crear test</button>
        </div>

        <div v-if="loading">Cargando…</div>
        <div v-else-if="error" class="text-red-600">{{ error }}</div>
        <div v-else class="space-y-2">
          <div v-for="t in tests" :key="t.id" class="p-3 bg-white rounded-xl shadow flex justify-between items-center">
            <div>
              <div class="font-semibold">{{ t.title }}</div>
              <div class="text-sm text-gray-600">{{ t.description }}</div>
            </div>
            <div class="flex gap-2">
              <button class="px-3 py-1 rounded shadow" @click="toggleActive(t)">{{ t.isActive ? 'Desactivar' : 'Activar' }}</button>
              <button class="px-3 py-1 rounded shadow" @click="selectTest(t.id)">Preguntas</button>
              <button class="px-3 py-1 rounded shadow" @click="removeTest(t)">Borrar</button>
              <button class="px-3 py-1 rounded shadow" @click="$router.push(`/admin/tests/${t.id}/sessions`)">Sesiones</button>
            </div>
          </div>
        </div>
      </section>

      <!-- QUESTIONS -->
      <section>
        <h2 class="text-lg font-semibold mb-2">Preguntas del test seleccionado</h2>
        <div v-if="!selectedTestId" class="text-sm text-gray-600">Selecciona un test para ver sus preguntas.</div>

        <div v-else class="space-y-3">
          <div class="space-y-2 p-3 bg-white rounded-xl shadow">
            <input v-model="qform.prompt" placeholder="Enunciado" class="w-full p-2 rounded border" />
            <input v-model.number="qform.possessionTime" type="number" placeholder="Posession time (ms)" class="w-full p-2 rounded border" />
            <div class="flex items-center gap-3">
              <input type="file" accept="image/*" @change="onUpload" />
              <span v-if="uploading" class="text-sm text-gray-500">Subiendo…</span>
            </div>
            <label class="text-sm">Respuesta correcta *</label>
            <select v-model="qform.correctValue" class="w-full p-2 rounded border">
              <option disabled value="">Seleccione una opción</option>
              <option v-for="c in choiceValues" :key="c" :value="c">{{ c.charAt(0).toUpperCase() + c.slice(1) }}</option>
            </select>
            <input v-model="qform.imageUrl" placeholder="URL imagen (rellenado al subir)" class="w-full p-2 rounded border" />
            <button class="px-4 py-2 rounded-xl shadow" @click="createQuestion">Crear pregunta</button>
          </div>

          <div class="space-y-2">
            <div v-for="q in questions" :key="q.id" class="p-3 bg-white rounded-xl shadow">
              <div class="font-medium">{{ q.prompt }}</div>
              <div class="text-sm text-gray-600">
                Tiempo: {{ q.possessionTime ?? '—' }} ms
              </div>
              <img v-if="q.imageUrl" :src="q.imageUrl" class="max-h-40 mt-2 rounded" />
              <div class="flex gap-2 mt-2">
                <button class="px-3 py-1 rounded shadow" @click="removeQuestion(q)">Borrar</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>
