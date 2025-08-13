<template>
  <div class="parking-spots">
    <div class="flex justify-content-between align-items-center mb-4">
      <h1 class="text-3xl font-bold">Parking Spots Management</h1>
      <Button label="Add New Spot" icon="pi pi-plus" @click="showCreateDialog = true" />
    </div>

    <div class="flex gap-2 mb-4">
      <Button label="All Spots" 
              :outlined="filter !== 'all'" 
              @click="filter = 'all'; fetchSpots()" />
      <Button label="Free Spots" 
              :outlined="filter !== 'free'" 
              @click="filter = 'free'; fetchSpots()" />
      <Button label="Occupied Spots" 
              :outlined="filter !== 'occupied'" 
              @click="filter = 'occupied'; fetchSpots()" />
    </div>

    <DataTable :value="filteredSpots" 
               :loading="parkingStore.loading" 
               stripedRows 
               paginator 
               :rows="10"
               :rowsPerPageOptions="[5, 10, 20, 50]"
               filterDisplay="menu"
               :globalFilterFields="['spotNumber', 'type', 'status']">
      
      <template #header>
        <div class="flex justify-content-between">
          <span class="p-input-icon-left">
            <i class="pi pi-search" />
            <InputText v-model="filters.global.value" placeholder="Search..." />
          </span>
        </div>
      </template>

      <Column field="spotId" header="Spot ID" sortable />

      <Column field="spotNumber" header="Spot Number" sortable>
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" class="p-column-filter" placeholder="Search by number" />
        </template>
      </Column>
      
      <Column field="type" header="Type" sortable>
        <template #body="slotProps">
          <Tag :value="slotProps.data.type" 
               :severity="getTypeSeverity(slotProps.data.type)" />
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <Dropdown v-model="filterModel.value" @change="filterCallback()" :options="spotTypes" placeholder="Select Type" class="p-column-filter" showClear />
        </template>
      </Column>
      
      <Column field="status" header="Status" sortable>
        <template #body="slotProps">
          <Tag :value="slotProps.data.status" 
               :severity="slotProps.data.status === 'FREE' ? 'success' : 'danger'" />
        </template>
      </Column>
      
      <Column field="hourlyRate" header="Hourly Rate" sortable>
        <template #body="slotProps">
          ${{ slotProps.data.hourlyRate }}/hr
        </template>
      </Column>
      
      <Column header="Actions" :exportable="false" style="min-width:8rem">
        <template #body="slotProps">
          <div class="flex gap-2">
            <Button icon="pi pi-pencil" 
                    outlined 
                    rounded 
                    @click="editSpot(slotProps.data)" />
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
            :header="editingSpot ? 'Edit Parking Spot' : 'Create New Parking Spot'"
            modal 
            class="p-fluid">
      <div class="field">
        <label for="spotNumber">Spot Number</label>
        <InputText id="spotNumber" v-model="spotForm.spotNumber" required />
      </div>
      
      <div class="field">
        <label for="type">Type</label>
        <Dropdown id="type" 
                  v-model="spotForm.type" 
                  :options="spotTypes" 
                  optionLabel="label" 
                  optionValue="value"
                  placeholder="Select Type" 
                  required />
      </div>
      
      <div class="field">
        <label for="hourlyRate">Hourly Rate ($)</label>
        <InputNumber id="hourlyRate" 
                     v-model="spotForm.hourlyRate" 
                     mode="currency" 
                     currency="USD" 
                     :minFractionDigits="2"
                     :maxFractionDigits="2"
                     required />
      </div>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" outlined @click="closeDialog" />
        <Button :label="editingSpot ? 'Update' : 'Create'" 
                icon="pi pi-check" 
                @click="saveSpot" />
      </template>
    </Dialog>

    <!-- Delete Confirmation -->
    <ConfirmDialog />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useParkingStore } from '@/stores/parking'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'
import type { ParkingSpot, CreateParkingSpotDto, UpdateParkingSpotDto } from '@/types'
import { SpotType } from '@/types'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Dropdown from 'primevue/dropdown'
import Tag from 'primevue/tag'
import ConfirmDialog from 'primevue/confirmdialog'

const parkingStore = useParkingStore()
const confirm = useConfirm()
const toast = useToast()

const filter = ref<'all' | 'free' | 'occupied'>('all')
const showCreateDialog = ref(false)
const editingSpot = ref<ParkingSpot | null>(null)

const spotForm = ref({
  spotNumber: '',
  type: null as SpotType | null,
  hourlyRate: 0
})

const spotTypes = [
  { label: 'Regular', value: SpotType.REGULAR },
  { label: 'Disabled', value: SpotType.DISABLED },
  { label: 'Electric', value: SpotType.ELECTRIC }
]

const filters = ref({
  global: { value: null, matchMode: 'contains' }
})

const filteredSpots = computed(() => {
  let spots = parkingStore.spots
  
  if (filter.value === 'free') {
    spots = parkingStore.freeSpots
  } else if (filter.value === 'occupied') {
    spots = parkingStore.occupiedSpots
  }
  
  return spots
})

const getTypeSeverity = (type: SpotType) => {
  switch (type) {
    case SpotType.REGULAR: return 'info'
    case SpotType.DISABLED: return 'warning'
    case SpotType.ELECTRIC: return 'success'
    default: return 'info'
  }
}

const fetchSpots = async () => {
  if (filter.value === 'free') {
    await parkingStore.fetchFreeSpots()
  } else {
    await parkingStore.fetchAllSpots()
  }
}

const editSpot = (spot: ParkingSpot) => {
  editingSpot.value = spot
  spotForm.value = {
    spotNumber: spot.spotNumber,
    type: spot.type,
    hourlyRate: spot.hourlyRate
  }
  showCreateDialog.value = true
}

const saveSpot = async () => {
  try {
    if (editingSpot.value) {
      const updateData: UpdateParkingSpotDto = {
        spotId: editingSpot.value.spotId,
        spotNumber: spotForm.value.spotNumber,
        type: spotForm.value.type!,
        hourlyRate: spotForm.value.hourlyRate
      }
      await parkingStore.updateSpot(updateData)
    } else {
      const createData: CreateParkingSpotDto = {
        spotNumber: spotForm.value.spotNumber,
        type: spotForm.value.type!,
        hourlyRate: spotForm.value.hourlyRate
      }
      await parkingStore.createSpot(createData)
    }
    closeDialog()
  } catch (error) {
    // Error handling is done in the store
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  editingSpot.value = null
  spotForm.value = {
    spotNumber: '',
    type: null,
    hourlyRate: 0
  }
}

const confirmDelete = (spot: ParkingSpot) => {
  confirm.require({
    message: `Are you sure you want to delete parking spot ${spot.spotNumber}?`,
    header: 'Delete Confirmation',
    icon: 'pi pi-exclamation-triangle',
    accept: () => deleteSpot(spot.spotId)
  })
}

const deleteSpot = async (id: number) => {
  try {
    await parkingStore.deleteSpot(id)
  } catch (error) {
    // Error handling is done in the store
  }
}

onMounted(() => {
  fetchSpots()
})
</script>

<style scoped>
.parking-spots {
  padding: 1rem;
}
</style> 