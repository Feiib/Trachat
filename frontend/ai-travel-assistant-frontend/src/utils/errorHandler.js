/**
 * 错误处理工具类
 * 将技术异常转换为用户友好的提示信息
 */

/**
 * 将错误对象转换为用户友好的消息
 * @param {Error|Object} error - 错误对象
 * @param {string} context - 错误上下文（如'登录'、'发送消息'等）
 * @returns {string} 用户友好的错误消息
 */
export function formatErrorMessage(error, context = '操作') {
  // 如果已经是用户友好的消息，直接返回
  if (typeof error === 'string') {
    return error
  }

  // 获取错误信息
  const errorMessage = error?.message || error?.toString() || ''
  const status = error?.response?.status || error?.status
  const responseData = error?.response?.data

  // 网络相关错误
  if (error?.code === 'ECONNABORTED' || errorMessage.includes('timeout')) {
    return `${context}超时，请检查网络连接后重试`
  }

  if (error?.code === 'NETWORK_ERROR' || errorMessage.includes('Network Error')) {
    return `网络连接异常，请检查网络后重试`
  }

  if (!error?.response && errorMessage.includes('fetch')) {
    return `网络请求失败，请检查网络连接`
  }

  // HTTP状态码错误
  if (status) {
    switch (status) {
      case 400:
        return responseData?.message || `请求参数错误，请检查输入内容`
      case 401:
        return `身份验证失败，请重新登录`
      case 403:
        return `权限不足，无法执行此操作`
      case 404:
        return `请求的资源不存在，请稍后重试`
      case 429:
        return `请求过于频繁，请稍后再试`
      case 500:
        return `服务器内部错误，请稍后重试`
      case 502:
      case 503:
      case 504:
        return `服务暂时不可用，请稍后重试`
      default:
        if (status >= 400 && status < 500) {
          return responseData?.message || `请求失败，请检查输入内容`
        } else if (status >= 500) {
          return `服务器错误，请稍后重试`
        }
    }
  }

  // 特定错误消息处理
  const lowerMessage = errorMessage.toLowerCase()
  
  if (lowerMessage.includes('connection reset') || lowerMessage.includes('connection refused')) {
    return `连接被重置，请稍后重试`
  }

  if (lowerMessage.includes('cors')) {
    return `跨域请求被阻止，请联系管理员`
  }

  if (lowerMessage.includes('parse') || lowerMessage.includes('json')) {
    return `数据格式错误，请稍后重试`
  }

  if (lowerMessage.includes('unauthorized')) {
    return `身份验证失败，请重新登录`
  }

  if (lowerMessage.includes('forbidden')) {
    return `权限不足，无法执行此操作`
  }

  // 聊天相关的特定错误
  if (context === '发送消息') {
    if (lowerMessage.includes('sse') || lowerMessage.includes('eventsource')) {
      return `连接中断，消息发送失败，请重试`
    }
    if (lowerMessage.includes('stream')) {
      return `数据传输异常，请重新发送消息`
    }
  }

  // 如果有后端返回的具体错误消息，优先使用
  if (responseData?.message && typeof responseData.message === 'string') {
    return responseData.message
  }

  // 默认错误消息
  return `${context}失败，请稍后重试`
}

/**
 * 聊天错误处理
 * @param {Error} error - 错误对象
 * @param {boolean} hasContent - 是否已有部分内容
 * @returns {Object} { userMessage, logMessage }
 */
export function handleChatError(error, hasContent = false) {
  const logMessage = error?.message || error?.toString() || '未知错误'
  
  let userMessage
  if (hasContent) {
    userMessage = '网络连接不稳定，消息可能不完整'
  } else {
    userMessage = formatErrorMessage(error, '发送消息')
  }

  return {
    userMessage,
    logMessage
  }
}

/**
 * 登录错误处理
 * @param {Error} error - 错误对象
 * @returns {string} 用户友好的错误消息
 */
export function handleLoginError(error) {
  const status = error?.response?.status
  const responseMessage = error?.response?.data?.message

  if (status === 401 || status === 403) {
    return '用户名或密码错误'
  }

  if (responseMessage) {
    return responseMessage
  }

  return formatErrorMessage(error, '登录')
}

/**
 * 会话操作错误处理
 * @param {Error} error - 错误对象
 * @param {string} operation - 操作类型（如'获取会话列表'、'删除会话'等）
 * @returns {string} 用户友好的错误消息
 */
export function handleSessionError(error, operation) {
  return formatErrorMessage(error, operation)
}

export default {
  formatErrorMessage,
  handleChatError,
  handleLoginError,
  handleSessionError
}