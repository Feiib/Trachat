<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900">
    <!-- 头部 -->
    <header class="pt-16 pb-8">
      <div class="container mx-auto px-6 text-center">
        <!-- 顶部右侧：登录状态/头像 -->
        <div class="flex justify-end mb-8">
          <!-- 已登录：显示头像与用户名 -->
          <div v-if="userStore.isAuthenticated" class="flex items-center space-x-4">
            <div class="w-10 h-10 rounded-full bg-gradient-to-r from-green-400 to-blue-500 flex items-center justify-center text-white font-semibold">
              {{ (userStore.userInfo?.username || 'U').charAt(0).toUpperCase() }}
            </div>
            <span class="text-gray-700 dark:text-gray-300">{{ userStore.userInfo?.username }}</span>
            <router-link 
              to="/chat"
              class="px-3 py-2 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-200 transition-colors"
            >
              进入聊天
            </router-link>
          </div>
          
          <!-- 未登录：显示登录与注册按钮 -->
          <div v-else class="flex space-x-4">
            <router-link 
              to="/login"
              class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white transition-colors"
            >
              登录
            </router-link>
            <router-link 
              to="/register"
              class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
              注册
            </router-link>
          </div>
        </div>
        
        <h1 class="text-5xl font-bold text-gray-800 dark:text-white mb-4 animate-fade-in">
          AI 旅行智能助手
        </h1>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-2xl mx-auto animate-slide-up">
          探索世界，让AI成为您最贴心的旅行伙伴
        </p>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="container mx-auto px-6 pb-16">
      <div class="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
        
        <!-- 旅行助手卡片 -->
        <div 
          @click="navigateToChat('travel')"
          class="group relative bg-white dark:bg-gray-800 rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 cursor-pointer transform hover:scale-105 hover:-translate-y-2 animate-fade-in"
          style="animation-delay: 0.2s"
        >
          <div class="absolute inset-0 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-2xl opacity-0 group-hover:opacity-10 transition-opacity duration-300"></div>
          
          <div class="relative p-8">
            <!-- 图标 -->
            <div class="w-16 h-16 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-2xl flex items-center justify-center mb-6 group-hover:animate-bounce-gentle">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
              </svg>
            </div>
            
            <!-- 标题和描述 -->
            <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-4 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
              旅行助手
            </h2>
            <p class="text-gray-600 dark:text-gray-300 mb-6 leading-relaxed">
              适合景点推荐、美食推荐、天气查询等快捷问答，快速获取目的地信息与实用建议。
            </p>
            
            <!-- 特性列表 -->
            <ul class="space-y-2 mb-6">
              <li class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                </svg>
                景点推荐
              </li>
              <li class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                </svg>
                美食推荐
              </li>
              <li class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                </svg>
                天气查询
              </li>
            </ul>
            
            <!-- 按钮 -->
            <div class="flex items-center text-blue-600 dark:text-blue-400 font-semibold group-hover:text-blue-700 dark:group-hover:text-blue-300 transition-colors">
              开始对话
              <svg class="w-5 h-5 ml-2 transform group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"></path>
              </svg>
            </div>
          </div>
        </div>

        <!-- Manus 智能体卡片 -->
        <div 
          @click="navigateToChat('manus')"
          class="group relative bg-white dark:bg-gray-800 rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 cursor-pointer transform hover:scale-105 hover:-translate-y-2 animate-fade-in"
          style="animation-delay: 0.4s"
        >
          <div class="absolute inset-0 bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl opacity-0 group-hover:opacity-10 transition-opacity duration-300"></div>
          
          <div class="relative p-8">
            <!-- 图标 -->
            <div class="w-16 h-16 bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl flex items-center justify-center mb-6 group-hover:animate-bounce-gentle">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
            
            <!-- 标题和描述 -->
            <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-4 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors">
              Travel Manus
            </h2>
            <p class="text-gray-600 dark:text-gray-300 mb-6 leading-relaxed">
              采用 ReAct 模式，擅长复杂多步骤的行程规划与旅行计划，支持路线优化与任务编排。
            </p>
            
            <!-- 特性列表 -->
            <ul class="space-y-2 mb-6">
              <li class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                </svg>
                复杂行程规划
              </li>
              <li class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                </svg>
                多步骤推理与优化
              </li>
              <li class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                </svg>
                计划编排与综合建议
              </li>
            </ul>
            
            <!-- 按钮 -->
            <div class="flex items-center text-purple-600 dark:text-purple-400 font-semibold group-hover:text-purple-700 dark:group-hover:text-purple-300 transition-colors">
              开始对话
              <svg class="w-5 h-5 ml-2 transform group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部信息 -->
      <div class="text-center mt-16 animate-fade-in" style="animation-delay: 0.6s">
        <p class="text-gray-500 dark:text-gray-400 text-sm">
          选择您需要的AI助手，开始智能对话体验
        </p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore'

const router = useRouter()
const userStore = useUserStore()

// 首次进入主页时，尝试从后端会话拉取用户信息，保证登录状态
onMounted(() => {
  userStore.fetchCurrentUser()
})

const navigateToChat = (type) => {
  // 登录用户进入聊天；未登录跳转到登录
  if (userStore.isAuthenticated) {
    router.push('/chat')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out forwards;
}

.animate-slide-up {
  animation: slideUp 0.8s ease-out 0.3s forwards;
  opacity: 0;
}
</style>