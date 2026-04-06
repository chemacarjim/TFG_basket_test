import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/useAuthStore'

const routes: RouteRecordRaw[] = [
  { path: '/', component: () => import('../pages/Home.vue') },
  { path: '/tests', component: () => import('../pages/TestsList.vue') },
  { path: '/test/:id', component: () => import('../pages/TestRunner.vue') },
  { path: '/admin/login', component: () => import('../pages/AdminLogin.vue') },
  { path: '/admin/tests', component: () => import('../pages/AdminTests.vue'), meta: { requiresAdminAuth: true } },
  { path: '/admin/tests/:id/sessions', component: () => import('../pages/AdminTestSessions.vue'), meta: { requiresAdminAuth: true } },
  { path: '/admin/users', component: () => import('../pages/AdminUsers.vue'), meta: { requiresAdminAuth: true, requiresSuperAdmin: true } },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  auth.restoreFromStorage()

  const requiresAdminAuth = to.matched.some((record) => record.meta.requiresAdminAuth)
  const requiresSuperAdmin = to.matched.some((record) => record.meta.requiresSuperAdmin)

  if (requiresAdminAuth && !auth.token) {
    return '/admin/login'
  }

  if (requiresSuperAdmin && !auth.hasSuperAdminRole) {
    return '/admin/tests'
  }

  return true
})
