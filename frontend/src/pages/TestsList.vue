<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import TestCard from '../components/TestCard.vue'
import TestDetailsDialog from '../components/TestDetailsDialog.vue'
import { getTestDetail, listActiveTests } from '../api/public'
import StartTestDialog from '../components/StartTestDialog.vue'
import { useSessionStore } from '../stores/useSessionStore'

type TestItem = {
  id: number
  title: string
  description: string | null
  questions?: unknown[]
}

const router = useRouter()

const loading = ref(true)
const error = ref<string | null>(null)
const tests = ref<TestItem[]>([])

const sessionStore = useSessionStore()

const startVisible = ref(false)
const starting = ref(false)
const startTest = ref<TestItem | null>(null)
const startError = ref<string | null>(null)

function openStartDialog(test: TestItem | null) {
  if (!test) return
  detailsVisible.value = false
  startError.value = null
  startTest.value = test
  startVisible.value = true
}

function closeStartDialog() {
  startVisible.value = false
  startTest.value = null
}

const searchTerm = ref('')

const filteredTests = computed(() => {
  const q = searchTerm.value.trim().toLowerCase()
  if (!q) return tests.value
  return tests.value.filter((t) => t.title.toLowerCase().includes(q))
})

const selectedTest = ref<TestItem | null>(null)
const detailsVisible = ref(false)

onMounted(async () => {
  try {
    tests.value = await listActiveTests()
  } catch (e: any) {
    error.value = e?.message ?? 'Error cargando tests'
  } finally {
    loading.value = false
  }
})

async function onView(id: number) {
  const summary = tests.value.find((t) => t.id === id) ?? null
  selectedTest.value = summary
  detailsVisible.value = true

  if (!summary) return

  try {
    const detail = await getTestDetail(id)
    selectedTest.value = { ...summary, questions: detail.questions ?? [] }
  } catch {
    // Si falla el detalle, mantenemos los datos básicos.
  }
}

async function confirmStart(payload: {
  name: string
  surname: string
  birthDate?: string
  country?: string
  gender?: string
  dominantHand?: string
  position?: string
  inasidnr?: string
  event?: string
  instructor?: string
}) {
  if (!startTest.value) return

  starting.value = true
  startError.value = null
  try {
    await sessionStore.start(startTest.value.id, payload)
    const testID = startTest.value.id
    closeStartDialog()
    router.push(`/test/${testID}`)
  } catch (e: any) {
    startError.value = e?.response?.data?.message || e?.message || 'No se pudo iniciar el test'
  } finally {
    starting.value = false
  }
}

function onStart(id: number) {
  const t = tests.value.find((x) => x.id === id) ?? null
  openStartDialog(t)
}
</script>

<template>
  <div class="min-h-full bg-gray-900 text-gray-100 px-6 pt-14 pb-10 md:pt-20 md:pb-12">
    <div class="max-w-7xl mx-auto">
      <div class="flex justify-end mb-4">
        <button
          class="px-4 py-2 rounded-xl bg-gray-700 hover:bg-gray-600 text-white"
          @click="router.push('/')"
        >
          Volver al inicio
        </button>
      </div>
      <section class="mb-10 text-center">
        <h1 class="text-4xl md:text-5xl font-extrabold text-white">Tests disponibles</h1>
        <p class="mt-3 text-lg text-gray-300">
          Selecciona un test para ver detalles o comenzar
        </p>
      </section>

      <section class="mb-8 max-w-2xl mx-auto">
        <input
            v-model="searchTerm"
            type="text"
            placeholder="Buscar test por nombre..."
            class="w-full rounded-xl bg-gray-800 border border-gray-600 px-4 py-3 text-white placeholder-gray-400 outline-none focus:ring-2 focus:ring-purple-500"
        />
      </section>

      <hr class="border-0 border-t border-gray-700/80 mb-8" />

      <section class="min-h-[46vh]">
        <div v-if="loading" class="text-center text-gray-400">Cargando tests...</div>
        <div v-else-if="error" class="text-center text-red-400">{{ error }}</div>

        <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-7 auto-rows-fr">
          <TestCard
            v-for="t in filteredTests"
            :key="t.id"
            class="h-full"
            :test="t"
            @view="onView"
            @start="onStart"
          />
        </div>
        <div v-if="!filteredTests.length" class="text-center text-gray-400">
            No se encontraron tests con ese nombre.
        </div>
        <div v-if="startError" class="text-center text-red-400 mt-3">
          {{ startError }}
        </div>
      </section>
    </div>

    <TestDetailsDialog
      :visible="detailsVisible"
      :test="selectedTest"
      @close="detailsVisible = false"
      @start="onStart"
    />
    <StartTestDialog
      :visible="startVisible"
      :test-title="startTest?.title"
      :loading="starting"
      @close="closeStartDialog"
      @start="confirmStart"
    />
  </div>
</template>