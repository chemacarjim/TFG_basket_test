<script setup lang="ts">
import { computed, watch, ref } from 'vue'
import type { ChoiceValue } from '../types/api'

const props = defineProps<{
  question: { id: number; imageUrl: string | null; possessionTime: number }
}>()

const emit = defineEmits<{
  (e: 'answered', payload: { questionId: number; selectedValue: ChoiceValue; responseTimeMs: number }): void
}>()

const startTime = ref<number>(0)
const selectedValue = ref<ChoiceValue | null>(null)

watch(
  () => props.question?.id,
  () => {
    startTime.value = Date.now()
    selectedValue.value = null
  },
  { immediate: true }
)

function selectChoice(choice: ChoiceValue) {
  selectedValue.value = choice
}

function clearSelection() {
  selectedValue.value = null
}

function confirmAnswer() {
  if (!selectedValue.value) return
  const responseTimeMs = Date.now() - startTime.value
  emit('answered', {
    questionId: props.question.id,
    selectedValue: selectedValue.value,
    responseTimeMs,
  })
}

const possessionSizeClass = computed(() => {
  const raw = props.question.possessionTime ?? 0
  const digits = `${Math.abs(raw)}`.length
  return digits >= 2 ? 'double-digit' : 'single-digit'
})

</script>

<template>
  <div class="space-y-5 text-center">
    <div class="flex justify-center">
      <div v-if="question.imageUrl" class="relative w-[80%]">
        <img
          :src="question.imageUrl"
          alt="Jugada de baloncesto"
          class="w-full h-auto max-h-96 rounded-lg shadow object-contain"
        />
        <span
          class="possessionTime"
          :class="possessionSizeClass"
        >
          {{ question.possessionTime }}
        </span>
      </div>
    </div>

    <div class="flex justify-center gap-4">
      <button
        class="min-w-28 px-6 py-2.5 text-base font-semibold rounded shadow transition"
        :class="selectedValue === 'DRIBBLE' ? 'bg-blue-700 text-white ring-2 ring-blue-300' : 'bg-blue-500 text-white hover:bg-blue-600'"
        @click="selectChoice('DRIBBLE')"
      >
        Botar
      </button>

      <button
        class="min-w-28 px-6 py-2.5 text-base font-semibold rounded shadow transition"
        :class="selectedValue === 'PASS' ? 'bg-green-700 text-white ring-2 ring-green-300' : 'bg-green-500 text-white hover:bg-green-600'"
        @click="selectChoice('PASS')"
      >
        Pasar
      </button>

      <button
        class="min-w-28 px-6 py-2.5 text-base font-semibold rounded shadow transition"
        :class="selectedValue === 'SHOOT' ? 'bg-red-700 text-white ring-2 ring-red-300' : 'bg-red-500 text-white hover:bg-red-600'"
        @click="selectChoice('SHOOT')"
      >
        Tirar
      </button>
    </div>

    <div class="flex justify-center gap-3">
      <button
        class="px-5 py-2 rounded-xl bg-indigo-600 text-white text-base font-semibold hover:bg-indigo-700 disabled:opacity-50"
        :disabled="!selectedValue"
        @click="confirmAnswer"
      >
        Confirmar respuesta
      </button>
      <button
        class="px-5 py-2 rounded-xl bg-gray-200 text-gray-800 text-base font-semibold hover:bg-gray-300"
        @click="clearSelection"
      >
        Borrar
      </button>
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