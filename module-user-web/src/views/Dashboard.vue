<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">用户总数</p>
              <h2 class="stat-value">{{ stats.userCount }}</h2>
            </div>
            <el-icon :size="48" color="#409EFF"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">部门总数</p>
              <h2 class="stat-value">{{ stats.departmentCount }}</h2>
            </div>
            <el-icon :size="48" color="#67C23A"><OfficeBuilding /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">角色总数</p>
              <h2 class="stat-value">{{ stats.roleCount }}</h2>
            </div>
            <el-icon :size="48" color="#E6A23C"><Stamp /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">租户总数</p>
              <h2 class="stat-value">{{ stats.tenantCount }}</h2>
            </div>
            <el-icon :size="48" color="#F56C6C"><School /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/users')">
              <el-icon><UserFilled /></el-icon> 用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/departments')">
              <el-icon><OfficeBuilding /></el-icon> 部门管理
            </el-button>
            <el-button type="warning" @click="$router.push('/roles')">
              <el-icon><Stamp /></el-icon> 角色管理
            </el-button>
            <el-button type="danger" @click="$router.push('/tenants')">
              <el-icon><School /></el-icon> 租户管理
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">Lonbon Cloud 用户管理系统</el-descriptions-item>
            <el-descriptions-item label="版本号">1.0.0</el-descriptions-item>
            <el-descriptions-item label="技术栈">Vue 3 + Element Plus + Vite</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Cloud</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { userApi, departmentApi, roleApi, tenantApi } from '../api'

const stats = reactive({
  userCount: 0,
  departmentCount: 0,
  roleCount: 0,
  tenantCount: 0
})

onMounted(async () => {
  try {
    const [users, departments, roles, tenants] = await Promise.allSettled([
      userApi.getAll(),
      departmentApi.getAll(),
      roleApi.getAll(),
      tenantApi.getAll()
    ])
    if (users.status === 'fulfilled') stats.userCount = users.value.data?.length || 0
    if (departments.status === 'fulfilled') stats.departmentCount = departments.value.data?.length || 0
    if (roles.status === 'fulfilled') stats.roleCount = roles.value.data?.length || 0
    if (tenants.status === 'fulfilled') stats.tenantCount = tenants.value.data?.length || 0
  } catch (e) {
    console.error('Failed to load stats:', e)
  }
})
</script>

<style scoped>
.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  color: #999;
  font-size: 14px;
  margin: 0 0 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
