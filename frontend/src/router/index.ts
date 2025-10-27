import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('../pages/Home.vue') },
  { path: '/test/:id', component: () => import('../pages/TestRunner.vue') },
  { path: '/admin/login', component: () => import('../pages/AdminLogin.vue') },
  { path: '/admin/tests', component: () => import('../pages/AdminTests.vue') },
  { path: '/admin/tests/:id/sessions', component: () => import('../pages/AdminTestSessions.vue') },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})
