<script setup lang="ts">

defineProps<{
  test: {
    id: number
    title: string
    description: string | null
  }
}>()

const emit = defineEmits<{
  (e: 'view', id: number): void
  (e: 'start', id: number): void
}>()
</script>

<template>
  <div class="p-4 bg-white rounded-2xl shadow hover:shadow-lg transition">
    <h3 class="text-lg font-semibold text-black">{{ test.title }}</h3>
    <p class="mt-1 text-sm text-gray-600 line-clamp-2" v-if="test.description">
      {{ test.description }}
    </p>
    <div class="flex gap-2 mt-4">
      <button class="flex-1 px-3 py-2 rounded-xl bg-emerald-600 text-white hover:bg-emerald-700 transition"
              @click="emit('view', test.id)">
        Ver
      </button>
      <button class="flex-1 px-3 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700"
              @click="emit('start', test.id)">
        Iniciar
      </button>
    </div>
  </div>
</template>
