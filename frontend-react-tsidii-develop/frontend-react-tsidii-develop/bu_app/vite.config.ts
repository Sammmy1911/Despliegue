import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
    define: {
        global: 'globalThis',
    },
    base: '/bu-app/',
    server: {
        port: 8082,
    },
});
