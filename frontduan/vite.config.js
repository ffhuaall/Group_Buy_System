import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:8888',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8888',
        changeOrigin: true
      },
      '/product': {
        target: 'http://localhost:8888',
        changeOrigin: true
      }
    }
  }
})