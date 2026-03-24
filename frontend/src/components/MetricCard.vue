<script setup lang="ts">
defineProps<{
  title: string
  value: number | string
}>()
</script>

<template>
  <div class="flex flex-col items-center p-4 bg-gray-900/80 rounded-xl shadow-md">
    <span class="text-3xl font-bold text-white">{{ value }}</span>
    <span class="mt-1 text-sm text-gray-200 uppercase">{{ title }}</span>
  </div>
</template>
