<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MetricCard from '../components/MetricCard.vue'
import { getPublicAverageScore, listActiveTests } from '../api/public'

const router = useRouter()

const loading = ref(true)
const error = ref<string | null>(null)
const availableTests = ref(0)
const averageScore = ref('0.0%')

function isIqTestTitle(title: string | null | undefined) {
  return (title ?? '').trim().toLowerCase().includes('iq')
}

onMounted(async () => {
  try {
    const [tests, average] = await Promise.all([listActiveTests(), getPublicAverageScore()])
    availableTests.value = tests.filter((test) => !isIqTestTitle(test.title)).length
    const safeAvg = Number.isFinite(average.averageScorePercent) ? average.averageScorePercent : 0
    averageScore.value = `${safeAvg.toFixed(1)}%`
  } catch (e: any) {
    error.value = e?.message ?? 'Error cargando tests'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-full bg-gray-900 text-gray-100 px-6 py-8 md:py-10">
    <div class="max-w-6xl mx-auto">
      <!-- Hero -->
      <section class="text-center mb-14">
        <div class="text-6xl mb-4">🏀</div>
        <h1 class="text-5xl md:text-6xl font-extrabold text-white">Basketball Tests</h1>
        <p class="mt-4 text-xl md:text-2xl text-gray-300">
          Plataforma de análisis de toma de decisiones en baloncesto
        </p>
      </section>

      <!-- Métricas (sin IQ) -->
      <section class="mb-14">
        <div v-if="loading" class="text-center text-gray-400">Cargando métricas...</div>
        <div v-else-if="error" class="text-center text-red-400">{{ error }}</div>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-7 max-w-4xl mx-auto">
          <MetricCard title="Tests disponibles" :value="availableTests" />
          <MetricCard title="Nota media" :value="averageScore" />
        </div>
      </section>

      <!-- Acciones principales -->
      <section class="grid grid-cols-1 md:grid-cols-2 gap-7">
        <article
          class="p-10 rounded-2xl bg-gray-800 shadow-lg hover:shadow-xl transition cursor-pointer border border-transparent hover:border-blue-500/40 text-center"
          @click="router.push('/admin/login')"
        >
          <h2 class="text-3xl font-bold text-white mb-3">Administrar</h2>
          <p class="text-gray-300 text-lg mb-5">Gestiona tests y usuarios</p>
          <div class="flex flex-wrap justify-center gap-2">
            <span class="px-3 py-1 text-sm rounded-full bg-blue-700 text-blue-100">CRUD Tests</span>
            <span class="px-3 py-1 text-sm rounded-full bg-green-700 text-green-100">CRUD Usuarios</span>
          </div>
        </article>

        <article
          class="p-10 rounded-2xl bg-gray-800 shadow-lg hover:shadow-xl transition cursor-pointer border border-transparent hover:border-purple-500/40 text-center"
          @click="router.push('/tests')"
        >
          <h2 class="text-3xl font-bold text-white mb-3">Tests</h2>
          <p class="text-gray-300 text-lg mb-5">Realiza tests de evaluación y consulta resultados</p>
          <div class="flex flex-wrap justify-center gap-2">
            <span class="px-3 py-1 text-sm rounded-full bg-purple-700 text-purple-100">Realizar Test</span>
            <span class="px-3 py-1 text-sm rounded-full bg-yellow-700 text-yellow-100">Ver Resultados</span>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>