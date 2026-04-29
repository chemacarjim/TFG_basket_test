import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes = [
  { path: '/', component: () => import('../pages/Home.vue') },
  { path: '/test/:id', component: () => import('../pages/TestRunner.vue') },
  { path: '/admin/login', component: () => import('../pages/AdminLogin.vue') },
  { path: '/admin/tests', component: () => import('../pages/AdminTests.vue') },
  { path: '/admin/tests/:id/sessions', component: () => import('../pages/AdminTestSessions.vue') },
  { path: '/admin/users', component: () => import('../pages/AdminUsers.vue') },
]

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})
