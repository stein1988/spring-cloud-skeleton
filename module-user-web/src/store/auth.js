import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '../api'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const login = async (credentials) => {
    try {
      const loginData = {
        username: credentials.username,
        passwordCipher: credentials.password
      }
      const response = await authApi.login(loginData)
      if (response && response.data) {
        accessToken.value = response.data.accessToken
        refreshToken.value = response.data.refreshToken
        userInfo.value = {
          id: response.data.userId,
          username: credentials.username
        }
        localStorage.setItem('accessToken', accessToken.value)
        localStorage.setItem('refreshToken', refreshToken.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        return true
      }
      return false
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }

  const logout = () => {
    accessToken.value = ''
    refreshToken.value = ''
    userInfo.value = {}
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }

  const isAuthenticated = () => {
    return !!accessToken.value
  }

  return {
    accessToken,
    refreshToken,
    userInfo,
    login,
    logout,
    isAuthenticated
  }
})
