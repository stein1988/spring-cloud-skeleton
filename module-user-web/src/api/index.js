import api from './request'

export const authApi = {
  login(data) {
    return api.post('/api/user/v1/auth/login', data)
  },

  refreshToken(data) {
    return api.post('/api/user/v1/auth/refresh-token', data)
  },

  getPublicKey() {
    return api.get('/api/user/v1/auth/public-key')
  }
}

export const userApi = {
  getAll() {
    return api.get('/api/user/v1/users')
  },

  getById(id, params = {}) {
    return api.get(`/api/user/v1/users/${id}`, { params })
  },

  create(data) {
    return api.post('/api/user/v1/users', data)
  },

  update(id, data) {
    return api.post(`/api/user/v1/users/${id}/update`, data)
  },

  delete(id) {
    return api.post(`/api/user/v1/users/${id}/delete`)
  }
}

export const departmentApi = {
  getAll() {
    return api.get('/api/user/v1/departments')
  },

  getById(id, params = {}) {
    return api.get(`/api/user/v1/departments/${id}`, { params })
  },

  create(data) {
    return api.post('/api/user/v1/departments', data)
  },

  update(id, data) {
    return api.post(`/api/user/v1/departments/${id}/update`, data)
  },

  delete(id) {
    return api.post(`/api/user/v1/departments/${id}/delete`)
  },

  getChildren(id) {
    return api.get(`/api/user/v1/departments/${id}/children`)
  },

  getDescendants(id) {
    return api.get(`/api/user/v1/departments/${id}/descendants`)
  },

  getParent(id) {
    return api.get(`/api/user/v1/departments/${id}/parent`)
  },

  getAncestors(id) {
    return api.get(`/api/user/v1/departments/${id}/ancestors`)
  },

  move(id, newParentId) {
    return api.post(`/api/user/v1/departments/${id}/move`, null, { params: { newParentId } })
  },

  getTree(id) {
    return api.get(`/api/user/v1/departments/${id}/tree`)
  }
}

export const roleApi = {
  getAll() {
    return api.get('/api/user/v1/roles')
  },

  getById(id) {
    return api.get(`/api/user/v1/roles/${id}`)
  },

  create(data) {
    return api.post('/api/user/v1/roles', data)
  },

  update(id, data) {
    return api.post(`/api/user/v1/roles/${id}/update`, data)
  },

  delete(id) {
    return api.post(`/api/user/v1/roles/${id}/delete`)
  }
}

export const permissionApi = {
  getAll() {
    return api.get('/api/user/v1/permissions')
  },

  getById(id) {
    return api.get(`/api/user/v1/permissions/${id}`)
  },

  create(data) {
    return api.post('/api/user/v1/permissions', data)
  },

  update(id, data) {
    return api.post(`/api/user/v1/permissions/${id}/update`, data)
  },

  delete(id) {
    return api.post(`/api/user/v1/permissions/${id}/delete`)
  }
}

export const tenantApi = {
  getAll(params = {}) {
    return api.get('/api/user/v1/tenants', { params })
  },

  getById(id) {
    return api.get(`/api/user/v1/tenants/${id}`)
  },

  create(data) {
    return api.post('/api/user/v1/tenants', data)
  },

  update(id, data) {
    return api.post(`/api/user/v1/tenants/${id}/update`, data)
  },

  delete(id) {
    return api.post(`/api/user/v1/tenants/${id}/delete`)
  },

  getChildren(id) {
    return api.get(`/api/user/v1/tenants/${id}/children`)
  },

  getDescendants(id) {
    return api.get(`/api/user/v1/tenants/${id}/descendants`)
  },

  getParent(id) {
    return api.get(`/api/user/v1/tenants/${id}/parent`)
  },

  getAncestors(id) {
    return api.get(`/api/user/v1/tenants/${id}/ancestors`)
  },

  move(id, newParentId) {
    return api.post(`/api/user/v1/tenants/${id}/move`, null, { params: { newParentId } })
  },

  getTree(id) {
    return api.get(`/api/user/v1/tenants/${id}/tree`)
  }
}

export const systemApi = {
  syncTable() {
    return api.post('/api/user/v1/system/sync-table')
  }
}
