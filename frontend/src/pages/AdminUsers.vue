<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/useAuthStore'
import { adminListUsers, adminCreateUser, adminUpdateUser, adminDeleteUser } from '../api/admin'
import type { AdminUser } from '../types/api'

const route = useRoute()
const auth = useAuthStore()
const loading = ref(true)
const error = ref<string|null>(null)
const users = ref<AdminUser[]>([])
const editUserId = ref<number|null>(null)
const form = ref({ email: '', password: '', isSuperAdmin: false })

onMounted(async () => {
  auth.restoreFromStorage()
  await load()
})

async function load() {
  loading.value = true
  error.value = null
  try {
    users.value = await adminListUsers()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error cargando usuarios'
  } finally {
    loading.value = false
  }
}

function startEdit(u: AdminUser) {
  editUserId.value = u.id
  form.value = { email: u.email, password: '', isSuperAdmin: u.isSuperAdmin }
}

function cancelEdit() {
  editUserId.value = null
  form.value = { email: '', password: '', isSuperAdmin: false }
}

async function saveUser() {
  try {
    if (editUserId.value) {
      // actualizar
      await adminUpdateUser(editUserId.value, {
        email: form.value.email,
        password: form.value.password || undefined,
        isSuperAdmin: form.value.isSuperAdmin
      })
    } else {
      // crear
      await adminCreateUser({
        email: form.value.email,
        password: form.value.password,
        isSuperAdmin: form.value.isSuperAdmin
      })
    }
    cancelEdit()
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error al guardar'
  }
}

async function deleteUser(u: AdminUser) {
  if (!confirm(`¿Eliminar al administrador ${u.email}?`)) return
  try {
    await adminDeleteUser(u.id)
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error al eliminar'
  }
}
</script>

<template>
  <div class="max-w-4xl mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Gestión de administradores</h1>

    <nav class="flex gap-4 border-b pb-2 mb-4">
      <router-link
        to="/admin/tests"
        class="px-3 py-2"
        :class="route.path.startsWith('/admin/tests') 
          ? 'border-b-2 border-blue-500 text-blue-500' 
          : 'border-b-2 border-transparent text-gray-600'">
        Tests
      </router-link>
      <router-link
        v-if="auth.hasSuperAdminRole"
        to="/admin/users"
        class="px-3 py-2"
        :class="route.path.startsWith('/admin/users') 
          ? 'border-b-2 border-blue-500 text-blue-500' 
          : 'border-b-2 border-transparent text-gray-600'">
        Usuarios
      </router-link>
    </nav>

    <div v-if="!auth.token" class="text-red-600">Debes iniciar sesión.</div>
    <div v-else-if="error" class="text-red-600 mb-2">{{ error }}</div>

    <div v-else>
      <div v-if="loading">Cargando…</div>

      <!-- Tabla de usuarios -->
      <table v-if="!loading" class="min-w-full table-auto border">
        <thead class="bg-gray-100">
          <tr>
            <th class="p-2 border">ID</th>
            <th class="p-2 border">Email</th>
            <th class="p-2 border">Super-admin</th>
            <th class="p-2 border">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id" class="border-t">
            <td class="p-2 border">{{ u.id }}</td>
            <td class="p-2 border">{{ u.email }}</td>
            <td class="p-2 border">{{ u.isSuperAdmin ? 'Sí' : 'No' }}</td>
            <td class="p-2 border">
              <button class="px-3 py-1 rounded shadow" @click="startEdit(u)">Editar</button>
              <button class="px-3 py-1 rounded shadow ml-2" @click="deleteUser(u)">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Formulario de creación/edición -->
      <div class="mt-6 p-4 bg-gray-50 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">{{ editUserId ? 'Editar administrador' : 'Nuevo administrador' }}</h2>
        <div class="space-y-3">
          <input v-model="form.email" type="email" placeholder="Email" class="w-full p-2 rounded border" />
          <input v-model="form.password" type="password" placeholder="Contraseña" class="w-full p-2 rounded border" />
          <label class="flex items-center gap-2">
            <input type="checkbox" v-model="form.isSuperAdmin" />
            Super-admin
          </label>
          <div class="flex gap-2">
            <button class="px-4 py-2 rounded-xl shadow bg-blue-600 text-white" @click="saveUser">Guardar</button>
            <button v-if="editUserId" class="px-4 py-2 rounded-xl shadow" @click="cancelEdit">Cancelar</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
