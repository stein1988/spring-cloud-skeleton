<template>
  <div class="tenant-management">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>租户树</span>
              <el-button type="primary" size="small" @click="handleCreate(null)">
                <el-icon><Plus /></el-icon> 新增根租户
              </el-button>
            </div>
          </template>
          <el-input
            v-model="filterText"
            placeholder="搜索租户"
            clearable
            style="margin-bottom: 12px"
          />
          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            :filter-node-method="filterNode"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon><School /></el-icon>
                <span>{{ node.label }}</span>
                <span class="tree-actions">
                  <el-button type="primary" link size="small" @click.stop="handleCreate(data)">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                  <el-button type="danger" link size="small" @click.stop="handleDelete(data)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span>{{ currentTenant ? '租户详情 - ' + (currentTenant.name || currentTenant.id) : '请选择租户' }}</span>
          </template>

          <div v-if="currentTenant">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="ID">{{ currentTenant.id }}</el-descriptions-item>
              <el-descriptions-item label="名称">{{ currentTenant.name }}</el-descriptions-item>
              <el-descriptions-item label="编码">{{ currentTenant.code }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ currentTenant.contact }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ currentTenant.phone }}</el-descriptions-item>
              <el-descriptions-item label="地址" :span="2">{{ currentTenant.address }}</el-descriptions-item>
              <el-descriptions-item label="创建时间" :span="2">{{ currentTenant.createdAt }}</el-descriptions-item>
            </el-descriptions>

            <div style="margin-top: 20px; display: flex; gap: 10px;">
              <el-button type="primary" @click="handleEdit(currentTenant)">编辑</el-button>
              <el-button @click="handleMove(currentTenant)">移动</el-button>
              <el-button @click="fetchChildren(currentTenant.id)">查看子租户</el-button>
              <el-button @click="fetchAncestors(currentTenant.id)">查看祖先</el-button>
            </div>

            <el-dialog v-model="childrenVisible" title="子租户列表" width="600px">
              <el-table :data="childrenList" stripe border>
                <el-table-column prop="id" label="ID" show-overflow-tooltip />
                <el-table-column prop="name" label="名称" />
                <el-table-column prop="code" label="编码" />
                <el-table-column prop="contact" label="联系人" />
              </el-table>
            </el-dialog>

            <el-dialog v-model="ancestorsVisible" title="祖先租户列表" width="600px">
              <el-table :data="ancestorsList" stripe border>
                <el-table-column prop="id" label="ID" show-overflow-tooltip />
                <el-table-column prop="name" label="名称" />
                <el-table-column prop="code" label="编码" />
              </el-table>
            </el-dialog>
          </div>

          <el-empty v-else description="请在左侧选择一个租户查看详情" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '新增租户' : '编辑租户'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入租户名称" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入租户编码" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="父租户" v-if="dialogType === 'create' && parentTenant">
          <el-input :model-value="parentTenant.name" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="moveVisible" title="移动租户" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前租户">
          <el-input :model-value="moveTenant?.name" disabled />
        </el-form-item>
        <el-form-item label="目标父租户">
          <el-select v-model="moveTargetId" placeholder="请选择目标父租户" filterable style="width: 100%">
            <el-option
              v-for="t in flatTenants"
              :key="t.id"
              :label="t.name || t.id"
              :value="t.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="moveVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleMoveSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { tenantApi } from '../api'

const loading = ref(false)
const submitLoading = ref(false)
const tenants = ref([])
const currentTenant = ref(null)
const parentTenant = ref(null)
const dialogVisible = ref(false)
const dialogType = ref('create')
const formRef = ref(null)
const treeRef = ref(null)
const filterText = ref('')
const childrenVisible = ref(false)
const childrenList = ref([])
const ancestorsVisible = ref(false)
const ancestorsList = ref([])
const moveVisible = ref(false)
const moveTenant = ref(null)
const moveTargetId = ref('')

const treeProps = {
  children: 'children',
  label: 'name'
}

const form = reactive({
  name: '',
  code: '',
  contact: '',
  phone: '',
  address: '',
  parentId: null
})

const formRules = {
  name: [{ required: true, message: '请输入租户名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入租户编码', trigger: 'blur' }]
}

const flatTenants = computed(() => {
  const result = []
  const flatten = (list) => {
    list.forEach(item => {
      result.push(item)
      if (item.children?.length) flatten(item.children)
    })
  }
  flatten(treeData.value)
  return result
})

const treeData = computed(() => {
  const map = {}
  const roots = []
  tenants.value.forEach(t => {
    map[t.id] = { ...t, children: [] }
  })
  tenants.value.forEach(t => {
    if (t.parentId && map[t.parentId]) {
      map[t.parentId].children.push(map[t.id])
    } else {
      roots.push(map[t.id])
    }
  })
  return roots
})

watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

const filterNode = (value, data) => {
  if (!value) return true
  return data.name?.includes(value)
}

const fetchTenants = async () => {
  loading.value = true
  try {
    const res = await tenantApi.getAll()
    tenants.value = res.data?.items || res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleNodeClick = (data) => {
  currentTenant.value = data
}

const handleCreate = (parent) => {
  parentTenant.value = parent
  Object.assign(form, { name: '', code: '', contact: '', phone: '', address: '', parentId: parent?.id || null })
  dialogType.value = 'create'
  dialogVisible.value = true
}

const handleEdit = (tenant) => {
  Object.assign(form, {
    name: tenant.name,
    code: tenant.code,
    contact: tenant.contact,
    phone: tenant.phone,
    address: tenant.address,
    parentId: tenant.parentId
  })
  currentTenant.value = tenant
  dialogType.value = 'edit'
  dialogVisible.value = true
}

const handleDelete = async (tenant) => {
  try {
    await ElMessageBox.confirm('确定要删除该租户吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await tenantApi.delete(tenant.id)
    ElMessage.success('删除成功')
    if (currentTenant.value?.id === tenant.id) currentTenant.value = null
    fetchTenants()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleMove = (tenant) => {
  moveTenant.value = tenant
  moveTargetId.value = ''
  moveVisible.value = true
}

const handleMoveSubmit = async () => {
  if (!moveTargetId.value) {
    ElMessage.warning('请选择目标父租户')
    return
  }
  submitLoading.value = true
  try {
    await tenantApi.move(moveTenant.value.id, moveTargetId.value)
    ElMessage.success('移动成功')
    moveVisible.value = false
    fetchTenants()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const fetchChildren = async (id) => {
  try {
    const res = await tenantApi.getChildren(id)
    childrenList.value = res.data || []
    childrenVisible.value = true
  } catch (e) {
    console.error(e)
  }
}

const fetchAncestors = async (id) => {
  try {
    const res = await tenantApi.getAncestors(id)
    ancestorsList.value = res.data || []
    ancestorsVisible.value = true
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
          await tenantApi.create(form)
          ElMessage.success('创建成功')
        } else {
          const { parentId, ...updateData } = form
          await tenantApi.update(currentTenant.value.id, updateData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchTenants()
      } catch (e) {
        console.error(e)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchTenants()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  font-size: 14px;
}

.tree-actions {
  margin-left: auto;
  display: none;
}

.tree-node:hover .tree-actions {
  display: inline-flex;
}
</style>
