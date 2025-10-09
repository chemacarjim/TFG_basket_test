<script setup lang="ts">
import { RouterView, RouterLink } from 'vue-router'
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gray-100 text-gray-900">
    <!-- Header -->
    <header class="bg-white shadow-md">
      <nav class="max-w-6xl mx-auto flex items-center justify-between px-6 py-4">
        <RouterLink to="/" class="text-xl font-bold hover:text-blue-600 transition-colors">
          Plataforma de test de baloncesto
        </RouterLink>

        <div class="flex gap-4">
          <RouterLink to="/" class="hover:underline">Inicio</RouterLink>
          <RouterLink to="/admin/login" class="hover:underline">Administrar</RouterLink>
        </div>
      </nav>
    </header>

    <!-- Contenido -->
    <main class="flex-1">
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
