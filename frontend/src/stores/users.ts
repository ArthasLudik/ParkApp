import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi } from '@/services/api'
import type { User, CreateUserDto, UserRole } from '@/types'

export const useUsersStore = defineStore('users', () => {
  const users = ref<User[]>([])
  const loading = ref(false)

  const fetchAllUsers = async () => {
    loading.value = true
    try {
      const resp = await userApi.getAll()
      users.value = resp.data
    } finally {
      loading.value = false
    }
  }

  const createUser = async (payload: CreateUserDto) => {
    const resp = await userApi.create(payload)
    users.value.push(resp.data as unknown as User)
    return resp.data
  }

  const updateUserRole = async (id: number, role: UserRole) => {
    const resp = await userApi.updateRole(id, role)
    const idx = users.value.findIndex(u => u.id === id)
    if (idx !== -1) users.value[idx] = resp.data as unknown as User
    return resp.data
  }

  const deleteUser = async (id: number) => {
    await userApi.delete(id)
    users.value = users.value.filter(u => u.id !== id)
  }

  return { users, loading, fetchAllUsers, createUser, updateUserRole, deleteUser }
})
