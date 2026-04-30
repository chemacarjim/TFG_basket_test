<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/useAuthStore'
import { adminListUsers, adminCreateUser, adminUpdateUser, adminDeleteUser } from '../api/admin'
import type { AdminUser } from '../types/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const loading = ref(true)
const error = ref<string | null>(null)
const success = ref<string | null>(null)
const users = ref<AdminUser[]>([])

const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const createDialogRef = ref<HTMLDialogElement | null>(null)
const editDialogRef = ref<HTMLDialogElement | null>(null)

const createForm = ref({ email: '', password: '', isSuperAdmin: false })
const editForm = ref({ email: '', password: '', isSuperAdmin: false })
const editingUserId = ref<number | null>(null)
let successTimer: ReturnType<typeof setTimeout> | null = null

function showSuccess(message: string) {
  success.value = message
  if (successTimer) clearTimeout(successTimer)
  successTimer = setTimeout(() => {
    success.value = null
  }, 3000)
}

onMounted(async () => {
  auth.restoreFromStorage()
  await load()
})

watch(createDialogVisible, (v) => {
  if (!createDialogRef.value) return
  if (v && !createDialogRef.value.open) createDialogRef.value.showModal()
  if (!v && createDialogRef.value.open) createDialogRef.value.close()
})

watch(editDialogVisible, (v) => {
  if (!editDialogRef.value) return
  if (v && !editDialogRef.value.open) editDialogRef.value.showModal()
  if (!v && editDialogRef.value.open) editDialogRef.value.close()
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

function formatDate(value: string | null) {
  if (!value) return '—'
  try {
    return new Date(value).toLocaleString('es-ES')
  } catch {
    return value
  }
}

function openEditDialog(u: AdminUser) {
  editingUserId.value = u.id
  editForm.value = { email: u.email, password: '', isSuperAdmin: u.isSuperAdmin }
  editDialogVisible.value = true
}

async function createUser() {
  try {
    await adminCreateUser({
      email: createForm.value.email,
      password: createForm.value.password,
      isSuperAdmin: createForm.value.isSuperAdmin,
    })
    createForm.value = { email: '', password: '', isSuperAdmin: false }
    createDialogVisible.value = false
    error.value = null
    showSuccess('Usuario creado correctamente.')
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error al crear usuario'
  }
}

async function saveUserEdit() {
  if (!editingUserId.value) return
  try {
    await adminUpdateUser(editingUserId.value, {
      email: editForm.value.email,
      password: editForm.value.password || undefined,
      isSuperAdmin: editForm.value.isSuperAdmin,
    })
    editDialogVisible.value = false
    editingUserId.value = null
    error.value = null
    showSuccess('Usuario actualizado correctamente.')
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error al guardar'
  }
}

async function deleteUser(u: AdminUser) {
  if (!confirm(`¿Eliminar al administrador ${u.email}?`)) return
  try {
    await adminDeleteUser(u.id)
    error.value = null
    showSuccess('Usuario eliminado correctamente.')
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Error al eliminar'
  }
}

function logout() {
  auth.logout()
  router.push('/admin/login')
}
</script>

<template>
  <div class="h-full bg-gray-50 px-6 py-8">
    <div class="max-w-7xl mx-auto space-y-6">
      <header class="bg-white rounded-2xl border border-gray-200 p-5 shadow-sm">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">Panel de Administración</h1>
            <p class="text-gray-500">Gestión de tests y usuarios</p>
          </div>
          <button
            class="px-4 py-2 rounded-xl border border-gray-300 text-gray-700 hover:bg-gray-100"
            @click="logout"
          >
            Cerrar sesión
          </button>
        </div>
      </header>

      <nav class="flex gap-3">
        <router-link
          to="/admin/tests"
          class="px-5 py-2 rounded-xl font-medium"
          :class="route.path.startsWith('/admin/tests') ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border border-gray-200'"
        >
          Tests
        </router-link>
        <router-link
          v-if="auth.hasSuperAdminRole"
          to="/admin/users"
          class="px-5 py-2 rounded-xl font-medium"
          :class="route.path.startsWith('/admin/users') ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border border-gray-200'"
        >
          Usuarios
        </router-link>
      </nav>

      <div v-if="!auth.token" class="text-red-600 bg-red-50 border border-red-200 rounded-xl p-3">
        Debes iniciar sesión para gestionar usuarios.
      </div>

      <div v-else-if="!auth.hasSuperAdminRole" class="text-amber-700 bg-amber-50 border border-amber-200 rounded-xl p-3">
        Solo un super-admin puede gestionar usuarios.
      </div>

      <div v-else class="space-y-6">
        <section class="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">Gestión de Usuarios</h2>
            <p class="text-gray-600">Listado en tarjetas con edición mediante diálogo</p>
          </div>
          <button
            class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700"
            @click="createDialogVisible = true"
          >
            + Crear usuario
          </button>
        </section>

        <div v-if="loading" class="text-gray-600">Cargando…</div>
        <div v-else-if="error" class="text-red-600 bg-red-50 border border-red-200 rounded-xl p-3">{{ error }}</div>
        <div v-if="success" class="text-green-700 bg-green-50 border border-green-200 rounded-xl p-3">{{ success }}</div>

        <section v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          <article
            v-for="u in users"
            :key="u.id"
            class="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 flex flex-col"
          >
            <div class="flex-1">
              <div class="flex items-start justify-between gap-3">
                <h3 class="text-lg font-semibold text-gray-900">{{ u.email }}</h3>
                <span
                  class="text-xs px-2 py-1 rounded-full"
                  :class="u.isSuperAdmin ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'"
                >
                  {{ u.isSuperAdmin ? 'Super-admin' : 'Admin' }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mt-2">ID: {{ u.id }}</p>
              <p class="text-sm text-gray-500 mt-1">Creado: {{ formatDate(u.createdAt) }}</p>
              <p class="text-sm text-gray-500 mt-1">Último acceso: {{ formatDate(u.lastLoginAt) }}</p>
            </div>
            <div class="grid grid-cols-2 gap-2 mt-4">
              <button class="px-3 py-2 rounded-xl bg-gray-100 hover:bg-gray-200 text-sm" @click="openEditDialog(u)">
                Editar
              </button>
              <button class="px-3 py-2 rounded-xl bg-red-600 hover:bg-red-700 text-white text-sm" @click="deleteUser(u)">
                Eliminar
              </button>
            </div>
          </article>
        </section>
      </div>
    </div>
  </div>

  <dialog ref="createDialogRef" class="backdrop:bg-black/50 rounded-xl p-0 w-[min(92vw,36rem)] max-w-none">
    <div class="p-6 bg-white rounded-xl">
      <h3 class="text-xl font-semibold text-gray-900">Crear usuario</h3>
      <div class="space-y-3 mt-4">
        <input v-model="createForm.email" type="email" placeholder="Email" class="w-full p-2 rounded border" />
        <input v-model="createForm.password" type="password" placeholder="Contraseña" class="w-full p-2 rounded border" />
        <label class="flex items-center gap-2 text-sm">
          <input type="checkbox" v-model="createForm.isSuperAdmin" />
          Super-admin
        </label>
      </div>
      <div class="mt-5 flex justify-end gap-2">
        <button class="px-4 py-2 rounded-xl bg-gray-100" @click="createDialogVisible = false">Cancelar</button>
        <button class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700" @click="createUser">Crear</button>
      </div>
    </div>
  </dialog>

  <dialog ref="editDialogRef" class="backdrop:bg-black/50 rounded-xl p-0 w-[min(92vw,36rem)] max-w-none">
    <div class="p-6 bg-white rounded-xl">
      <h3 class="text-xl font-semibold text-gray-900">Editar usuario</h3>
      <div class="space-y-3 mt-4">
        <input v-model="editForm.email" type="email" placeholder="Email" class="w-full p-2 rounded border" />
        <input v-model="editForm.password" type="password" placeholder="Nueva contraseña (opcional)" class="w-full p-2 rounded border" />
        <label class="flex items-center gap-2 text-sm">
          <input type="checkbox" v-model="editForm.isSuperAdmin" />
          Super-admin
        </label>
      </div>
      <div class="mt-5 flex justify-end gap-2">
        <button class="px-4 py-2 rounded-xl bg-gray-100" @click="editDialogVisible = false">Cancelar</button>
        <button class="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700" @click="saveUserEdit">Guardar cambios</button>
      </div>
    </div>
  </dialog>
</template>
