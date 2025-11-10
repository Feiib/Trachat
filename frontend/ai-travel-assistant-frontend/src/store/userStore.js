import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCurrentUser, register } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref(null)
  const isLoggedIn = ref(false)
  const isLoading = ref(false)

  // 计算属性
  const userInfo = computed(() => user.value)
  const isAuthenticated = computed(() => isLoggedIn.value && user.value !== null)

  // 动作
  function setUser(userData) {
    user.value = userData
    isLoggedIn.value = true
  }

  function clearUser() {
    user.value = null
    isLoggedIn.value = false
  }

  function setLoading(loading) {
    isLoading.value = loading
  }

  // 用户注册
  async function userRegister(username, password) {
    try {
      setLoading(true)
      const response = await register(username, password)
      if (response.data.code === 0) {
        return { success: true, message: '注册成功' }
      } else {
        return { success: false, message: response.data.message || '注册失败' }
      }
    } catch (error) {
      console.error('注册失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '注册失败，请稍后重试' 
      }
    } finally {
      setLoading(false)
    }
  }

  // 用户登录
  async function userLogin(username, password) {
    try {
      setLoading(true)
      const response = await login(username, password)
      if (response.data.code === 0) {
        // 登录成功后获取用户信息
        const meResult = await fetchCurrentUser()
        if (meResult.success) {
          return { success: true, message: '登录成功' }
        } else {
          // 如果无法获取用户信息，则视为登录失败
          clearUser()
          return { success: false, message: meResult.message || '登录失败，无法获取用户信息' }
        }
      } else {
        return { success: false, message: response.data.message || '用户名或密码错误' }
      }
    } catch (error) {
      console.error('登录失败:', error)
      const status = error.response?.status
      let message = error.response?.data?.message

      if (!message) {
        if (status === 401 || status === 403) {
          message = '用户名或密码错误'
        } else if (status >= 500) {
          message = '服务器错误，请稍后重试'
        } else if (error.code === 'ECONNABORTED') {
          message = '网络超时，请稍后重试'
        } else if (!error.response) {
          message = '网络异常，请检查连接'
        } else {
          message = '登录失败，请检查用户名和密码'
        }
      }

      return { success: false, message }
    } finally {
      setLoading(false)
    }
  }

  // 用户退出登录
  async function userLogout() {
    try {
      setLoading(true)
      await logout()
      clearUser()
      return { success: true, message: '退出登录成功' }
    } catch (error) {
      console.error('退出登录失败:', error)
      // 即使API调用失败，也清除本地状态
      clearUser()
      return { success: true, message: '已退出登录' }
    } finally {
      setLoading(false)
    }
  }

  // 获取当前用户信息（兼容后端返回 BaseResponse 或直接用户对象）
  async function fetchCurrentUser() {
    try {
      const response = await getCurrentUser()
      // 如果是 BaseResponse 格式
      if (response.data && typeof response.data === 'object' && 'code' in response.data) {
        if (response.data.code === 0) {
          const userData = response.data.data
          if (userData) {
            setUser(userData)
            return { success: true, user: userData }
          }
        }
        clearUser()
        return { success: false, message: response.data.message || '用户未登录' }
      }

      // 如果直接是用户对象
      if (response.data && response.data.id) {
        setUser(response.data)
        return { success: true, user: response.data }
      } else {
        clearUser()
        return { success: false, message: '用户未登录' }
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      clearUser()
      return { success: false, message: '用户未登录' }
    }
  }

  // 检查登录状态
  async function checkAuthStatus() {
    try {
      const result = await fetchCurrentUser()
      return result.success
    } catch (error) {
      console.error('检查登录状态失败:', error)
      return false
    }
  }

  return {
    // 状态
    user,
    isLoggedIn,
    isLoading,
    
    // 计算属性
    userInfo,
    isAuthenticated,
    
    // 动作
    setUser,
    clearUser,
    setLoading,
    userRegister,
    userLogin,
    userLogout,
    fetchCurrentUser,
    checkAuthStatus
  }
})
