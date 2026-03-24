<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MetricCard from '../components/MetricCard.vue'
import TestCard from '../components/TestCard.vue'
import TestDetailsDialog from '../components/TestDetailsDialog.vue'
import { getTestDetail, listActiveTests } from '../api/public'

const router = useRouter()

const tests = ref<any[]>([])
const completedTests = ref<any[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const selectedTest = ref<any | null>(null)
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
    // Keep summary data if detail request fails.
  }
}

function onStart(id: number) {
  router.push(`/test/${id}`)
}
</script>

<template>
  <div class="bg-gray-900 text-gray-100 min-h-screen">
    <section class="py-16 px-6 text-center bg-gradient-to-b from-gray-900 to-gray-800">
      <h1 class="text-4xl md:text-5xl font-extrabold">Basketball IQ</h1>
      <p class="mt-3 text-lg md:text-xl max-w-2xl mx-auto">
        Plataforma de análisis de toma de decisiones en baloncesto
      </p>

      <div class="mt-10 flex flex-col md:flex-row gap-6 max-w-4xl mx-auto">
        <div class="flex-1 p-6 bg-gray-800 rounded-2xl shadow-lg">
          <h3 class="text-2xl font-semibold">Administrar</h3>
          <p class="mt-1 text-gray-400">Gestiona tests y usuarios</p>
          <div class="flex gap-2 mt-4">
            <span class="px-3 py-1 bg-blue-700 text-sm rounded-full">CRUD Tests</span>
            <span class="px-3 py-1 bg-green-700 text-sm rounded-full">CRUD Usuarios</span>
          </div>
          <button class="mt-6 w-full py-2 rounded-xl bg-blue-600 hover:bg-blue-700"
                  @click="router.push('/admin/login')">
            Entrar en admin
          </button>
        </div>

        <!-- Tests -->
        <div class="flex-1 p-6 bg-gray-800 rounded-2xl shadow-lg">
          <h3 class="text-2xl font-semibold">Tests</h3>
          <p class="mt-1 text-gray-400">Realiza tus tests o consulta resultados</p>
          <div class="flex gap-2 mt-4">
            <span class="px-3 py-1 bg-purple-700 text-sm rounded-full">Realizar Test</span>
            <span class="px-3 py-1 bg-yellow-700 text-sm rounded-full">Ver Resultados</span>
          </div>
          <button class="mt-6 w-full py-2 rounded-xl bg-purple-600 hover:bg-purple-700"
                  @click="router.push('/')">
            Ver tests
          </button>
        </div>
      </div>
    </section>

    <section class="py-8 px-6 bg-gray-800">
      <div class="max-w-3xl mx-auto flex flex-wrap justify-center gap-6">
        <MetricCard class="w-full sm:w-72" title="Tests disponibles" :value="tests.length" />
        <MetricCard class="w-full sm:w-72" title="Tests completados" :value="completedTests.length" />
      </div>
    </section>

    <section class="py-10 px-6">
      <div class="max-w-5xl mx-auto">
        <h2 class="text-2xl font-bold mb-6">Tests disponibles</h2>
        <div v-if="loading" class="text-gray-400">Cargando…</div>
        <div v-else-if="error" class="text-red-400">{{ error }}</div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <TestCard v-for="t in tests" :key="t.id" :test="t" @view="onView" @start="onStart" />
        </div>
      </div>
    </section>

    <TestDetailsDialog :visible="detailsVisible"
                       :test="selectedTest"
                       @close="detailsVisible = false"
                       @start="onStart" />
  </div>
</template>
