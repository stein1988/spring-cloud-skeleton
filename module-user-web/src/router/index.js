import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'departments',
        name: 'Departments',
        component: () => import('../views/DepartmentManagement.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('../views/RoleManagement.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'permissions',
        name: 'Permissions',
        component: () => import('../views/PermissionManagement.vue'),
        meta: { title: '权限管理' }
      },
      {
        path: 'tenants',
        name: 'Tenants',
        component: () => import('../views/TenantManagement.vue'),
        meta: { title: '租户管理' }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('../views/SystemManagement.vue'),
        meta: { title: '系统管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuth = authStore.isAuthenticated()

  if (to.path === '/login') {
    if (isAuth) {
      next('/')
    } else {
      next()
    }
  } else if (to.meta.requiresAuth !== false && !isAuth) {
    next('/login')
  } else {
    next()
  }
})

export default router
