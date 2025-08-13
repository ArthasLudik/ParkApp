<template>
  <div class="users">
    <div class="flex justify-content-between align-items-center mb-4">
      <h1 class="text-3xl font-bold">Users Management</h1>
      <Button label="Add New User" icon="pi pi-plus" @click="showCreateDialog = true" />
    </div>

    <DataTable :value="usersStore.users"
               :loading="usersStore.loading"
               stripedRows
               paginator
               :rows="10"
               :rowsPerPageOptions="[5, 10, 20, 50]">

      <Column field="id" header="ID" sortable></Column>
      <Column field="name" header="Name" sortable></Column>
      <Column field="role" header="Role" sortable>
        <template #body="slotProps">
          <Tag :value="slotProps.data.role"
               :severity="slotProps.data.role === 'ADMIN' ? 'danger' : 'info'" />
        </template>
      </Column>

      <Column header="Actions" :exportable="false" style="min-width:8rem">
        <template #body="slotProps">
          <div class="flex gap-2">
            <Button icon="pi pi-trash"
                    outlined
                    rounded
                    severity="danger"
                    @click="confirmDelete(slotProps.data)" />
          </div>
        </template>
      </Column>
    </DataTable>

    <!-- Create/Edit Dialog -->
    <Dialog v-model:visible="showCreateDialog"
            header="Create New User"
            modal
            class="p-fluid">
      <div class="field">
        <label for="name">Name</label>
        <InputText id="name" v-model="userForm.name" required />
      </div>

      <div class="field">
        <label for="role">Role</label>
        <Dropdown id="role"
                  v-model="userForm.role"
                  :options="userRoles"
                  optionLabel="label"
                  optionValue="value"
                  placeholder="Select Role"
                  required />
      </div>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" outlined @click="closeDialog" />
        <Button label="Create"
                icon="pi pi-check"
                @click="saveUser" />
      </template>
    </Dialog>

    <ConfirmDialog />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'
import type { CreateUserDto } from '@/types'
import { UserRole } from '@/types'
import { useUsersStore } from '@/stores/users'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Dropdown from 'primevue/dropdown'
import Tag from 'primevue/tag'
import ConfirmDialog from 'primevue/confirmdialog'

const usersStore = useUsersStore()
const confirm = useConfirm()
const toast = useToast()

const showCreateDialog = ref(false)

const userForm = ref({
  name: '',
  role: null as UserRole | null
})

const userRoles = [
  { label: 'User', value: UserRole.USER },
  { label: 'Admin', value: UserRole.ADMIN }
]

const fetchUsers = async () => {
  await usersStore.fetchAllUsers()
}

const saveUser = async () => {
  try {
    const payload: CreateUserDto = {
      name: userForm.value.name,
      role: userForm.value.role ?? UserRole.USER
    } as any
    await usersStore.createUser(payload)
    toast.add({ severity: 'success', summary: 'Success', detail: 'User created successfully', life: 3000 })
    closeDialog()
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to create user', life: 3000 })
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  userForm.value = { name: '', role: null }
}

const confirmDelete = (user: { id: number, name: string }) => {
  confirm.require({
    message: `Are you sure you want to delete user ${user.name}?`,
    header: 'Delete Confirmation',
    icon: 'pi pi-exclamation-triangle',
    accept: () => deleteUser(user.id)
  })
}

const deleteUser = async (id: number) => {
  try {
    await usersStore.deleteUser(id)
    toast.add({ severity: 'success', summary: 'Success', detail: 'User deleted', life: 3000 })
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete user', life: 3000 })
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.users {
  padding: 1rem;
}
</style>
