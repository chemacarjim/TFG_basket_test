<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTestDetail } from '../api/public'
import type { TestDetail, ChoiceValue } from '../types/api'
import { useSessionStore } from '../stores/useSessionStore'

const route = useRoute()
const router = useRouter()
const session = useSessionStore()

const test = ref<TestDetail | null>(null)
const idx = ref(0)
const qStart = ref<number | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const finished = ref(false)
const summary = ref<{score:number; total:number; durationMs:number|null} | null>(null)

const currentQ = computed(() => test.value?.questions[idx.value] ?? null)
const hasPrev = computed(() => idx.value > 0)
const hasNext = computed(() => test.value && idx.value < test.value.questions.length - 1)

onMounted(async () => {
  try {
    const testId = Number(route.params.id)
    test.value = await getTestDetail(testId)
    await session.start(testId, 'anon-demo')
    idx.value = 0
    qStart.value = Date.now()
  } catch (e:any) {
    error.value = e?.message ?? 'Error inicializando el test'
  } finally {
    loading.value = false
  }
})

async function answer(choice: ChoiceValue) {
  if (!currentQ.value || qStart.value == null) return
  const elapsed = Date.now() - qStart.value
  session.addLocalResponse({
    questionId: currentQ.value.id,
    selectedValue: choice,
    responseTimeMs: elapsed,
  })

  if (hasNext.value) {
    idx.value++
    qStart.value = Date.now()
  } else {
    // Enviar lote y finalizar
    loading.value = true
    try {
      summary.value = await session.finish()
      finished.value = true
    } catch (e:any) {
      error.value = e?.message ?? 'Error al finalizar la sesión'
    } finally {
      loading.value = false
    }
  }
}

function prev() {
  if (!hasPrev.value) return
  idx.value--
  qStart.value = Date.now()
}

function backHome() {
  router.push('/')
}
</script>

<template>
  <div class="max-w-3xl mx-auto p-6">
    <div v-if="loading">Cargando…</div>
    <div v-else-if="error" class="text-red-600">{{ error }}</div>

    <template v-else>
      <div v-if="finished && summary" class="space-y-3">
        <h1 class="text-2xl font-bold">¡Test finalizado!</h1>
        <p><b>Aciertos:</b> {{ summary.score }} / {{ summary.total }}</p>
        <p><b>Duración:</b> {{ summary.durationMs ? (summary.durationMs/1000).toFixed(1)+'s' : '—' }}</p>
        <div class="flex gap-2">
          <button class="px-4 py-2 rounded-xl shadow" @click="backHome">Volver al inicio</button>
          <!-- PDF más adelante (Sprint 4) -->
        </div>
      </div>

      <div v-else>
        <h1 class="text-xl font-semibold mb-2">{{ test?.title }}</h1>
        <p class="text-sm text-gray-600 mb-4">Pregunta {{ idx + 1 }} de {{ test?.questions.length }}</p>

        <div v-if="currentQ" class="space-y-4">
          <div class="p-4 rounded-xl bg-gray-50">
            <p class="mb-2">{{ currentQ.prompt }}</p>
            <img v-if="currentQ.imageUrl" :src="currentQ.imageUrl" alt="" class="rounded-lg max-h-80 object-contain" />
          </div>

          <div class="flex gap-3">
            <button class="px-4 py-2 rounded-xl shadow" @click="answer('DRIBBLE')">DRIBBLE</button>
            <button class="px-4 py-2 rounded-xl shadow" @click="answer('PASS')">PASS</button>
            <button class="px-4 py-2 rounded-xl shadow" @click="answer('SHOOT')">SHOOT</button>
          </div>

          <div class="flex justify-between items-center mt-2">
            <button class="text-sm text-gray-600" :disabled="!hasPrev" @click="prev">← Anterior</button>
            <span class="text-sm text-gray-500">Tiempo corriendo…</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
