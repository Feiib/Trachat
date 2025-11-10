<template>
  <div id="app" class="min-h-screen flex flex-col">
    <RouterView class="flex-1" />
    <footer class="py-4 text-center text-gray-500 dark:text-gray-400 text-sm">
      <a
        href="https://github.com/Feiib"
        target="_blank"
        rel="noopener noreferrer"
        class="hover:text-blue-600 dark:hover:text-blue-400"
      >
        TraChat By Feiib
      </a>
      <span class="mx-2">|</span>
      <a
        href="https://beian.miit.gov.cn/"
        target="_blank"
        rel="noopener noreferrer"
        class="hover:text-blue-600 dark:hover:text-blue-400"
      >
        陇ICP备2025023321号-1
      </a>
    </footer>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style>
/* 全局样式重置 */
* {
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  :root {
    color-scheme: dark;
  }
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.5);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.7);
}

/* 暗色模式滚动条 */
.dark ::-webkit-scrollbar-thumb {
  background: rgba(75, 85, 99, 0.5);
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: rgba(75, 85, 99, 0.7);
}
</style>
