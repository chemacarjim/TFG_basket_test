<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { ChoiceValue } from '../types/api'

// Props: una pregunta con ID e imagen
const props = defineProps<{
  question: { id: number; imageUrl: string | null }
}>()

// Emitimos un evento 'answered' al responder
const emit = defineEmits<{
  (e: 'answered', payload: { questionId: number; selectedValue: ChoiceValue; responseTimeMs: number }): void
}>()

// Guardamos cuándo empieza la pregunta
const startTime = ref<number>(0)

// Reiniciamos el cronómetro cuando aparece la pregunta
onMounted(() => {
  startTime.value = Date.now()
})

// Al pulsar un botón, calculamos el tiempo y emitimos
function answer(choice: ChoiceValue) {
  const endTime = Date.now()
  const responseTimeMs = endTime - startTime.value
  emit('answered', {
    questionId: props.question.id,
    selectedValue: choice,
    responseTimeMs: responseTimeMs,
  })
  // Para la siguiente pregunta, el cronómetro se reiniciará en el montaje del nuevo componente
}
</script>

<template>
  <div class="space-y-4 text-center">
    <img
      v-if="question.imageUrl"
      :src="question.imageUrl"
      alt="Jugada de baloncesto"
      class="mx-auto max-h-96 rounded-lg shadow"
    />
    <div class="flex justify-center gap-6">
      <button
        class="px-4 py-2 bg-blue-500 text-white rounded shadow"
        @click="answer('DRIBBLE')"
      >Botar</button>
      <button
        class="px-4 py-2 bg-green-500 text-white rounded shadow"
        @click="answer('PASS')"
      >Pasar</button>
      <button
        class="px-4 py-2 bg-red-500 text-white rounded shadow"
        @click="answer('SHOOT')"
      >Tirar</button>
    </div>
  </div>
</template>

<style scoped>
/* puedes ajustar colores y tamaños según Tailwind */
</style>
