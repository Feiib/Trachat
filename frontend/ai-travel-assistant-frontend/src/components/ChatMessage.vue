<template>
  <div class="flex mb-6" :class="message.role === 'user' ? 'justify-end' : 'justify-start'">
    <!-- AI头像 -->
    <div v-if="message.role === 'ai'" class="flex-shrink-0 mr-3">
      <div :class="[
        'w-8 h-8 rounded-full flex items-center justify-center',
        sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'
          ? 'bg-gradient-to-r from-blue-500 to-cyan-500'
          : 'bg-gradient-to-r from-purple-500 to-pink-500'
      ]">
        <svg v-if="sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'" class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
        </svg>
        <svg v-else class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
        </svg>
      </div>
    </div>

    <!-- 消息气泡 -->
    <div 
      class="max-w-xs lg:max-w-md xl:max-w-lg px-4 py-3 rounded-2xl shadow-sm animate-slide-up"
      :class="messageClasses"
    >
      <!-- 消息内容 -->
      <div class="text-sm leading-relaxed whitespace-pre-wrap" v-html="formattedContent"></div>
      
      <!-- 消息状态指示器 -->
      <div class="flex items-center justify-between mt-2">
        <!-- 左侧：流式输入指示器或状态 -->
        <div class="flex items-center">
          <!-- 流式输入指示器 -->
          <div v-if="message.isStreaming || message.status === 'sending'" class="flex items-center">
            <div class="flex space-x-1">
              <div class="w-2 h-2 bg-current rounded-full animate-pulse"></div>
              <div class="w-2 h-2 bg-current rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
              <div class="w-2 h-2 bg-current rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
            </div>
            <span class="ml-2 text-xs opacity-70">正在输入...</span>
          </div>
          
          <!-- 完成状态指示器 -->
          <div v-else-if="message.status === 'done'" class="flex items-center">
            <svg class="w-3 h-3 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
            <span class="ml-1 text-xs opacity-70 text-green-600 dark:text-green-400">已完成</span>
          </div>
        </div>
        
        <!-- 右侧：错误提示 -->
        <div v-if="message.hasError" class="flex items-center ml-2">
          <svg class="w-3 h-3 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
          </svg>
          <span class="ml-1 text-xs text-red-600 dark:text-red-400" :title="message.errorMessage">
            {{ message.errorMessage || '发生错误' }}
          </span>
        </div>
      </div>
      
      <!-- 操作与时间戳 -->
      <div class="flex items-center justify-between mt-2">
        <!-- 操作按钮 -->
        <div class="flex items-center space-x-2 opacity-0 hover:opacity-100 transition-opacity duration-200">
          <!-- 复制（用户与AI都显示） -->
          <button
            class="p-1.5 rounded-md hover:bg-black/5 dark:hover:bg-white/10"
            :title="'复制'"
            @click.stop="copyContent"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
            </svg>
          </button>
        </div>

        <!-- 时间戳 -->
        <div class="text-xs opacity-60" :class="timestampClasses">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>
    </div>

    <!-- 用户头像 -->
    <div v-if="message.role === 'user'" class="flex-shrink-0 ml-3">
      <div class="w-8 h-8 rounded-full bg-gradient-to-r from-green-400 to-blue-500 flex items-center justify-center">
        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useSessionStore } from '@/store/sessionStore'

const sessionStore = useSessionStore()

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

// 无对外事件

// 消息气泡样式
const messageClasses = computed(() => {
  if (props.message.role === 'user') {
    return 'bg-gradient-to-r from-blue-500 to-blue-600 text-white ml-auto'
  } else {
    return 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 mr-auto'
  }
})

// 时间戳样式
const timestampClasses = computed(() => {
  if (props.message.role === 'user') {
    return 'text-blue-100'
  } else {
    return 'text-gray-500 dark:text-gray-400'
  }
})

// 格式化消息内容
const formattedContent = computed(() => {
  let content = props.message.content || ''

  // 过滤掉后端的完成标记
  content = content.replace(/\[DONE\]/gi, '')

  // 简单的Markdown支持
  content = content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  content = content.replace(/\*(.*?)\*/g, '<em>$1</em>')
  content = content.replace(/`(.*?)`/g, '<code class="bg-gray-200 dark:bg-gray-600 px-1 py-0.5 rounded text-xs">$1</code>')
  
  // 换行处理
  content = content.replace(/\n/g, '<br>')
  
  return content
})

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  // 如果是今天
  if (diff < 24 * 60 * 60 * 1000) {
    return date.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  }
  
  // 如果是昨天或更早
  return date.toLocaleDateString('zh-CN', { 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

// 复制内容
const copyContent = async () => {
  try {
    await navigator.clipboard.writeText(props.message.content || '')
  } catch (e) {
    // 可根据需要添加fallback
    console.error('复制失败', e)
  }
}

//
</script>

<style scoped>
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-slide-up {
  animation: slideUp 0.3s ease-out;
}

/* 自定义滚动条 */
:deep(code) {
  font-family: 'Courier New', monospace;
}
</style>