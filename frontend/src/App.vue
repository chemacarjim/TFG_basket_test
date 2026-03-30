<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gray-100 text-gray-900">
    <!-- Contenido -->
    <main
      class="flex-1"
      :class="route.path === '/' || route.path === '/tests' || route.path.startsWith('/test/') ? 'bg-gray-900' : ''"
    >
      <RouterView />
    </main>

    <!-- Footer -->
    <footer class="bg-gray-800 text-gray-300 text-center py-4 text-sm">
      © {{ new Date().getFullYear() }} Chema Cárceles — Proyecto TFG
    </footer>
  </div>
</template>

<style scoped>
html, body {
  margin: 0;
  padding: 0;
}
</style>
