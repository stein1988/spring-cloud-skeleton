<template>
  <div class="department-management">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>部门树</span>
              <el-button type="primary" size="small" @click="handleCreate(null)">
                <el-icon><Plus /></el-icon> 新增根部门
              </el-button>
            </div>
          </template>
          <el-input
            v-model="filterText"
            placeholder="搜索部门"
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
                <el-icon><OfficeBuilding /></el-icon>
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
            <span>{{ currentDept ? '部门详情 - ' + (currentDept.name || currentDept.id) : '请选择部门' }}</span>
          </template>

          <div v-if="currentDept">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="ID">{{ currentDept.id }}</el-descriptions-item>
              <el-descriptions-item label="名称">{{ currentDept.name }}</el-descriptions-item>
              <el-descriptions-item label="编码">{{ currentDept.code }}</el-descriptions-item>
              <el-descriptions-item label="排序">{{ currentDept.sort }}</el-descriptions-item>
              <el-descriptions-item label="创建时间" :span="2">{{ currentDept.createdAt }}</el-descriptions-item>
            </el-descriptions>

            <div style="margin-top: 20px; display: flex; gap: 10px;">
              <el-button type="primary" @click="handleEdit(currentDept)">编辑</el-button>
              <el-button @click="handleMove(currentDept)">移动</el-button>
              <el-button @click="fetchChildren(currentDept.id)">查看子部门</el-button>
              <el-button @click="fetchAncestors(currentDept.id)">查看祖先</el-button>
            </div>

            <el-dialog v-model="childrenVisible" title="子部门列表" width="600px">
              <el-table :data="childrenList" stripe border>
                <el-table-column prop="id" label="ID" show-overflow-tooltip />
                <el-table-column prop="name" label="名称" />
                <el-table-column prop="code" label="编码" />
              </el-table>
            </el-dialog>

            <el-dialog v-model="ancestorsVisible" title="祖先部门列表" width="600px">
              <el-table :data="ancestorsList" stripe border>
                <el-table-column prop="id" label="ID" show-overflow-tooltip />
                <el-table-column prop="name" label="名称" />
                <el-table-column prop="code" label="编码" />
              </el-table>
            </el-dialog>
          </div>

          <el-empty v-else description="请在左侧选择一个部门查看详情" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '新增部门' : '编辑部门'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="父部门" v-if="dialogType === 'create' && parentDept">
          <el-input :model-value="parentDept.name" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="moveVisible" title="移动部门" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前部门">
          <el-input :model-value="moveDept?.name" disabled />
        </el-form-item>
        <el-form-item label="目标父部门">
          <el-select v-model="moveTargetId" placeholder="请选择目标父部门" filterable style="width: 100%">
            <el-option
              v-for="dept in flatDepartments"
              :key="dept.id"
              :label="dept.name || dept.id"
              :value="dept.id"
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
import { departmentApi } from '../api'

const loading = ref(false)
const submitLoading = ref(false)
const departments = ref([])
const currentDept = ref(null)
const parentDept = ref(null)
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
const moveDept = ref(null)
const moveTargetId = ref('')

const treeProps = {
  children: 'children',
  label: 'name'
}

const form = reactive({
  name: '',
  code: '',
  sort: 0,
  parentId: null
})

const formRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }]
}

const flatDepartments = computed(() => {
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
  departments.value.forEach(dept => {
    map[dept.id] = { ...dept, children: [] }
  })
  departments.value.forEach(dept => {
    if (dept.parentId && map[dept.parentId]) {
      map[dept.parentId].children.push(map[dept.id])
    } else {
      roots.push(map[dept.id])
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

const fetchDepartments = async () => {
  loading.value = true
  try {
    const res = await departmentApi.getAll()
    departments.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleNodeClick = (data) => {
  currentDept.value = data
}

const handleCreate = (parent) => {
  parentDept.value = parent
  Object.assign(form, { name: '', code: '', sort: 0, parentId: parent?.id || null })
  dialogType.value = 'create'
  dialogVisible.value = true
}

const handleEdit = (dept) => {
  Object.assign(form, {
    name: dept.name,
    code: dept.code,
    sort: dept.sort || 0,
    parentId: dept.parentId
  })
  currentDept.value = dept
  dialogType.value = 'edit'
  dialogVisible.value = true
}

const handleDelete = async (dept) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await departmentApi.delete(dept.id)
    ElMessage.success('删除成功')
    if (currentDept.value?.id === dept.id) currentDept.value = null
    fetchDepartments()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleMove = (dept) => {
  moveDept.value = dept
  moveTargetId.value = ''
  moveVisible.value = true
}

const handleMoveSubmit = async () => {
  if (!moveTargetId.value) {
    ElMessage.warning('请选择目标父部门')
    return
  }
  submitLoading.value = true
  try {
    await departmentApi.move(moveDept.value.id, moveTargetId.value)
    ElMessage.success('移动成功')
    moveVisible.value = false
    fetchDepartments()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const fetchChildren = async (id) => {
  try {
    const res = await departmentApi.getChildren(id)
    childrenList.value = res.data || []
    childrenVisible.value = true
  } catch (e) {
    console.error(e)
  }
}

const fetchAncestors = async (id) => {
  try {
    const res = await departmentApi.getAncestors(id)
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
          await departmentApi.create(form)
          ElMessage.success('创建成功')
        } else {
          const { parentId, ...updateData } = form
          await departmentApi.update(currentDept.value.id, updateData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchDepartments()
      } catch (e) {
        console.error(e)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchDepartments()
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
