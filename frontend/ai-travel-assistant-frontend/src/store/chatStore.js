import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { v4 as uuidv4 } from 'uuid'

export const useChatStore = defineStore('chat', () => {
  // 状态
  const messages = ref([])
  const isLoading = ref(false)
  const currentChatId = ref('')
  const currentEventSource = ref(null)
  const chatType = ref('') // 'travel' 或 'manus'
  
  // 计算属性
  const hasMessages = computed(() => messages.value.length > 0)
  const lastMessage = computed(() => messages.value[messages.value.length - 1])
  
  // 动作
  function initializeChat(type) {
    chatType.value = type
    currentChatId.value = '' // 不再前端生成chatId，由后端管理
    messages.value = []
    isLoading.value = false
    
    // 添加欢迎消息
    const welcomeMessage = type === 'travel' 
      ? '您好！我是您的旅行助手，适合景点推荐、美食推荐、天气查询等快捷问答。请告诉我您的目的地或问题？'
      : '您好！我是 Travel Manus，采用 ReAct 模式，擅长复杂多步骤的行程规划与旅行计划，支持火车余票查询和天气查询。请告诉我您的目的地、出行计划，城市天气？'
    
    addMessage('ai', welcomeMessage)
  }
  
  function addMessage(role, content, isStreaming = false) {
    const message = {
      id: uuidv4(),
      role, // 'user' 或 'ai'
      content,
      timestamp: new Date(),
      isStreaming,
      status: isStreaming ? 'sending' : 'done', // 'sending', 'done', 'error'
      hasError: false, // 是否有错误（但不覆盖内容）
      errorMessage: '' // 错误信息
    }
    
    messages.value.push(message)
    return message
  }
  
  function updateLastMessage(content) {
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      lastMsg.content = content
    }
  }
  
  function appendToLastMessage(chunk) {
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      lastMsg.content += chunk
    }
  }
  
  function finishStreaming() {
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      lastMsg.isStreaming = false
      lastMsg.status = 'done' // 标记为完成状态
    }
    isLoading.value = false
  }
  
  // 设置消息错误状态（不覆盖已有内容）
  function setMessageError(messageId, errorMessage) {
    const message = messages.value.find(msg => msg.id === messageId)
    if (message) {
      message.hasError = true
      message.errorMessage = errorMessage
      message.status = 'error'
      message.isStreaming = false
    }
  }
  
  // 设置最后一条消息的错误状态
  function setLastMessageError(errorMessage) {
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      lastMsg.hasError = true
      lastMsg.errorMessage = errorMessage
      lastMsg.status = 'error'
      lastMsg.isStreaming = false
    }
  }
  
  // 清除消息错误状态
  function clearMessageError(messageId) {
    const message = messages.value.find(msg => msg.id === messageId)
    if (message) {
      message.hasError = false
      message.errorMessage = ''
      message.status = message.content ? 'done' : 'sending'
    }
  }
  
  function setLoading(loading) {
    isLoading.value = loading
  }
  
  function setEventSource(eventSource) {
    currentEventSource.value = eventSource
  }
  
  // 设置从后端获取的chatId
  function setChatId(chatId) {
    currentChatId.value = chatId
  }
  
  function closeEventSource() {
    if (currentEventSource.value) {
      currentEventSource.value.close()
      currentEventSource.value = null
    }
  }
  
  function clearChat() {
    messages.value = []
    isLoading.value = false
    closeEventSource()
  }
  
  function resetStore() {
    messages.value = []
    isLoading.value = false
    currentChatId.value = ''
    chatType.value = ''
    closeEventSource()
  }
  
  return {
    // 状态
    messages,
    isLoading,
    currentChatId,
    currentEventSource,
    chatType,
    
    // 计算属性
    hasMessages,
    lastMessage,
    
    // 动作
    initializeChat,
    addMessage,
    updateLastMessage,
    appendToLastMessage,
    finishStreaming,
    setMessageError,
    setLastMessageError,
    clearMessageError,
    setLoading,
    setEventSource,
    setChatId,
    closeEventSource,
    clearChat,
    resetStore
  }
})