import axios from 'axios'

// 配置基础URL
const BASE_URL = import.meta.env.PROD ? '/api' : 'http://localhost:8123/api'

// 创建axios实例
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  withCredentials: true, // 使用session
  headers: {
    'Content-Type': 'application/json',
  }
})

/**
 * 获取会话列表
 * @param {string} agentType - 智能体类型 (TRAVEL_ASSISTANT | MANUS_AGENT)
 * @param {number} page - 页码，默认1
 * @param {number} size - 每页大小，默认20
 * @returns {Promise} 会话列表
 */
export function getSessions(agentType, page = 1, size = 20) {
  return apiClient.get('/chat/sessions', {
    params: { agentType, page, size }
  })
}

/**
 * 删除会话
 * @param {number} sessionId - 会话ID
 * @returns {Promise} 删除结果
 */
export function deleteSession(sessionId) {
  return apiClient.delete('/chat/session', {
    params: { sessionId }
  })
}

/**
 * 更新会话标题
 * @param {number} sessionId - 会话ID
 * @param {string} title - 新标题
 * @returns {Promise} 更新结果
 */
export function updateSessionTitle(sessionId, title) {
  return apiClient.put('/chat/session/title', null, {
    params: { sessionId, title }
  })
}

/**
 * 清空会话消息
 * @param {number} sessionId - 会话ID
 * @returns {Promise} 清空结果
 */
export function clearSessionMessages(sessionId) {
  return apiClient.delete('/chat/session/messages', {
    params: { sessionId }
  })
}

/**
 * 获取聊天历史记录
 * @param {number} sessionId - 会话ID
 * @returns {Promise} 聊天历史
 */
export function getChatHistory(sessionId) {
  return apiClient.get('/chat/history', {
    params: { sessionId }
  })
}

export default {
  getSessions,
  deleteSession,
  updateSessionTitle,
  clearSessionMessages,
  getChatHistory
}
