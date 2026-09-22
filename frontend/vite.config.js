import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // Dev-only proxy so `npm run dev` can call the Spring Boot
    // backend with the same relative URLs used in production
    // (Nginx serves /api/* on :80 in production).
    proxy: {
      "/api": {
        target: "http://localhost:8000",
        changeOrigin: true,
      },
    },
  },
});
