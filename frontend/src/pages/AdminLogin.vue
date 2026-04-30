<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../stores/useAuthStore'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref<string|null>(null)
const auth = useAuthStore()
const router = useRouter()

async function submit() {
  loading.value = true
  error.value = null
  try {
    await auth.login(email.value, password.value)
    router.push('/admin/tests')
  } catch (e:any) {
    error.value = 'Credenciales inválidas'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-full px-6 py-14">
    <div class="max-w-md mx-auto">
      <div class="flex justify-end mb-6">
        <button
          class="px-4 py-2 rounded-xl bg-gray-700 hover:bg-gray-600 text-white"
          @click="router.push('/')"
        >
          Volver al inicio
        </button>
      </div>

      <div class="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div class="text-center mb-8">
          <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-blue-600 text-white flex items-center justify-center text-2xl">
            🔒
          </div>
          <h1 class="text-3xl font-bold text-gray-900">Acceso Administrador</h1>
          <p class="text-gray-600 mt-2">Introduce tus credenciales para continuar</p>
        </div>

        <form class="space-y-4" @submit.prevent="submit">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              v-model="email"
              type="email"
              placeholder="admin@ejemplo.com"
              class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
            <input
              v-model="password"
              type="password"
              placeholder="••••••••"
              class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <p v-if="error" class="text-red-600 text-sm bg-red-50 border border-red-200 rounded-lg px-3 py-2">
            {{ error }}
          </p>

          <button
            type="submit"
            class="w-full px-4 py-2.5 rounded-xl shadow bg-blue-600 text-white font-medium hover:bg-blue-700 disabled:opacity-60"
            :disabled="loading"
          >
            {{ loading ? 'Entrando…' : 'Iniciar sesión' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
