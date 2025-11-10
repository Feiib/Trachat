<template>
  <div class="flex h-screen bg-gray-50 dark:bg-gray-900 relative">
    <!-- 侧边栏 -->
    <div 
      class="flex-shrink-0 overflow-hidden border-r border-gray-200 dark:border-gray-700 transition-all duration-200"
      :class="isSidebarCollapsed ? 'w-0' : 'w-80'"
    >
      <ChatSidebar @toggle-collapse="toggleSidebar" />
    </div>
    
    <!-- 聊天区域 -->
    <div class="flex-1 flex flex-col min-w-0 relative">
      <!-- 展开按钮（折叠时显示） -->
      <button
        v-if="isSidebarCollapsed"
        @click="toggleSidebar()"
        class="absolute top-3 left-3 z-10 p-2 rounded-lg bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 shadow hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
        title="展开侧边栏"
      >
        <svg class="w-5 h-5 text-gray-700 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 12h16M10 6l-6 6 6 6" />
        </svg>
      </button>
      
      <!-- 返回主页按钮 -->
      <button
        @click="goHome"
        class="absolute top-3 right-3 z-10 p-2 rounded-lg bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 shadow hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
        title="返回主页"
      >
        <svg class="w-5 h-5 text-gray-700 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      
      <ChatWindow />
    </div>
  </div>
  
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore'
import { useSessionStore } from '@/store/sessionStore'
import { useChatStore } from '@/store/chatStore'
import ChatSidebar from '@/components/ChatSidebar.vue'
import ChatWindow from '@/components/ChatWindow.vue'

const router = useRouter()
const userStore = useUserStore()
const sessionStore = useSessionStore()
const chatStore = useChatStore()

// 侧边栏折叠状态
const isSidebarCollapsed = ref(false)
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

// 返回主页
const goHome = () => {
  router.push('/')
}

// 页面挂载时初始化
onMounted(async () => {
  // 检查登录状态
  const isAuthenticated = await userStore.checkAuthStatus()
  if (!isAuthenticated) {
    // 如果未登录，跳转到登录页面
    window.location.href = '/login'
    return
  }
  
  // 初始化聊天状态
  const initType = sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' ? 'travel' : 'manus'
  chatStore.initializeChat(initType)
  
  // 获取会话列表
  await sessionStore.fetchSessions()
})

// 当左侧切换智能体类型时，重置聊天窗口并显示对应欢迎语
watch(() => sessionStore.currentAgentType, (newType) => {
  chatStore.clearChat()
  const mappedType = newType === 'TRAVEL_ASSISTANT' ? 'travel' : 'manus'
  chatStore.initializeChat(mappedType)
})
</script>

<style scoped>
/* 确保布局正确 */
.flex {
  display: flex;
}

.h-screen {
  height: 100vh;
}

.min-w-0 {
  min-width: 0;
}

.flex-shrink-0 {
  flex-shrink: 0;
}

.flex-1 {
  flex: 1 1 0%;
}
</style>
