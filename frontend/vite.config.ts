import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/TFG_basket_test/',  // prefijo para GitHub Pages
  plugins: [vue()],
})
