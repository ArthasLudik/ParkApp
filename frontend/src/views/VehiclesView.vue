<template>
  <div class="vehicles">
    <div class="flex justify-content-between align-items-center mb-4">
      <h1 class="text-3xl font-bold">Vehicles Management</h1>
      <Button label="Add New Vehicle" icon="pi pi-plus" @click="showCreateDialog = true" />
    </div>

    <DataTable :value="vehicles" 
               :loading="loading" 
               stripedRows 
               paginator 
               :rows="10"
               :rowsPerPageOptions="[5, 10, 20, 50]">
      
      <Column field="id" header="ID" sortable></Column>
      <Column field="licensePlate" header="License Plate" sortable></Column>
      <Column field="type" header="Type" sortable>
        <template #body="slotProps">
          <Tag :value="slotProps.data.type" 
               :severity="getTypeSeverity(slotProps.data.type)" />
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

    <!-- Create Dialog -->
    <Dialog v-model:visible="showCreateDialog" 
            header="Add New Vehicle"
            modal 
            class="p-fluid">
      <div class="field">
        <label for="licensePlate">License Plate</label>
        <InputText id="licensePlate" v-model="vehicleForm.licensePlate" required />
      </div>
      
      <div class="field">
        <label for="type">Vehicle Type</label>
        <Dropdown id="type" 
                  v-model="vehicleForm.type" 
                  :options="vehicleTypes" 
                  optionLabel="label" 
                  optionValue="value"
                  placeholder="Select Type" 
                  required />
      </div>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" outlined @click="closeDialog" />
        <Button label="Create" icon="pi pi-check" @click="saveVehicle" />
      </template>
    </Dialog>

    <ConfirmDialog />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { vehicleApi } from '@/services/api'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'
import type { Vehicle, CreateVehicleDto } from '@/types'
import { VehicleType } from '@/types'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Dropdown from 'primevue/dropdown'
import Tag from 'primevue/tag'
import ConfirmDialog from 'primevue/confirmdialog'

const vehicles = ref<Vehicle[]>([])
const loading = ref(false)
const showCreateDialog = ref(false)
const confirm = useConfirm()
const toast = useToast()

const vehicleForm = ref({
  licensePlate: '',
  type: null as VehicleType | null
})

const vehicleTypes = [
  { label: 'Car', value: VehicleType.CAR },
  { label: 'Motorcycle', value: VehicleType.MOTORCYCLE },
  { label: 'Truck', value: VehicleType.TRUCK }
]

const getTypeSeverity = (type: VehicleType) => {
  switch (type) {
    case VehicleType.CAR: return 'info'
    case VehicleType.MOTORCYCLE: return 'warning'
    case VehicleType.TRUCK: return 'danger'
    default: return 'info'
  }
}

const fetchVehicles = async () => {
  loading.value = true
  try {
    const resp = await vehicleApi.getAll()
    vehicles.value = resp.data
  } catch (e) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch vehicles', life: 3000 })
  } finally {
    loading.value = false
  }
}

const saveVehicle = async () => {
  try {
    const createData: CreateVehicleDto = {
      licensePlate: vehicleForm.value.licensePlate,
      type: vehicleForm.value.type!
    }
    const response = await vehicleApi.create(createData)
    vehicles.value.push(response.data)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Vehicle created successfully', life: 3000 })
    closeDialog()
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to create vehicle', life: 3000 })
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  vehicleForm.value = { licensePlate: '', type: null }
}

const confirmDelete = (vehicle: Vehicle) => {
  confirm.require({
    message: `Are you sure you want to delete vehicle ${vehicle.licensePlate}?`,
    header: 'Delete Confirmation',
    icon: 'pi pi-exclamation-triangle',
    accept: () => deleteVehicle(vehicle.id)
  })
}

const deleteVehicle = async (id: number) => {
  try {
    await vehicleApi.delete(id)
    vehicles.value = vehicles.value.filter(v => v.id !== id)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Vehicle deleted successfully', life: 3000 })
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete vehicle', life: 3000 })
  }
}

onMounted(fetchVehicles)
</script>

<style scoped>
.vehicles {
  padding: 1rem;
}
</style> 