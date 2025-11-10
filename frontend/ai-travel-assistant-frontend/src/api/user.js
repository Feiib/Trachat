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
 * 用户注册
 * @param {string} username - 用户名
 * @param {string} password - 密码
 * @returns {Promise} 注册结果
 */
export function register(username, password) {
  return apiClient.post('/user/register', null, {
    params: { username, password }
  })
}

/**
 * 用户登录
 * @param {string} username - 用户名
 * @param {string} password - 密码
 * @returns {Promise} 登录结果
 */
export function login(username, password) {
  return apiClient.post('/user/login', null, {
    params: { username, password }
  })
}

/**
 * 用户退出登录
 * @returns {Promise} 退出结果
 */
export function logout() {
  return apiClient.get('/user/logout')
}

/**
 * 获取当前用户信息
 * @returns {Promise} 用户信息
 */
export function getCurrentUser() {
  return apiClient.get('/user/me')
}

export default {
  register,
  login,
  logout,
  getCurrentUser
}
