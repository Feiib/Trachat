<template>
  <div class="flex flex-col h-full bg-gray-50 dark:bg-gray-900">
    <!-- 聊天头部 -->
    <div class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-3 ml-12">
          <!-- 智能体图标 -->
          <div :class="[
            'w-10 h-10 rounded-full flex items-center justify-center',
            sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'
              ? 'bg-gradient-to-r from-blue-500 to-cyan-500'
              : 'bg-gradient-to-r from-purple-500 to-pink-500'
          ]">
            <svg v-if="sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'" class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <svg v-else class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
            </svg>
          </div>
          <div>
            <h1 class="text-lg font-semibold text-gray-800 dark:text-white">
              {{ sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' ? '旅行助手对话' : 'Travel Manus' }}
            </h1>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              {{ sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' ? '景点、美食、天气等快捷问答' : '基于 ReAct 模式，专业的行程规划与旅行助手，支持火车票查询、天气预报等服务' }}
            </p>
          </div>
        </div>
        
        <div class="flex items-center space-x-2">
          <!-- 清空会话按钮 -->
          <button 
            v-if="sessionStore.currentSession"
            @click="clearCurrentSession"
            class="px-3 py-1.5 text-sm text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          >
            清空对话
          </button>
          <!-- 连接状态指示器 -->
          <div class="w-2 h-2 rounded-full" :class="isConnected ? 'bg-green-500' : 'bg-red-500'"></div>
        </div>
      </div>
    </div>

    <!-- 聊天消息区域 -->
    <div class="flex-1 overflow-hidden flex flex-col">
      <!-- 消息列表 -->
      <div 
        ref="messagesContainer"
        class="flex-1 overflow-y-auto px-6 py-4 space-y-4"
      >
        <!-- 空状态 -->
        <div v-if="!sessionStore.currentSession && !chatStore.hasMessages" class="flex items-center justify-center h-full">
          <div class="text-center">
            <div :class="[
              'w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4',
              sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'
                ? 'bg-gradient-to-r from-blue-500 to-cyan-500'
                : 'bg-gradient-to-r from-purple-500 to-pink-500'
            ]">
              <svg v-if="sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'" class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
              </svg>
              <svg v-else class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">
              {{ sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' ? '开始您的旅行快捷查询' : 'Travel Manus' }}
            </h3>
            <p class="text-gray-600 dark:text-gray-400 max-w-md">
              {{ sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'
                ? '我擅长景点推荐、美食推荐、天气查询等快速问答。请告诉我您的目的地或问题！'
                : '我是 Travel Manus，采用 ReAct 模式，擅长复杂多步骤的行程规划与旅行计划，支持火车余票查询和天气查询。请告诉我您的目的地、出行计划，城市天气？'
              }}
            </p>
          </div>
        </div>
        
        <!-- 历史消息 -->
        <ChatMessage 
          v-for="message in sessionStore.chatHistory" 
          :key="message.id" 
          :message="message"
        />
        
        <!-- 当前会话消息 -->
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
              @keydown.enter.prevent.stop="handleEnterKey"
              :disabled="chatStore.isLoading"
              :placeholder="sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' ? '请输入您的旅行问题...' : '请输入您的问题...'"
              rows="1"
              class="w-full px-4 py-3 border border-gray-300 dark:border-gray-600 rounded-2xl focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none bg-white dark:bg-gray-700 text-gray-800 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 transition-all duration-200"
              style="min-height: 48px; max-height: 120px;"
            ></textarea>
          </div>
          <button
            @click.prevent.stop="sendMessage"
            :disabled="!inputMessage.trim() || chatStore.isLoading || isSending"
            :class="[
              'px-6 py-3 text-white rounded-2xl hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center space-x-2 shadow-lg',
              sessionStore.currentAgentType === 'TRAVEL_ASSISTANT'
                ? 'bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600'
                : 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600'
            ]"
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useChatStore } from '@/store/chatStore'
import { useSessionStore } from '@/store/sessionStore'
import { travelAssistantChat, manusChat, closeSSEConnection, travelAssistantChatOnce, manusChatOnce } from '@/api/chat'
import { handleChatError } from '@/utils/errorHandler'
import ChatMessage from '@/components/ChatMessage.vue'

const chatStore = useChatStore()
const sessionStore = useSessionStore()

const inputMessage = ref('')
const messagesContainer = ref(null)
const isConnected = ref(false)
const isSending = ref(false)

// 使用非流式模式
const USE_NON_STREAM = true

// 打字机效果函数
const typeOut = (text, speed = 20) => {
  return new Promise((resolve) => {
    let i = 0
    const timer = setInterval(() => {
      if (i < text.length) {
        chatStore.appendToLastMessage(text[i])
        i++
      } else {
        clearInterval(timer)
        chatStore.finishStreaming()
        resolve()
      }
    }, speed)
  })
}

// 监听消息变化，自动滚动到底部
watch(() => [chatStore.messages.length, sessionStore.chatHistory.length], () => {
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

// 处理回车键
const handleEnterKey = (event) => {
  if (event.shiftKey) {
    // Shift + Enter 换行
    return
  } else {
    // Enter 发送消息
    sendMessage()
  }
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  
  if (!message || chatStore.isLoading || isSending.value) {
    return
  }
  
  isSending.value = true

  // 先关闭之前的连接
  if (chatStore.currentEventSource) {
    chatStore.closeEventSource()
    isConnected.value = false
  }

  // 添加用户消息
  chatStore.addMessage('user', message)
  inputMessage.value = ''
  chatStore.setLoading(true)

  // 添加AI消息占位符
  chatStore.addMessage('ai', '', true)

  try {
    if (USE_NON_STREAM) {
      // 使用非流式API
      const chatFunction = sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' 
        ? travelAssistantChatOnce 
        : manusChatOnce

      const { chatId: returnedChatId, content } = await chatFunction(
        message,
        sessionStore.currentSessionId
      )

      // 更新chatId（如果后端返回了新的sessionId）
      if (returnedChatId && returnedChatId !== sessionStore.currentSessionId) {
        // 这里可以根据需要更新sessionStore或重新获取会话列表
        console.log('收到新的sessionId:', returnedChatId)
      }

      // 使用打字机效果显示AI回复
      await typeOut(content, 20)
      
    } else {
      // 保留原有的SSE流式处理逻辑
      const chatFunction = sessionStore.currentAgentType === 'TRAVEL_ASSISTANT' 
        ? travelAssistantChat 
        : manusChat

      const eventSource = chatFunction(
        message,
        sessionStore.currentSessionId,
        // onMessage - 接收流数据
        (chunk) => {
          chatStore.appendToLastMessage(chunk)
        },
        // onError - 错误处理
        (error, hasContent) => {
          console.error('聊天错误:', error)
          
          const { userMessage } = handleChatError(error, hasContent)
          
          if (hasContent) {
            chatStore.setLastMessageError(userMessage)
          } else {
            chatStore.updateLastMessage(userMessage)
            chatStore.setLastMessageError(userMessage)
          }
          
          chatStore.finishStreaming()
          isConnected.value = false
        },
        // onComplete - 正常完成
        () => {
          chatStore.finishStreaming()
          chatStore.closeEventSource()
          isConnected.value = false
        },
        // onChatId - 接收chatId
        (chatId) => {
          console.log('收到后端chatId:', chatId)
        }
      )

      chatStore.setEventSource(eventSource)
      isConnected.value = true
    }

  } catch (error) {
    console.error('发送消息失败:', error)
    const { userMessage } = handleChatError(error, false)
    chatStore.updateLastMessage(userMessage)
    chatStore.setLastMessageError(userMessage)
    chatStore.finishStreaming()
  } finally {
    chatStore.setLoading(false)
    isSending.value = false
    isConnected.value = false
  }
}

// 清空当前会话
const clearCurrentSession = async () => {
  if (sessionStore.currentSession && confirm('确定要清空当前会话的所有消息吗？')) {
    const result = await sessionStore.clearSession(sessionStore.currentSession.id)
    if (result.success) {
      chatStore.clearChat()
    } else {
      // 使用更友好的错误提示方式，而不是直接alert
      console.error('清空会话失败:', result.message)
      // 可以考虑使用toast通知或其他更优雅的方式
    }
  }
}

// 清理资源
onUnmounted(() => {
  chatStore.closeEventSource()
})
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
