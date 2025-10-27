<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adminListSessions } from '../api/admin'

const route = useRoute()
const router = useRouter()
const testId = Number(route.params.id)

const items = ref<Array<{sessionId:number; name:string; surname:string; finishedAt:string|null; score:number|null; total:number|null}>>([])
const offset = ref(0)
const hasMore = ref(true)
const loading = ref(false)
const error = ref<string|null>(null)

async function loadMore() {
  if (loading.value || !hasMore.value) return
  loading.value = true
  error.value = null
  try {
    const res = await adminListSessions(testId, 15, offset.value)
    items.value.push(...res.items.map(item => ({ ...item, sessionId: item.id })))
    offset.value = res.nextOffset ?? (offset.value + res.items.length)
    hasMore.value = !!res.hasMore
  } catch (e:any) {
    error.value = e?.response?.data?.message || e?.message || 'Error cargando sesiones'
  } finally {
    loading.value = false
  }
}

function downloadPdf(sid:number) {
  const base = import.meta.env.VITE_API_URL
  window.open(`${base}/sessions/${sid}/report.pdf`, '_blank', 'noopener')
}

onMounted(loadMore)
</script>

<template>
  <div class="max-w-5xl mx-auto p-6 space-y-4">
    <div class="flex items-center gap-3">
      <button class="px-3 py-1 rounded shadow" @click="router.push('/admin/tests')">← Volver</button>
      <h1 class="text-2xl font-bold">Sesiones del Test {{ testId }}</h1>
    </div>

    <div v-if="error" class="text-red-600">{{ error }}</div>

    <table class="w-full bg-white rounded-xl shadow">
      <thead>
        <tr class="text-left">
          <th class="p-2">Nombre</th>
          <th class="p-2">Apellidos</th>
          <th class="p-2">Fecha realización</th>
          <th class="p-2">Score</th>
          <th class="p-2">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="it in items" :key="it.sessionId" class="border-t">
          <td class="p-2">{{ it.name }}</td>
          <td class="p-2">{{ it.surname }}</td>
          <td class="p-2">
            <span v-if="it.finishedAt">{{ new Date(it.finishedAt).toLocaleString('es-ES') }}</span>
            <span v-else>—</span>
          </td>
          <td class="p-2">{{ it.score ?? '—' }} / {{ it.total ?? '—' }}</td>
          <td class="p-2">
            <button class="px-3 py-1 rounded shadow" @click="downloadPdf(it.sessionId)">Descargar PDF</button>
          </td>
        </tr>
        <tr v-if="!items.length && !loading">
          <td class="p-2 text-center text-gray-500" colspan="5">No hay sesiones finalizadas</td>
        </tr>
      </tbody>
    </table>

    <div class="flex justify-center">
      <button v-if="hasMore" class="px-4 py-2 rounded-xl shadow" @click="loadMore">
        {{ loading ? 'Cargando…' : 'Mostrar más' }}
      </button>
    </div>
  </div>
</template>
