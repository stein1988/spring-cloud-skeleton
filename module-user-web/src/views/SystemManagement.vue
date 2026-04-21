<template>
  <div class="system-management">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据库同步</span>
              <el-tag type="warning">谨慎操作</el-tag>
            </div>
          </template>
          <el-alert
            title="此操作将同步数据库表结构（Code First），请确保在开发环境中使用。"
            type="warning"
            :closable="false"
            show-icon
            style="margin-bottom: 20px"
          />
          <el-button
            type="danger"
            :loading="syncLoading"
            @click="handleSyncTable"
          >
            <el-icon><Refresh /></el-icon> 同步数据库表
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">Lonbon Cloud</el-descriptions-item>
            <el-descriptions-item label="模块">module-user</el-descriptions-item>
            <el-descriptions-item label="版本">1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot / Spring Cloud</el-descriptions-item>
            <el-descriptions-item label="ORM">easy-query</el-descriptions-item>
            <el-descriptions-item label="认证">Sa-Token + JWT</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>API 接口概览</span>
          </template>
          <el-table :data="apiList" stripe border>
            <el-table-column prop="module" label="模块" width="120" />
            <el-table-column prop="path" label="路径" width="250" />
            <el-table-column prop="method" label="方法" width="100">
              <template #default="{ row }">
                <el-tag :type="getMethodTag(row.method)" size="small">{{ row.method }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { systemApi } from '../api'

const syncLoading = ref(false)

const apiList = ref([
  { module: '鉴权', path: '/auth/login', method: 'POST', description: '用户登录' },
  { module: '鉴权', path: '/auth/refresh-token', method: 'POST', description: '刷新Token' },
  { module: '鉴权', path: '/auth/public-key', method: 'GET', description: '获取公钥' },
  { module: '用户', path: '/api/users', method: 'GET', description: '查询所有用户' },
  { module: '用户', path: '/api/users', method: 'POST', description: '创建用户' },
  { module: '用户', path: '/api/users/{id}', method: 'GET', description: '获取用户详情' },
  { module: '用户', path: '/api/users/{id}/update', method: 'POST', description: '更新用户' },
  { module: '用户', path: '/api/users/{id}/delete', method: 'POST', description: '删除用户' },
  { module: '部门', path: '/api/departments', method: 'GET', description: '查询所有部门' },
  { module: '部门', path: '/api/departments', method: 'POST', description: '创建部门' },
  { module: '部门', path: '/api/departments/{id}', method: 'GET', description: '获取部门详情' },
  { module: '部门', path: '/api/departments/{id}/update', method: 'POST', description: '更新部门' },
  { module: '部门', path: '/api/departments/{id}/delete', method: 'POST', description: '删除部门' },
  { module: '部门', path: '/api/departments/{id}/children', method: 'GET', description: '获取子部门' },
  { module: '部门', path: '/api/departments/{id}/descendants', method: 'GET', description: '获取后代部门' },
  { module: '部门', path: '/api/departments/{id}/parent', method: 'GET', description: '获取父部门' },
  { module: '部门', path: '/api/departments/{id}/ancestors', method: 'GET', description: '获取祖先部门' },
  { module: '部门', path: '/api/departments/{id}/move', method: 'POST', description: '移动部门' },
  { module: '部门', path: '/api/departments/{id}/tree', method: 'GET', description: '获取部门树' },
  { module: '角色', path: '/api/roles', method: 'GET', description: '查询所有角色' },
  { module: '角色', path: '/api/roles', method: 'POST', description: '创建角色' },
  { module: '角色', path: '/api/roles/{id}', method: 'GET', description: '获取角色详情' },
  { module: '角色', path: '/api/roles/{id}/update', method: 'POST', description: '更新角色' },
  { module: '角色', path: '/api/roles/{id}/delete', method: 'POST', description: '删除角色' },
  { module: '权限', path: '/api/permissions', method: 'GET', description: '查询所有权限' },
  { module: '权限', path: '/api/permissions', method: 'POST', description: '创建权限' },
  { module: '权限', path: '/api/permissions/{id}', method: 'GET', description: '获取权限详情' },
  { module: '权限', path: '/api/permissions/{id}/update', method: 'POST', description: '更新权限' },
  { module: '权限', path: '/api/permissions/{id}/delete', method: 'POST', description: '删除权限' },
  { module: '租户', path: '/api/tenants', method: 'GET', description: '分页查询租户' },
  { module: '租户', path: '/api/tenants', method: 'POST', description: '创建租户' },
  { module: '租户', path: '/api/tenants/{id}', method: 'GET', description: '获取租户详情' },
  { module: '租户', path: '/api/tenants/{id}/update', method: 'POST', description: '更新租户' },
  { module: '租户', path: '/api/tenants/{id}/delete', method: 'POST', description: '删除租户' },
  { module: '租户', path: '/api/tenants/{id}/children', method: 'GET', description: '获取子租户' },
  { module: '租户', path: '/api/tenants/{id}/descendants', method: 'GET', description: '获取后代租户' },
  { module: '租户', path: '/api/tenants/{id}/parent', method: 'GET', description: '获取父租户' },
  { module: '租户', path: '/api/tenants/{id}/ancestors', method: 'GET', description: '获取祖先租户' },
  { module: '租户', path: '/api/tenants/{id}/move', method: 'POST', description: '移动租户' },
  { module: '租户', path: '/api/tenants/{id}/tree', method: 'GET', description: '获取租户树' },
  { module: '系统', path: '/system/sync-table', method: 'POST', description: '同步数据库表' }
])

const getMethodTag = (method) => {
  const map = { GET: 'success', POST: 'warning', PUT: '', DELETE: 'danger' }
  return map[method] || 'info'
}

const handleSyncTable = async () => {
  try {
    await ElMessageBox.confirm(
      '此操作将同步数据库表结构，可能会修改或创建数据库表。确定要继续吗？',
      '警告',
      {
        confirmButtonText: '确定同步',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    syncLoading.value = true
    await systemApi.syncTable()
    ElMessage.success('数据库表同步成功')
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  } finally {
    syncLoading.value = false
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
