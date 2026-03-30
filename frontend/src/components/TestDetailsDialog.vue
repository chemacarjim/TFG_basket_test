<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  visible: boolean
  test: {
    id: number
    title: string
    description?: string | null
    questions?: unknown[]
  } | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'start', id: number): void
}>()

const dialogRef = ref<HTMLDialogElement | null>(null)

watch(
  () => props.visible,
  (v) => {
    if (!dialogRef.value) return
    if (v) dialogRef.value.showModal()
    else dialogRef.value.close()
  }
)
</script>

<template>
  <dialog ref="dialogRef" class="backdrop:bg-black/50 rounded-xl p-0 w-[min(92vw,58rem)] max-w-none">
    <div v-if="test" class="p-10 bg-white rounded-xl">
      <h2 class="text-4xl font-semibold">{{ test.title }}</h2>
      <p v-if="test.description" class="mt-3 text-lg text-gray-700">{{ test.description }}</p>
      <div class="grid grid-cols-2 gap-6 mt-6 text-base">
        <div><strong>ID:</strong> {{ test.id }}</div>
        <div><strong>Estado:</strong> Activo</div>
        <div><strong>Preguntas:</strong> {{ Array.isArray(test.questions) ? test.questions.length : '—' }}</div>
      </div>
      <div class="mt-10 flex justify-end gap-3">
        <button class="px-6 py-3 bg-gray-100 rounded-xl text-lg" @click="emit('close')">
          Cerrar
        </button>
        <button class="px-6 py-3 bg-blue-600 text-white rounded-xl text-lg"
                @click="emit('start', test.id)">
          Iniciar test
        </button>
      </div>
    </div>
  </dialog>
</template>
