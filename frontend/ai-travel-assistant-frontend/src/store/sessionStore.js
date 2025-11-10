import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getSessions, deleteSession, updateSessionTitle, clearSessionMessages, getChatHistory } from '@/api/session'

export const useSessionStore = defineStore('session', () => {
  // 状态
  const sessions = ref([])
  const currentSession = ref(null)
  const currentAgentType = ref('TRAVEL_ASSISTANT')
  const isLoading = ref(false)
  const chatHistory = ref([])

  // 计算属性
  const currentSessions = computed(() => sessions.value)
  const hasSessions = computed(() => sessions.value.length > 0)
  const currentSessionId = computed(() => currentSession.value?.id || null)

  // 动作
  function setSessions(sessionList) {
    sessions.value = sessionList
  }

  function setCurrentSession(session) {
    currentSession.value = session
  }

  function setCurrentAgentType(agentType) {
    currentAgentType.value = agentType
  }

  function setLoading(loading) {
    isLoading.value = loading
  }

  function setChatHistory(history) {
    chatHistory.value = history
  }

  // 获取会话列表
  async function fetchSessions(agentType = null) {
    try {
      setLoading(true)
      const type = agentType || currentAgentType.value
      const response = await getSessions(type)
      if (response.data.code === 0) {
        setSessions(response.data.data || [])
        return { success: true, sessions: response.data.data || [] }
      } else {
        return { success: false, message: response.data.message || '获取会话列表失败' }
      }
    } catch (error) {
      console.error('获取会话列表失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '获取会话列表失败，请稍后重试' 
      }
    } finally {
      setLoading(false)
    }
  }

  // 删除会话
  async function removeSession(sessionId) {
    try {
      setLoading(true)
      const response = await deleteSession(sessionId)
      if (response.data.code === 0) {
        // 从本地列表中移除
        sessions.value = sessions.value.filter(session => session.id !== sessionId)
        // 如果删除的是当前会话，清空当前会话
        if (currentSession.value?.id === sessionId) {
          setCurrentSession(null)
          setChatHistory([])
        }
        return { success: true, message: '删除成功' }
      } else {
        return { success: false, message: response.data.message || '删除失败' }
      }
    } catch (error) {
      console.error('删除会话失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '删除失败，请稍后重试' 
      }
    } finally {
      setLoading(false)
    }
  }

  // 更新会话标题
  async function renameSession(sessionId, newTitle) {
    try {
      setLoading(true)
      const response = await updateSessionTitle(sessionId, newTitle)
      if (response.data.code === 0) {
        // 更新本地列表中的标题
        const session = sessions.value.find(s => s.id === sessionId)
        if (session) {
          session.title = newTitle
        }
        // 如果更新的是当前会话，也更新当前会话
        if (currentSession.value?.id === sessionId) {
          currentSession.value.title = newTitle
        }
        return { success: true, message: '重命名成功' }
      } else {
        return { success: false, message: response.data.message || '重命名失败' }
      }
    } catch (error) {
      console.error('重命名会话失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '重命名失败，请稍后重试' 
      }
    } finally {
      setLoading(false)
    }
  }

  // 清空会话消息
  async function clearSession(sessionId) {
    try {
      setLoading(true)
      const response = await clearSessionMessages(sessionId)
      if (response.data.code === 0) {
        // 如果清空的是当前会话，清空聊天历史
        if (currentSession.value?.id === sessionId) {
          setChatHistory([])
        }
        return { success: true, message: '清空成功' }
      } else {
        return { success: false, message: response.data.message || '清空失败' }
      }
    } catch (error) {
      console.error('清空会话失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '清空失败，请稍后重试' 
      }
    } finally {
      setLoading(false)
    }
  }

  // 获取聊天历史
  async function fetchChatHistory(sessionId) {
    try {
      setLoading(true)
      const response = await getChatHistory(sessionId)
      if (response.data.code === 0) {
        setChatHistory(response.data.data || [])
        return { success: true, history: response.data.data || [] }
      } else {
        return { success: false, message: response.data.message || '获取聊天历史失败' }
      }
    } catch (error) {
      console.error('获取聊天历史失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '获取聊天历史失败，请稍后重试' 
      }
    } finally {
      setLoading(false)
    }
  }

  // 切换会话
  async function switchSession(session) {
    setCurrentSession(session)
    if (session) {
      await fetchChatHistory(session.id)
    } else {
      setChatHistory([])
    }
  }

  // 切换智能体类型
  async function switchAgentType(agentType) {
    setCurrentAgentType(agentType)
    setCurrentSession(null)
    setChatHistory([])
    await fetchSessions(agentType)
  }

  // 重置状态
  function resetStore() {
    sessions.value = []
    currentSession.value = null
    currentAgentType.value = 'TRAVEL_ASSISTANT'
    chatHistory.value = []
  }

  return {
    // 状态
    sessions,
    currentSession,
    currentAgentType,
    isLoading,
    chatHistory,
    
    // 计算属性
    currentSessions,
    hasSessions,
    currentSessionId,
    
    // 动作
    setSessions,
    setCurrentSession,
    setCurrentAgentType,
    setLoading,
    setChatHistory,
    fetchSessions,
    removeSession,
    renameSession,
    clearSession,
    fetchChatHistory,
    switchSession,
    switchAgentType,
    resetStore
  }
})
