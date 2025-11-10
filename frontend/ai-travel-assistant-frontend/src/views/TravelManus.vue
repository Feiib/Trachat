<template>
  <div class="flex flex-col h-screen bg-gray-50 dark:bg-gray-900">
    <!-- 头部 -->
    <header class="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center space-x-4">
          <button 
            @click="goBack"
            class="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <svg class="w-5 h-5 text-gray-600 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
          </button>
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
            <div>
              <h1 class="text-lg font-semibold text-gray-800 dark:text-white">Travel Manus</h1>
              <p class="text-sm text-gray-500 dark:text-gray-400">ReAct 模式，擅长复杂多步骤行程规划</p>
            </div>
          </div>
        </div>
        
        <div class="flex items-center space-x-2">
          <button 
            @click="clearChat"
            class="px-3 py-1.5 text-sm text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          >
            清空对话
          </button>
          <div class="w-2 h-2 rounded-full" :class="isConnected ? 'bg-green-500' : 'bg-red-500'"></div>
        </div>
      </div>
    </header>

    <!-- 聊天区域 -->
    <main class="flex-1 overflow-hidden flex flex-col">
      <!-- 消息列表 -->
      <div 
        ref="messagesContainer"
        class="flex-1 overflow-y-auto px-6 py-4 space-y-4"
      >
        <div v-if="!chatStore.hasMessages" class="flex items-center justify-center h-full">
          <div class="text-center">
            <div class="w-16 h-16 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">Travel Manus</h3>
            <p class="text-gray-600 dark:text-gray-400 max-w-md">
              我是 Travel Manus，采用 ReAct 模式，擅长复杂多步骤的行程规划与旅行计划，支持火车余票查询和天气查询。请告诉我您的目的地、出行计划，城市天气？
            </p>
          </div>
        </div>
        
        <ChatMessage 
          v-for="message in chatStore.messages" 
          :key="message.id" 
          :message="message" 
        />
      </div>

      <!-- 输入区域 -->
      <div class="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 px-6 py-4">
        <div class="flex items-end space-x-4">
          <div class="flex-1">
            <textarea
              v-model="inputMessage"
              @keydown.enter.prevent="handleEnterKey"
              :disabled="chatStore.isLoading"
              placeholder="请输入您的问题..."
              rows="1"
              class="w-full px-4 py-3 border border-gray-300 dark:border-gray-600 rounded-2xl focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none bg-white dark:bg-gray-700 text-gray-800 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 transition-all duration-200"
              style="min-height: 48px; max-height: 120px;"
            ></textarea>
          </div>
          <button
            @click="sendMessage"
            :disabled="!inputMessage.trim() || chatStore.isLoading || isSending"
            class="px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-2xl hover:from-purple-600 hover:to-pink-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center space-x-2 shadow-lg hover:shadow-xl"
          >
            <svg v-if="!chatStore.isLoading" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
            </svg>
            <svg v-else class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span class="hidden sm:inline">{{ chatStore.isLoading ? '发送中...' : '发送' }}</span>
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/store/chatStore'
import { manusChat, closeSSEConnection } from '@/api/chat'
import { handleChatError } from '@/utils/errorHandler'
import ChatMessage from '@/components/ChatMessage.vue'

const router = useRouter()
const chatStore = useChatStore()

const inputMessage = ref('')
const messagesContainer = ref(null)
const isConnected = ref(false)

// 初始化聊天
onMounted(() => {
  chatStore.initializeChat('manus')
})

// 清理资源
onUnmounted(() => {
  chatStore.closeEventSource()
})

// 监听消息变化，自动滚动到底部
watch(() => chatStore.messages.length, () => {
  nextTick(() => {
    scrollToBottom()
  })
})

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 返回首页
const goBack = () => {
  chatStore.resetStore()
  router.push('/')
}

// 清空对话
const clearChat = () => {
  chatStore.clearChat()
  chatStore.initializeChat('manus')
}

// 防重复请求标志
const isSending = ref(false)

// 处理回车键
const handleEnterKey = (event) => {
  if (event.shiftKey) {
    // Shift + Enter 换行
    return
  }
  // Enter 发送消息
  sendMessage()
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  
  // 增强的重复请求检查
  if (!message || chatStore.isLoading || isConnected.value || isSending.value) {
    console.log('sendMessage blocked at: ' + new Date().toISOString() + ', isSending: ' + isSending.value);
    return
  }
  
  // 立即设置发送状态，防止重复请求
  isSending.value = true

  // 添加用户消息
  chatStore.addMessage('user', message)
  inputMessage.value = ''
  chatStore.setLoading(true)

  // 添加AI消息占位符
  const aiMessage = chatStore.addMessage('ai', '', true)

  try {
    // 建立SSE连接
    const eventSource = manusChat(
      message,
      chatStore.currentChatId,
      // onMessage - 接收流数据
      (chunk) => {
        chatStore.appendToLastMessage(chunk)
      },
      // onError - 改进的错误处理
      (error, hasContent) => {
        console.error('聊天错误:', error)
        
        const { userMessage } = handleChatError(error, hasContent)
        
        // 如果已有内容，不覆盖消息内容，只设置错误状态
        if (hasContent) {
          chatStore.setLastMessageError(userMessage)
        } else {
          // 如果没有内容，显示错误消息
          chatStore.updateLastMessage(userMessage)
          chatStore.setLastMessageError(userMessage)
        }
        
        chatStore.finishStreaming()
        isConnected.value = false
        isSending.value = false // 重置发送状态
      },
      // onComplete - 正常完成
      () => {
        chatStore.finishStreaming()
        chatStore.closeEventSource()
        isConnected.value = false
        isSending.value = false // 重置发送状态
      },
      // onChatId - 接收chatId（不显示在界面上）
      (chatId) => {
        console.log('收到后端chatId:', chatId)
        chatStore.setChatId(chatId)
        // chatId只用于后续请求，不在界面上显示
      }
    )

    chatStore.setEventSource(eventSource)
    isConnected.value = true

  } catch (error) {
    console.error('发送消息失败:', error)
    // 初始化错误，直接设置错误消息
    const { userMessage } = handleChatError(error, false)
    chatStore.updateLastMessage(userMessage)
    chatStore.setLastMessageError(userMessage)
    chatStore.finishStreaming()
    chatStore.setLoading(false)
    isConnected.value = false
    isSending.value = false // 重置发送状态
  }
}
</script>

<style scoped>
/* 自定义滚动条 */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.5);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.7);
}

/* 暗色模式滚动条 */
.dark .overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(75, 85, 99, 0.5);
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(75, 85, 99, 0.7);
}

/* 文本域自动调整高度 */
textarea {
  /* field-sizing: content; */
}
</style>