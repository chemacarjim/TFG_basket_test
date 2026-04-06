<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/useAuthStore'
import { adminListTests, adminCreateTest, adminUpdateTest, adminDeleteTest, adminListQuestions, adminCreateQuestion, adminUpdateQuestion, adminDeleteQuestion, adminUploadImage } from '../api/admin'
import { choiceValues, type ChoiceValue } from '../types/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const loading = ref(true)
const error = ref<string|null>(null)
const success = ref<string|null>(null)
const tests = ref<Array<{id:number; title:string; description:string|null; isActive:boolean}>>([])

const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const questionsDialogVisible = ref(false)
const createDialogRef = ref<HTMLDialogElement | null>(null)
const editDialogRef = ref<HTMLDialogElement | null>(null)
const questionsDialogRef = ref<HTMLDialogElement | null>(null)

const createForm = ref({ title: '', description: '', isActive: false })
const editForm = ref({ title: '', description: '', isActive: false })
const editingTestId = ref<number | null>(null)

const selectedTestId = ref<number|null>(null)
const questions = ref<any[]>([])
const qform = ref({ prompt:'', possessionTime:12, imageUrl:'', correctValue:choiceValues[0] })
const uploading = ref(false)

const selectedTestTitle = computed(() => tests.value.find(t => t.id === selectedTestId.value)?.title ?? '')
const selectedTestDescription = computed(() => tests.value.find(t => t.id === selectedTestId.value)?.description ?? '')
const selectedTest = computed(() => tests.value.find(t => t.id === selectedTestId.value) ?? null)
let successTimer: ReturnType<typeof setTimeout> | null = null

function showSuccess(message: string) {
  success.value = message
  if (successTimer) clearTimeout(successTimer)
  successTimer = setTimeout(() => {
    success.value = null
  }, 3000)
}

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

watch(createDialogVisible, (v) => {
  if (!createDialogRef.value) return
  if (v && !createDialogRef.value.open) createDialogRef.value.showModal()
  if (!v && createDialogRef.value.open) createDialogRef.value.close()
})

watch(editDialogVisible, (v) => {
  if (!editDialogRef.value) return
  if (v && !editDialogRef.value.open) editDialogRef.value.showModal()
  if (!v && editDialogRef.value.open) editDialogRef.value.close()
})

watch(questionsDialogVisible, (v) => {
  if (!questionsDialogRef.value) return
  if (v && !questionsDialogRef.value.open) questionsDialogRef.value.showModal()
  if (!v && questionsDialogRef.value.open) questionsDialogRef.value.close()
})

async function createTest() {
  try{
    await adminCreateTest({
      title: createForm.value.title,
      description: createForm.value.description || undefined,
      isActive: createForm.value.isActive,
    })
    createForm.value = { title:'', description:'', isActive:false }
    createDialogVisible.value = false
    error.value = null
    showSuccess('Test creado correctamente.')
    await load()
  } catch (e:any) {
    const msg = e?.response?.data?.message || e?.message || 'Error creando test'
    error.value = msg 
  }
  
}

function openEditDialog(t: {id:number; title:string; description:string|null; isActive:boolean}) {
  editingTestId.value = t.id
  editForm.value = {
    title: t.title,
    description: t.description ?? '',
    isActive: t.isActive,
  }
  editDialogVisible.value = true
}

async function saveTestEdit() {
  if (!editingTestId.value) return
  try {
    await adminUpdateTest(editingTestId.value, {
      title: editForm.value.title,
      description: editForm.value.description || undefined,
      isActive: editForm.value.isActive,
    })
    editDialogVisible.value = false
    editingTestId.value = null
    error.value = null
    showSuccess('Test actualizado correctamente.')
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'No se pudo guardar el test'
  }
}

function logout() {
  auth.logout()
  router.push('/admin/login')
}

async function toggleActive(t:any) {
  try {
    await adminUpdateTest(t.id, { isActive: !t.isActive })
    error.value = null
    showSuccess(`Test ${t.isActive ? 'desactivado' : 'activado'} correctamente.`)
    await load()
  } catch (e:any) {
    const msg = e?.response?.data?.message || e?.message || 'No se pudo actualizar el test'
    error.value = msg 
  }
  
}

async function removeTest(t:any) {
  try {
    await adminDeleteTest(t.id)
    if (selectedTestId.value === t.id) { selectedTestId.value = null; questions.value = [] }
    error.value = null
    showSuccess('Test eliminado correctamente.')
    await load()
  } catch (e:any) {
    error.value = e?.response?.data?.message || e?.message || 'No se pudo eliminar el test'
  }
}

async function selectTest(id:number) {
  try {
    selectedTestId.value = id
    questions.value = await adminListQuestions(id)
    questionsDialogVisible.value = true
  } catch (e:any) {
    error.value = e?.response?.data?.message || e?.message || 'No se pudieron cargar las preguntas'
  }
}

async function createQuestion() {
  if (!selectedTestId.value) return
  try {
    await adminCreateQuestion(selectedTestId.value, { ...qform.value, correctValue:qform.value.correctValue as ChoiceValue })
    qform.value = { prompt:'', possessionTime:0, imageUrl:qform.value.imageUrl, correctValue:choiceValues[0] }
    error.value = null
    showSuccess('Pregunta creada correctamente.')
    await selectTest(selectedTestId.value)
  } catch (e:any) {
    error.value = e?.response?.data?.message || e?.message || 'No se pudo crear la pregunta'
  }
}

async function removeQuestion(q:any) {
  if (!selectedTestId.value) return
  try {
    await adminDeleteQuestion(selectedTestId.value, q.id)
    error.value = null
    showSuccess('Pregunta eliminada correctamente.')
    await selectTest(selectedTestId.value)
  } catch (e:any) {
    error.value = e?.response?.data?.message || e?.message || 'No se pudo eliminar la pregunta'
  }
}

async function saveQuestionCorrectValue(q:any) {
  if (!selectedTestId.value) return
  try {
    await adminUpdateQuestion(selectedTestId.value, q.id, {
      correctValue: q.correctValue as ChoiceValue,
    })
    error.value = null
    showSuccess('Respuesta correcta actualizada.')
    await selectTest(selectedTestId.value)
  } catch (e:any) {
    error.value = e?.response?.data?.message || e?.message || 'No se pudo actualizar la respuesta correcta'
  }
}

async function onUpload(e: Event) {
  const input = e.target as HTMLInputElement
  if (!input.files || !input.files[0]) return
  uploading.value = true
  try {
    const url = await adminUploadImage(input.files[0])
    qform.value.imageUrl = url
    error.value = null
    showSuccess('Imagen subida correctamente.')
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error subiendo imagen'
  } finally {
    uploading.value = false
  }
}

</script>

<template>
  <div class="h-full bg-gray-50 px-6 py-8">
    <div class="max-w-7xl mx-auto space-y-6">
      <!-- Header admin -->
      <header class="bg-white rounded-2xl border border-gray-200 p-5 shadow-sm">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">Panel de Administración</h1>
            <p class="text-gray-500">Gestión de tests y usuarios</p>
          </div>
          <button
            class="px-4 py-2 rounded-xl border border-gray-300 text-gray-700 hover:bg-gray-100"
            @click="logout"
          >
            Cerrar sesión
          </button>
        </div>
      </header>

      <!-- Tabs -->
      <nav class="flex gap-3">
        <router-link
          to="/admin/tests"
          class="px-5 py-2 rounded-xl font-medium"
          :class="route.path.startsWith('/admin/tests') ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border border-gray-200'"
        >
          Tests
        </router-link>
        <router-link
          v-if="auth.hasSuperAdminRole"
          to="/admin/users"
          class="px-5 py-2 rounded-xl font-medium"
          :class="route.path.startsWith('/admin/users') ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border border-gray-200'"
        >
          Usuarios
        </router-link>
      </nav>

      <div v-if="!auth.token" class="text-red-600 bg-red-50 border border-red-200 rounded-xl p-3">
        Debes iniciar sesión para gestionar contenidos.
      </div>

      <div v-else class="space-y-6">
        <section class="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">Gestión de Tests</h2>
            <p class="text-gray-600">Listado en formato tarjetas y edición mediante diálogo</p>
          </div>
          <button
            class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700"
            @click="createDialogVisible = true"
          >
            + Crear test
          </button>
        </section>

        <div v-if="loading" class="text-gray-600">Cargando…</div>
        <div v-else-if="error" class="text-red-600 bg-red-50 border border-red-200 rounded-xl p-3">{{ error }}</div>
        <div v-if="success" class="text-green-700 bg-green-50 border border-green-200 rounded-xl p-3">{{ success }}</div>

        <section v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          <article
            v-for="t in tests"
            :key="t.id"
            class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
          >
            <div class="flex-1">
              <div class="flex items-start justify-between gap-3">
                <h3 class="text-lg font-semibold text-gray-900">{{ t.title }}</h3>
                <span
                  class="text-xs px-2 py-1 rounded-full"
                  :class="t.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'"
                >
                  {{ t.isActive ? 'Activo' : 'Inactivo' }}
                </span>
              </div>
              <p class="text-sm text-gray-600 mt-2 line-clamp-3">{{ t.description || 'Sin descripción' }}</p>
            </div>
            <div class="mt-4">
              <button class="w-full px-3 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white text-sm" @click="selectTest(t.id)">
                Preguntas
              </button>
              <div class="grid grid-cols-2 gap-2 mt-2">
                <button class="px-3 py-2 rounded-xl bg-amber-500 hover:bg-amber-600 text-white text-sm" @click="toggleActive(t)">
                  {{ t.isActive ? 'Desactivar' : 'Activar' }}
                </button>
                <button class="px-3 py-2 rounded-xl bg-red-600 hover:bg-red-700 text-white text-sm" @click="removeTest(t)">Borrar</button>
              </div>
              <button
                class="mt-2 w-full px-3 py-2 rounded-xl border border-gray-300 hover:bg-gray-50 text-sm"
                @click="router.push(`/admin/tests/${t.id}/sessions`)"
              >
                Ver sesiones
              </button>
            </div>
          </article>
        </section>
      </div>
    </div>
  </div>

  <dialog ref="createDialogRef" class="backdrop:bg-black/50 rounded-xl p-0 w-[min(92vw,36rem)] max-w-none">
    <div class="p-6 bg-white rounded-xl">
      <h3 class="text-xl font-semibold text-gray-900">Crear test</h3>
      <div class="space-y-3 mt-4">
        <input v-model="createForm.title" placeholder="Título" class="w-full p-2 rounded border" />
        <textarea v-model="createForm.description" placeholder="Descripción" class="w-full p-2 rounded border min-h-24" />
        <label class="flex items-center gap-2 text-sm">
          <input type="checkbox" v-model="createForm.isActive" />
          Activo
        </label>
      </div>
      <div class="mt-5 flex justify-end gap-2">
        <button class="px-4 py-2 rounded-xl bg-gray-100" @click="createDialogVisible = false">Cancelar</button>
        <button class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700" @click="createTest">Crear</button>
      </div>
    </div>
  </dialog>

  <dialog ref="editDialogRef" class="backdrop:bg-black/50 rounded-xl p-0 w-[min(92vw,36rem)] max-w-none">
    <div class="p-6 bg-white rounded-xl">
      <h3 class="text-xl font-semibold text-gray-900">Editar test</h3>
      <div class="space-y-3 mt-4">
        <input v-model="editForm.title" placeholder="Título" class="w-full p-2 rounded border" />
        <textarea v-model="editForm.description" placeholder="Descripción" class="w-full p-2 rounded border min-h-24" />
        <label class="flex items-center gap-2 text-sm">
          <input type="checkbox" v-model="editForm.isActive" />
          Activo
        </label>
      </div>
      <div class="mt-5 flex justify-end gap-2">
        <button class="px-4 py-2 rounded-xl bg-gray-100" @click="editDialogVisible = false">Cancelar</button>
        <button class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700" @click="saveTestEdit">Guardar cambios</button>
      </div>
    </div>
  </dialog>

  <dialog ref="questionsDialogRef" class="backdrop:bg-black/50 rounded-xl p-0 w-[min(96vw,72rem)] max-w-none">
    <div class="p-6 bg-white rounded-xl">
      <div class="flex items-start justify-between gap-3">
        <div>
          <h3 class="text-xl font-semibold text-gray-900">Preguntas de: {{ selectedTestTitle || '—' }}</h3>
          <div class="mt-1 flex items-center justify-between gap-3">
            <p class="text-sm text-gray-600">
              {{ selectedTestDescription || 'Sin descripción del test.' }}
            </p>
            <button
              v-if="selectedTest"
              class="px-3 py-1.5 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm whitespace-nowrap"
              @click="openEditDialog(selectedTest)"
            >
              Editar
            </button>
          </div>
        </div>
        <button class="px-3 py-2 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-700" @click="questionsDialogVisible = false">
          Cerrar
        </button>
      </div>

      <div class="space-y-4 mt-5">
        <div class="space-y-2 p-4 bg-gray-50 rounded-xl border border-gray-200">
          <input v-model="qform.prompt" placeholder="Enunciado" class="w-full p-2 rounded border" />
          <label class="text-sm text-gray-600">Tiempo de posesión</label>
          <input v-model.number="qform.possessionTime" type="number" class="w-full p-2 rounded border" />
          <div class="flex items-center gap-3">
            <input type="file" accept="image/*" @change="onUpload" />
            <span v-if="uploading" class="text-sm text-gray-500">Subiendo…</span>
          </div>
          <label class="text-sm text-gray-600">Respuesta correcta</label>
          <select v-model="qform.correctValue" class="w-full p-2 rounded border">
            <option disabled value="">Seleccione una opción</option>
            <option v-for="c in choiceValues" :key="c" :value="c">{{ c.charAt(0).toUpperCase() + c.slice(1) }}</option>
          </select>
          <button class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700" @click="createQuestion">Crear pregunta</button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 max-h-[46vh] overflow-y-auto pr-1">
          <article v-for="q in questions" :key="q.id" class="p-4 bg-gray-50 rounded-xl border border-gray-200">
            <div class="font-medium text-gray-900">{{ q.prompt }}</div>
            <div class="text-sm text-gray-600 mt-1">Tiempo: {{ q.possessionTime ?? '—' }}</div>
            <img v-if="q.imageUrl" :src="q.imageUrl" class="w-full max-h-40 object-cover mt-2 rounded" />
            <div class="mt-3">
              <label class="text-xs text-gray-500">Respuesta correcta</label>
              <select v-model="q.correctValue" class="w-full mt-1 p-2 rounded border text-sm">
                <option disabled value="">Seleccione una opción</option>
                <option v-for="c in choiceValues" :key="c" :value="c">{{ c.charAt(0).toUpperCase() + c.slice(1) }}</option>
              </select>
            </div>
            <div class="mt-3 grid grid-cols-2 gap-2">
              <button class="px-3 py-1.5 rounded-lg bg-blue-600 text-white hover:bg-blue-700 text-sm" @click="saveQuestionCorrectValue(q)">
                Guardar
              </button>
              <button class="px-3 py-1.5 rounded-lg bg-red-600 text-white hover:bg-red-700 text-sm" @click="removeQuestion(q)">
                Borrar
              </button>
            </div>
          </article>
        </div>
      </div>
    </div>
  </dialog>
</template>