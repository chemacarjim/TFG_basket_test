<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listActiveTests } from '../api/public'
import type { TestSummary } from '../types/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(true)
const tests = ref<TestSummary[]>([])
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    tests.value = await listActiveTests()
  } catch (e:any) {
    error.value = e?.message ?? 'Error cargando tests'
  } finally {
    loading.value = false
  }
})

function go(id:number) {
  router.push(`/test/${id}`)
}
</script>

<template>
  <div class="max-w-3xl mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Tests activos</h1>

    <div v-if="loading">Cargando…</div>
    <div v-else-if="error" class="text-red-600">{{ error }}</div>

    <div v-else class="grid gap-4">
      <div v-for="t in tests" :key="t.id" class="p-4 rounded-2xl shadow cursor-pointer hover:shadow-md"
           @click="go(t.id)">
        <h2 class="text-lg font-semibold">{{ t.title }}</h2>
        <p class="text-sm text-gray-600" v-if="t.description">{{ t.description }}</p>
      </div>
    </div>
  </div>
</template>
