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
  <dialog ref="dialogRef" class="backdrop:bg-black/50 rounded-lg p-0 max-w-lg">
    <div v-if="test" class="p-6 bg-white rounded-lg">
      <h2 class="text-2xl font-semibold">{{ test.title }}</h2>
      <p v-if="test.description" class="mt-2 text-gray-700">{{ test.description }}</p>
      <div class="grid grid-cols-2 gap-4 mt-4 text-sm">
        <div><strong>ID:</strong> {{ test.id }}</div>
        <div><strong>Estado:</strong> Activo</div>
        <div><strong>Preguntas:</strong> {{ Array.isArray(test.questions) ? test.questions.length : '—' }}</div>
      </div>
      <div class="mt-6 flex justify-end gap-2">
        <button class="px-4 py-2 bg-gray-100 rounded-xl" @click="emit('close')">
          Cerrar
        </button>
        <button class="px-4 py-2 bg-blue-600 text-white rounded-xl"
                @click="emit('start', test.id)">
          Iniciar test
        </button>
      </div>
    </div>
  </dialog>
</template>
