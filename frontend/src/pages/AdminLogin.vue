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
  <div class="max-w-md mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Login admin</h1>
    <div class="space-y-3">
      <input v-model="email" type="email" placeholder="Email" class="w-full p-2 rounded border" />
      <input v-model="password" type="password" placeholder="Password" class="w-full p-2 rounded border" />
      <button @click="submit" class="px-4 py-2 rounded-xl shadow w-full">
        {{ loading ? 'Entrando…' : 'Entrar' }}
      </button>
      <p v-if="error" class="text-red-600 text-sm">{{ error }}</p>
    </div>
  </div>
</template>
