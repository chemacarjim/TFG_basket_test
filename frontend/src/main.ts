import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { router } from './router'
import './index.css'
import { useAuthStore } from './stores/useAuthStore'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)

useAuthStore().restoreFromStorage()

app.mount('#app')
