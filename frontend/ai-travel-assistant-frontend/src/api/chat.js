import axios from 'axios'

// 配置基础URL
const API_BASE_URL = import.meta.env.PROD ? '/api' : 'http://localhost:8123/api'
const BASE_URL = `${API_BASE_URL}/ai`

// 创建axios实例
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// 全局连接管理
let globalEventSource = null
let globalConnectionId = null

/**
 * 关闭全局连接
 */
function closeGlobalConnection() {
  if (globalEventSource) {
    console.log('关闭全局SSE连接')
    globalEventSource.close()
    globalEventSource = null
    globalConnectionId = null
  }
}

/**
 * 创建SSE连接的通用函数
 * @param {string} url - SSE连接URL
 * @param {function} onMessage - 消息回调函数 (chunk) => void
 * @param {function} onError - 错误回调函数 (error, hasContent) => void
 * @param {function} onComplete - 完成回调函数 () => void
 * @returns {EventSource} EventSource实例，可用于关闭连接
 */
function createSSEConnection(url, onMessage, onError, onComplete) {
  // 先关闭之前的全局连接
  closeGlobalConnection()
  
  console.log('创建SSE连接:', url)
  const connectionId = Date.now() + Math.random()
  globalConnectionId = connectionId
  
  // 必须携带凭证以传递会话 Cookie（后端基于 Session 鉴权）
  const eventSource = new EventSource(url, { withCredentials: true })
  globalEventSource = eventSource
  
  let hasReceivedContent = false
  let isCompleted = false
  let hasConnected = false
  let connectionTimeout = null
  let lastDataChunk = null

  const complete = () => {
    if (!isCompleted && globalConnectionId === connectionId) {
      isCompleted = true
      if (connectionTimeout) {
        clearTimeout(connectionTimeout)
        connectionTimeout = null
      }
      onComplete && onComplete()
      closeGlobalConnection()
    }
  }

  const handleError = (error) => {
    if (!isCompleted && globalConnectionId === connectionId) {
      isCompleted = true
      if (connectionTimeout) {
        clearTimeout(connectionTimeout)
        connectionTimeout = null
      }
      onError && onError(error, hasReceivedContent)
      closeGlobalConnection()
    }
  }

  // 设置连接超时
  connectionTimeout = setTimeout(() => {
    if (!hasConnected && globalConnectionId === connectionId) {
      console.log('SSE连接超时')
      handleError(new Error('连接超时'))
    }
  }, 10000) // 10秒超时

  // 监听连接打开事件
  eventSource.onopen = function(event) {
    if (globalConnectionId === connectionId) {
      console.log('SSE连接已建立')
      hasConnected = true
      if (connectionTimeout) {
        clearTimeout(connectionTimeout)
        connectionTimeout = null
      }
    }
  }

  eventSource.onmessage = function(event) {
    if (globalConnectionId !== connectionId) {
      return // 不是当前连接，忽略
    }
    
    try {
      const raw = event.data ?? ''
      const text = String(raw).trim()
      const normalized = text.startsWith('data:') ? text.slice(5).trim() : text
      
      // 忽略并处理完成标记
      if (/^\[DONE\]$/i.test(normalized)) {
        console.log('收到结束标记，关闭连接')
        complete()
        return
      }
      
      // 处理错误消息（不展示到内容区）
      if (normalized.startsWith('ERROR:')) {
        const errMsg = normalized.slice(6).trim()
        handleError(new Error(errMsg || 'SSE错误'))
        return
      }
      
      if (normalized && normalized.trim()) {
        // 检查是否是chatId消息（格式：CHAT_ID:xxx）
        if (normalized.startsWith('CHAT_ID:')) {
          const chatId = normalized.substring(8).trim() // 移除 'CHAT_ID:' 前缀
          console.log('收到chatId:', chatId)
          if (onMessage && typeof onMessage === 'function') {
            onMessage('', chatId)
          }
          return // 不显示chatId信息给用户
        }
        
        // 简单去重：同一连接内如果连续收到完全相同的片段，忽略
        if (lastDataChunk !== normalized) {
          lastDataChunk = normalized
          hasReceivedContent = true
          onMessage && onMessage(normalized)
        }
      }
    } catch (error) {
      console.error('解析SSE消息失败:', error)
      if (!hasReceivedContent) {
        handleError(error)
      }
    }
  }
  
  eventSource.onerror = function(error) {
    if (globalConnectionId !== connectionId) {
      return // 不是当前连接，忽略
    }
    
    if (isCompleted) {
      return
    }
    
    if (eventSource.readyState === EventSource.CLOSED) {
      console.log('SSE连接正常关闭')
      if (hasReceivedContent) {
        complete()
      } else if (hasConnected) {
        // 如果已经连接过但没收到内容，可能是正常结束
        complete()
      } else {
        handleError(new Error('连接关闭但未接收到任何内容'))
      }
    } else if (eventSource.readyState === EventSource.CONNECTING) {
      console.log('SSE连接中...')
      // 如果已经接收过内容，再次进入CONNECTING 说明服务器已结束，我们主动关闭以避免浏览器自动重连
      if (hasReceivedContent) {
        complete()
      }
    } else {
      console.error('SSE连接错误:', error)
      handleError(error)
    }
  }
  
  return eventSource
}
/**
 * 旅行助手SSE聊天接口
 * @param {string} message - 用户消息
 * @param {string} chatId - 聊天ID（可选，首次调用时不传，后续使用后端返回的chatId）
 * @param {function} onMessage - 接收消息的回调函数 (chunk, chatId) => void
 * @param {function} onError - 错误处理回调函数 (error, hasContent) => void
 * @param {function} onComplete - 完成回调函数
 * @param {function} onChatId - 接收chatId的回调函数 (chatId) => void
 * @returns {EventSource} EventSource实例，可用于关闭连接
 */
export function travelAssistantChat(message, chatId, onMessage, onError, onComplete, onChatId) {
  // 构建URL，如果chatId存在则添加，否则不传chatId参数
  let url = `${BASE_URL}/travel_app/chat/sse?message=${encodeURIComponent(message)}`
  if (chatId) {
    url += `&chatId=${encodeURIComponent(chatId)}`
  }
  const wrappedOnMessage = (chunk, receivedChatId) => {
    if (receivedChatId && onChatId) {
      onChatId(receivedChatId)
    } else if (chunk) {
      onMessage(chunk)
    }
  }
  return createSSEConnection(url, wrappedOnMessage, onError, onComplete)
}

/**
 * Manus智能体SSE聊天接口
 * @param {string} message - 用户消息
 * @param {string} chatId - 聊天ID（可选，首次调用时不传，后续使用后端返回的chatId）
 * @param {function} onMessage - 接收消息的回调函数 (chunk, chatId) => void
 * @param {function} onError - 错误处理回调函数 (error, hasContent) => void
 * @param {function} onComplete - 完成回调函数
 * @param {function} onChatId - 接收chatId的回调函数 (chatId) => void
 * @returns {EventSource} EventSource实例，可用于关闭连接
 */
export function manusChat(message, chatId, onMessage, onError, onComplete, onChatId) {
  // 构建URL，如果chatId存在则添加，否则不传chatId参数
  let url = `${BASE_URL}/manus/chat?message=${encodeURIComponent(message)}`
  if (chatId) {
    url += `&chatId=${encodeURIComponent(chatId)}`
  }
  const wrappedOnMessage = (chunk, receivedChatId) => {
    if (receivedChatId && onChatId) {
      onChatId(receivedChatId)
    } else if (chunk) {
      onMessage(chunk)
    }
  }
  return createSSEConnection(url, wrappedOnMessage, onError, onComplete)
}

/**
 * 关闭SSE连接
 * @param {EventSource} eventSource - EventSource实例（可选，如果不传则关闭全局连接）
 */
export function closeSSEConnection(eventSource) {
  if (eventSource && eventSource.readyState !== EventSource.CLOSED) {
    eventSource.close()
  }
  // 同时关闭全局连接
  closeGlobalConnection()
}

/**
 * 解析后端非流式字符串响应（兼容 CHAT_ID / data: 格式）
 * @param {string} raw - 后端返回的原始字符串
 * @returns {Object} { chatId, content }
 */
function parseNonStreamPayload(raw) {
  const text = String(raw ?? '')
  let chatId = ''
  let content = text

  // 提取 chatId（格式：CHAT_IDxxx）
  const cidMatch = text.match(/CHAT_ID(\d+)/i)
  if (cidMatch) {
    chatId = cidMatch[1].trim()
  }

  // 提取 data 内容（格式：data: xxx）
  // 修改正则表达式，使其能正确匹配包含换行符的内容
  const dataMatch = text.match(/data:\s*([\s\S]*?)(?:\[DONE\]|$)/i)
  if (dataMatch) {
    content = dataMatch[1].trim()
  } else {
    // 兜底：移除 CHAT_ID 和其他标记后返回内容
    content = text
      .replace(/CHAT_ID\d+/gi, '')
      .replace(/data:\s*/gi, '')
      .replace(/\[DONE\]/gi, '')
      .trim()
  }

  return { chatId, content }
}

/**
 * 非流式（一次性）聊天接口：旅行助手
 * @param {string} message - 用户消息
 * @param {string} chatId - 聊天ID（可选）
 * @returns {Promise<Object>} { chatId, content }
 */
export async function travelAssistantChatOnce(message, chatId) {
  const params = { message }
  if (chatId) params.chatId = chatId

  const response = await apiClient.get('/travel_app/chat', { 
    params,
    withCredentials: true,
    timeout: 180000 // 调整超时时间到3分钟
  })
  if (response.data.code === 0) {
    const raw = response.data.data
    return parseNonStreamPayload(raw)
  } else {
    throw new Error(response.data.message || '请求失败')
  }
}

/**
 * 非流式（一次性）聊天接口：Manus
 * @param {string} message - 用户消息
 * @param {string} chatId - 聊天ID（可选）
 * @returns {Promise<Object>} { chatId, content }
 */
export async function manusChatOnce(message, chatId) {
  const params = { message }
  if (chatId) params.chatId = chatId

  const response = await apiClient.get('/manus/chat', { 
    params,
    withCredentials: true,
    timeout: 180000 // 调整超时时间到3分钟
  })
  if (response.data.code === 0) {
    const raw = response.data.data
    return parseNonStreamPayload(raw)
  } else {
    throw new Error(response.data.message || '请求失败')
  }
}

export default {
  travelAssistantChat,
  manusChat,
  closeSSEConnection,
  // 新增非流式接口
  travelAssistantChatOnce,
  manusChatOnce
}