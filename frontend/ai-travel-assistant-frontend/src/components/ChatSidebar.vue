<template>
  <div class="flex flex-col h-full bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700">
    <!-- 顶部栏：右上折叠按钮 -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-end">
        <!-- 右上角折叠按钮 -->
        <button
          @click="$emit('toggle-collapse')"
          class="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          title="折叠侧边栏"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4m6 6l-6-6 6-6" />
          </svg>
        </button>
      </div>
    </div>

    <!-- 智能体切换 -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700">
      <div class="flex space-x-2">
        <button
          @click="switchAgent('TRAVEL_ASSISTANT')"
          :class="[
            'flex-1 px-3 py-2 text-sm font-medium rounded-lg transition-colors',
            sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
          ]"
        >
          旅行助手
        </button>
        <button
          @click="switchAgent('MANUS_AGENT')"
          :class="[
            'flex-1 px-3 py-2 text-sm font-medium rounded-lg transition-colors',
            sessionStore.currentAgentType === 'MANUS_AGENT'
              ? 'bg-purple-500 text-white'
              : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
          ]"
        >
          Travel Manus
        </button>
      </div>
    </div>

    <!-- 会话列表区域 -->
    <div class="flex-1 overflow-hidden flex flex-col">
      <!-- 会话列表头部 -->
      <div class="flex items-center justify-between p-4">
        <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300">会话列表</h3>
        <button
          @click="createNewSession"
          class="p-1 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors"
          title="新建会话"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
          </svg>
        </button>
      </div>

      <!-- 会话列表 -->
      <div class="flex-1 overflow-y-auto px-2 pb-2">
        <div v-if="sessionStore.isLoading" class="flex items-center justify-center py-8">
          <svg class="w-6 h-6 animate-spin text-gray-500" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>
        
        <div v-else-if="!sessionStore.hasSessions" class="text-center py-8">
          <div class="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-3">
            <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
            </svg>
          </div>
          <p class="text-sm text-gray-500 dark:text-gray-400">暂无会话</p>
          <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">点击上方 + 号创建新会话</p>
        </div>

        <div v-else class="space-y-1">
          <div
            v-for="session in sessionStore.currentSessions"
            :key="session.id"
            @click="selectSession(session)"
            :class="[
              'group relative p-3 rounded-lg cursor-pointer transition-colors',
              sessionStore.currentSession?.id === session.id
                ? 'bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800'
                : 'hover:bg-gray-50 dark:hover:bg-gray-700'
            ]"
          >
            <!-- 会话标题 -->
            <div class="flex items-start space-x-3">
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-800 dark:text-white truncate">
                  {{ session.title || '新会话' }}
                </p>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  {{ formatDate(session.updateTime) }}
                </p>
              </div>
              
              <!-- 会话操作菜单 -->
              <div class="opacity-0 group-hover:opacity-100 transition-opacity">
                <div class="flex items-center space-x-1">
                  <!-- 重命名按钮 -->
                  <button
                    @click.stop="renameSession(session)"
                    class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600 rounded transition-colors"
                    title="重命名"
                  >
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                    </svg>
                  </button>
                  
                  <!-- 删除按钮 -->
                  <button
                    @click.stop="deleteSession(session)"
                    class="p-1 text-gray-400 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors"
                    title="删除会话"
                  >
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部用户信息区域 -->
    <div class="mt-auto p-4 border-t border-gray-200 dark:border-gray-700">
      <div class="flex items-center space-x-3">
        <!-- 用户头像 -->
        <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-full flex items-center justify-center">
          <span class="text-white font-semibold text-sm">
            {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
          </span>
        </div>
        <!-- 用户信息 -->
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-gray-800 dark:text-white truncate">
            {{ userStore.userInfo?.username || '用户' }}
          </p>
          <p class="text-xs text-gray-500 dark:text-gray-400">在线</p>
        </div>
        <!-- 退出登录按钮 -->
        <button
          @click="handleLogout"
          class="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          title="退出登录"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/userStore'
import { useSessionStore } from '@/store/sessionStore'
import { useChatStore } from '@/store/chatStore'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const sessionStore = useSessionStore()
const chatStore = useChatStore()
const router = useRouter()

// 组件挂载时获取会话列表
onMounted(async () => {
  await sessionStore.fetchSessions()
})

// 切换智能体类型
const switchAgent = async (agentType) => {
  await sessionStore.switchAgentType(agentType)
}

// 选择会话
const selectSession = async (session) => {
  await sessionStore.switchSession(session)
}

// 创建新会话
  const createNewSession = () => {
    // 切换到空会话
    sessionStore.switchSession(null)
    // 清空当前聊天消息
    chatStore.clearChat()
    // 重新初始化聊天
    const mappedType = sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' ? 'travel' : 'manus'
    chatStore.initializeChat(mappedType)
  }

// 重命名会话
const renameSession = async (session) => {
  const newTitle = prompt('请输入新的会话标题:', session.title || '新会话')
  if (newTitle && newTitle.trim() && newTitle !== session.title) {
    const result = await sessionStore.renameSession(session.id, newTitle.trim())
    if (!result.success) {
      alert(result.message)
    }
  }
}

// 删除会话
const deleteSession = async (session) => {
  if (confirm('确定要删除这个会话吗？此操作不可恢复。')) {
    const result = await sessionStore.removeSession(session.id)
    if (!result.success) {
      alert(result.message)
    }
  }
}

// 退出登录
const handleLogout = async () => {
  if (confirm('确定要退出登录吗？')) {
    await userStore.userLogout()
    router.push('/login')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) { // 1分钟内
    return '刚刚'
  } else if (diff < 3600000) { // 1小时内
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) { // 24小时内
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return date.toLocaleDateString()
  }
}
</script>

<style scoped>
/* 自定义滚动条 */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.3);
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.5);
}

/* 暗色模式滚动条 */
.dark .overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(75, 85, 99, 0.3);
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(75, 85, 99, 0.5);
}
</style>
