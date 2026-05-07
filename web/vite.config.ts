import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

export default defineConfig({
  base: '/',
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'chart-vendor': ['chart.js'],
          'react-vendor': ['react', 'react-dom', 'react/jsx-runtime'],
        },
      },
    },
  },
});
