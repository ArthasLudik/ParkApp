<template>
  <div class="sessions">
    <div class="flex justify-content-between align-items-center mb-4">
      <h1 class="text-3xl font-bold">Parking Sessions</h1>
      <Button label="Start New Session" icon="pi pi-plus" @click="showCreateDialog = true" />
    </div>

    <div class="flex gap-2 mb-4">
      <Button label="All Sessions" 
              :outlined="filter !== 'all'" 
              @click="filter = 'all'; fetchSessions()" />
      <Button label="Active Sessions" 
              :outlined="filter !== 'active'" 
              @click="filter = 'active'; fetchSessions()" />
      <Button label="Completed Sessions" 
              :outlined="filter !== 'completed'" 
              @click="filter = 'completed'; fetchSessions()" />
    </div>

    <DataTable :value="filteredSessions" 
               :loading="sessionsStore.loading" 
               stripedRows 
               paginator 
               :rows="10"
               :rowsPerPageOptions="[5, 10, 20, 50]">
      
      <Column field="id" header="Session ID" sortable></Column>
      <Column field="vehicleId" header="Vehicle ID" sortable></Column>
      <Column field="spotId" header="Spot ID" sortable></Column>
      <Column field="operatorId" header="Operator ID" sortable></Column>
      <Column field="startTime" header="Start Time" sortable>
        <template #body="slotProps">
          {{ formatDateTime(slotProps.data.startTime) }}
        </template>
      </Column>
      <Column field="endTime" header="End Time" sortable>
        <template #body="slotProps">
          {{ slotProps.data.endTime ? formatDateTime(slotProps.data.endTime) : '-' }}
        </template>
      </Column>
      <Column field="totalCost" header="Total Cost" sortable>
        <template #body="slotProps">
          {{ slotProps.data.totalCost ? `$${slotProps.data.totalCost}` : '-' }}
        </template>
      </Column>
      <Column field="isActive" header="Status" sortable>
        <template #body="slotProps">
          <Tag :value="slotProps.data.isActive ? 'ACTIVE' : 'COMPLETED'" 
               :severity="slotProps.data.isActive ? 'warning' : 'success'" />
        </template>
      </Column>
      
      <Column header="Actions" :exportable="false" style="min-width:8rem">
        <template #body="slotProps">
          <div class="flex gap-2">
            <Button v-if="slotProps.data.isActive"
                    icon="pi pi-stop" 
                    outlined 
                    rounded 
                    severity="warning"
                    @click="completeSession(slotProps.data)" />
            <Button icon="pi pi-trash" 
                    outlined 
                    rounded 
                    severity="danger" 
                    @click="confirmDelete(slotProps.data)" />
          </div>
        </template>
      </Column>
    </DataTable>

    <!-- Create Session Dialog -->
    <Dialog v-model:visible="showCreateDialog" 
            header="Start New Parking Session"
            modal 
            class="p-fluid">
      <div class="field">
        <label for="vehicleId">Vehicle ID</label>
        <InputNumber id="vehicleId" v-model="sessionForm.vehicleId" required />
      </div>
      
      <div class="field">
        <label for="spotId">Parking Spot ID</label>
        <InputNumber id="spotId" v-model="sessionForm.spotId" required />
      </div>

      <div class="field">
        <label for="operatorId">Operator ID</label>
        <InputNumber id="operatorId" v-model="sessionForm.operatorId" required />
      </div>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" outlined @click="closeDialog" />
        <Button label="Start Session" icon="pi pi-check" @click="saveSession" />
      </template>
    </Dialog>

    <!-- Complete Session Dialog -->
    <Dialog v-model:visible="showCompleteDialog" 
            header="Complete Parking Session"
            modal 
            class="p-fluid">
      <div class="field">
        <label for="endTime">End Time</label>
        <Calendar id="endTime" 
                  v-model="completeForm.endTime" 
                  showTime 
                  showSeconds
                  required />
      </div>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" outlined @click="closeCompleteDialog" />
        <Button label="Complete Session" icon="pi pi-check" @click="finishSession" />
      </template>
    </Dialog>

    <!-- Delete Confirmation -->
    <ConfirmDialog />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useSessionsStore } from '@/stores/sessions'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'
import type { ParkingSession, CreateParkingSessionDto, CompleteParkingSessionDto } from '@/types'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputNumber from 'primevue/inputnumber'
import Calendar from 'primevue/calendar'
import Tag from 'primevue/tag'
import ConfirmDialog from 'primevue/confirmdialog'

const sessionsStore = useSessionsStore()
const confirm = useConfirm()
const toast = useToast()

const filter = ref<'all' | 'active' | 'completed'>('all')
const showCreateDialog = ref(false)
const showCompleteDialog = ref(false)
const completingSession = ref<ParkingSession | null>(null)

const sessionForm = ref({
  vehicleId: null as number | null,
  spotId: null as number | null,
  operatorId: null as number | null
})

const completeForm = ref({
  endTime: null as Date | null
})

const filteredSessions = computed(() => {
  let sessions = sessionsStore.sessions
  
  if (filter.value === 'active') {
    sessions = sessionsStore.activeSessions
  } else if (filter.value === 'completed') {
    sessions = sessionsStore.completedSessions
  }
  
  return sessions
})

const formatDateTime = (dateString: string) => new Date(dateString).toLocaleString()

const fetchSessions = async () => {
  await sessionsStore.fetchAllSessions()
}

const saveSession = async () => {
  if (!sessionForm.value.vehicleId || !sessionForm.value.spotId || !sessionForm.value.operatorId) {
    toast.add({ severity: 'warn', summary: 'Validation', detail: 'Fill all fields', life: 2000 })
    return
  }
  try {
    const createData: CreateParkingSessionDto = {
      vehicleId: sessionForm.value.vehicleId!,
      spotId: sessionForm.value.spotId!,
      operatorId: sessionForm.value.operatorId!
    }
    await sessionsStore.createSession(createData)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Parking session started', life: 2000 })
    closeDialog()
  } catch (error) {
    // handled in store
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  sessionForm.value = { vehicleId: null, spotId: null, operatorId: null }
}

const completeSession = (session: ParkingSession) => {
  completingSession.value = session
  showCompleteDialog.value = true
}

const finishSession = async () => {
  if (!completingSession.value || !completeForm.value.endTime) return
  try {
    const completeData: CompleteParkingSessionDto = {
      sessionId: completingSession.value.id,
      endTime: completeForm.value.endTime.toISOString()
    }
    await sessionsStore.completeSession(completeData)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Parking session completed', life: 2000 })
    closeCompleteDialog()
  } catch (error) {
    // handled in store
  }
}

const closeCompleteDialog = () => {
  showCompleteDialog.value = false
  completingSession.value = null
  completeForm.value.endTime = null
}

const confirmDelete = (session: ParkingSession) => {
  confirm.require({
    message: `Are you sure you want to delete parking session ${session.id}?`,
    header: 'Delete Confirmation',
    icon: 'pi pi-exclamation-triangle',
    accept: () => deleteSession(session.id)
  })
}

const deleteSession = async (id: number) => {
  try {
    await sessionsStore.deleteSession(id)
  } catch (error) {
    // handled in store
  }
}

onMounted(fetchSessions)
</script>

<style scoped>
.sessions {
  padding: 1rem;
}
</style> 