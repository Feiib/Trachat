import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Chat from '@/views/Chat.vue'
import TravelAssistant from '@/views/TravelAssistant.vue'
import TravelManus from '@/views/TravelManus.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: 'AI 旅行智能助手'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录 - AI 旅行智能助手',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      title: '注册 - AI 旅行智能助手',
      requiresAuth: false
    }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat,
    meta: {
      title: '聊天 - AI 旅行智能助手',
      requiresAuth: true
    }
  },
  {
    path: '/travel-assistant',
    name: 'TravelAssistant',
    component: TravelAssistant,
    meta: {
      title: '旅行助手对话',
      requiresAuth: true
    }
  },
  {
    path: '/travel-manus',
    name: 'TravelManus',
    component: TravelManus,
    meta: {
      title: 'Travel Manus - 行程与旅行相关对话',
      requiresAuth: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 设置页面标题和认证检查
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 动态导入用户store
    const { useUserStore } = await import('@/store/userStore')
    const userStore = useUserStore()
    
    // 检查登录状态
    const isAuthenticated = await userStore.checkAuthStatus()
    if (!isAuthenticated) {
      // 未登录，跳转到登录页面
      next('/login')
      return
    }
  }
  
  // 如果已登录用户访问登录/注册页面，重定向到聊天页面
  if ((to.name === 'Login' || to.name === 'Register') && to.meta.requiresAuth === false) {
    const { useUserStore } = await import('@/store/userStore')
    const userStore = useUserStore()
    const isAuthenticated = await userStore.checkAuthStatus()
    if (isAuthenticated) {
      next('/chat')
      return
    }
  }
  
  next()
})

export default router