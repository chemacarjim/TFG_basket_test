<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { ChoiceValue } from '../types/api'

// Props: una pregunta con ID e imagen
const props = defineProps<{
  question: { id: number; imageUrl: string | null; possessionTime: number }
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

const possessionSizeClass = computed(() => {
  const raw = props.question.possessionTime ?? 0
  const digits = `${Math.abs(raw)}`.length
  return digits >= 2 ? 'double-digit' : 'single-digit'
})
</script>

<template>
  <div class="space-y-4 text-center">
    <div class="relative inline-block mx-auto">
      <img
        v-if="question.imageUrl"
        :src="question.imageUrl"
        alt="Jugada de baloncesto"
        class="mx-auto max-h-96 rounded-lg shadow"
      />
      <span
        v-if="question.imageUrl"
        class="possessionTime"
        :class="possessionSizeClass"
      >
        {{ question.possessionTime }}
      </span>
    </div>
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
  @font-face {
    font-family: 'Digital-7';
    src: url('fonts/digital-7.ttf') format('truetype');
  }

  .possessionTime {
    position: absolute;
    top: 12px;
    left: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.35rem 0.5rem;
    font-family: 'Digital-7', sans-serif;
    font-size: 36px;
    line-height: 1;
    color: #d60505;
    letter-spacing: 4px;
    background-color: rgba(0, 0, 0, 0.85);
    border-radius: 12px;
    z-index: 1;
    width: clamp(50px, 10%, 110px);
  }

  .possessionTime.single-digit {
    width: clamp(40px, 8%, 90px);
  }

  .possessionTime.double-digit {
    width: clamp(60px, 12%, 130px);
  }
</style>