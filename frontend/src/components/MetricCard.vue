<script setup lang="ts">
defineProps<{
  title: string
  value: number | string
}>()
</script>

<template>
  <div class="flex flex-col items-center p-7 bg-gray-800 rounded-2xl border border-gray-600/60 shadow-lg">
    <span class="text-4xl font-extrabold text-white">{{ value }}</span>
    <span class="mt-2 text-base text-gray-100 uppercase tracking-wide">{{ title }}</span>
  </div>
</template>
