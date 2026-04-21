<template>
  <div class="permission-management">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon> 新增权限
          </el-button>
        </div>
      </template>

      <el-table :data="permissions" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="300" show-overflow-tooltip />
        <el-table-column prop="name" label="权限名称" width="180" />
        <el-table-column prop="code" label="权限编码" width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getPermTypeTag(row.type)">
              {{ getPermTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resource" label="资源" width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" link @click="handleView(row)">查看</el-button>
            <el-popconfirm
              title="确定要删除该权限吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '新增权限' : '编辑权限'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限编码" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择权限类型" style="width: 100%">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
            <el-option label="数据" value="DATA" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源" prop="resource">
          <el-input v-model="form.resource" placeholder="请输入资源路径" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="权限详情" width="500px">
      <el-descriptions :column="1" border v-if="currentPerm">
        <el-descriptions-item label="ID">{{ currentPerm.id }}</el-descriptions-item>
        <el-descriptions-item label="权限名称">{{ currentPerm.name }}</el-descriptions-item>
        <el-descriptions-item label="权限编码">{{ currentPerm.code }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="getPermTypeTag(currentPerm.type)">{{ getPermTypeLabel(currentPerm.type) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="资源">{{ currentPerm.resource }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentPerm.description }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentPerm.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentPerm.updatedAt }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { permissionApi } from '../api'

const loading = ref(false)
const submitLoading = ref(false)
const permissions = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogType = ref('create')
const currentPerm = ref(null)
const formRef = ref(null)

const form = reactive({
  name: '',
  code: '',
  type: '',
  resource: '',
  description: ''
})

const formRules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

const getPermTypeLabel = (type) => {
  const map = { MENU: '菜单', BUTTON: '按钮', API: '接口', DATA: '数据' }
  return map[type] || type || '未知'
}

const getPermTypeTag = (type) => {
  const map = { MENU: '', BUTTON: 'success', API: 'warning', DATA: 'info' }
  return map[type] || 'info'
}

const fetchPermissions = async () => {
  loading.value = true
  try {
    const res = await permissionApi.getAll()
    permissions.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, { name: '', code: '', type: '', resource: '', description: '' })
}

const handleCreate = () => {
  resetForm()
  dialogType.value = 'create'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    name: row.name,
    code: row.code,
    type: row.type,
    resource: row.resource,
    description: row.description
  })
  currentPerm.value = row
  dialogType.value = 'edit'
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await permissionApi.getById(row.id)
    currentPerm.value = res.data
    detailVisible.value = true
  } catch (e) {
    currentPerm.value = row
    detailVisible.value = true
  }
}

const handleDelete = async (row) => {
  try {
    await permissionApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchPermissions()
  } catch (e) {
    console.error(e)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'create') {
          await permissionApi.create(form)
          ElMessage.success('创建成功')
        } else {
          await permissionApi.update(currentPerm.value.id, form)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchPermissions()
      } catch (e) {
        console.error(e)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchPermissions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
