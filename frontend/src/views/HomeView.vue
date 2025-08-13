<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useParkingStore } from '@/stores/parking'
import { useSessionsStore } from '@/stores/sessions'
import BackendStatus from '@/components/BackendStatus.vue'
import Card from 'primevue/card'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'

const parkingStore = useParkingStore()
const sessionsStore = useSessionsStore()

const loading = ref(false)

const stats = computed(() => ({
  totalSpots: parkingStore.spots.length,
  freeSpots: parkingStore.freeSpots.length,
  occupiedSpots: parkingStore.occupiedSpots.length,
  activeSessions: sessionsStore.activeSessions.length
}))

const recentSpots = computed(() => 
  parkingStore.spots.slice(0, 5)
)

const activeSessions = computed(() => 
  sessionsStore.activeSessions.slice(0, 5)
)

const formatDateTime = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    parkingStore.fetchAllSpots(),
    sessionsStore.fetchAllSessions()
  ])
  loading.value = false
})
</script>

<template>
  <div class="home">
    <BackendStatus />
    <h1 class="text-3xl font-bold mb-6">Parking System Dashboard</h1>
    
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <Card class="stat-card">
        <template #content>
          <div class="flex align-items-center justify-content-between">
            <div>
              <div class="text-500 font-medium">Total Spots</div>
              <div class="text-900 font-bold text-xl">{{ stats.totalSpots }}</div>
            </div>
            <i class="pi pi-map-marker text-blue-500 text-3xl"></i>
          </div>
        </template>
      </Card>

      <Card class="stat-card">
        <template #content>
          <div class="flex align-items-center justify-content-between">
            <div>
              <div class="text-500 font-medium">Free Spots</div>
              <div class="text-900 font-bold text-xl">{{ stats.freeSpots }}</div>
            </div>
            <i class="pi pi-check-circle text-green-500 text-3xl"></i>
          </div>
        </template>
      </Card>

      <Card class="stat-card">
        <template #content>
          <div class="flex align-items-center justify-content-between">
            <div>
              <div class="text-500 font-medium">Active Sessions</div>
              <div class="text-900 font-bold text-xl">{{ stats.activeSessions }}</div>
            </div>
            <i class="pi pi-clock text-orange-500 text-3xl"></i>
          </div>
        </template>
      </Card>

      <Card class="stat-card">
        <template #content>
          <div class="flex align-items-center justify-content-between">
            <div>
              <div class="text-500 font-medium">Occupied Spots</div>
              <div class="text-900 font-bold text-xl">{{ stats.occupiedSpots }}</div>
            </div>
            <i class="pi pi-times-circle text-red-500 text-3xl"></i>
          </div>
        </template>
      </Card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <Card>
        <template #title>
          <div class="flex align-items-center">
            <i class="pi pi-map-marker mr-2"></i>
            Recent Parking Spots
          </div>
        </template>
        <template #content>
          <DataTable :value="recentSpots" :loading="loading" stripedRows>
            <Column field="spotNumber" header="Spot Number"></Column>
            <Column field="type" header="Type"></Column>
            <Column field="status" header="Status">
              <template #body="slotProps">
                <Tag :value="slotProps.data.status" 
                     :severity="slotProps.data.status === 'FREE' ? 'success' : 'danger'" />
              </template>
            </Column>
            <Column field="hourlyRate" header="Rate">
              <template #body="slotProps">
                ${{ slotProps.data.hourlyRate }}/hr
              </template>
            </Column>
          </DataTable>
        </template>
      </Card>

      <Card>
        <template #title>
          <div class="flex align-items-center">
            <i class="pi pi-clock mr-2"></i>
            Active Sessions
          </div>
        </template>
        <template #content>
          <DataTable :value="activeSessions" :loading="loading" stripedRows>
            <Column field="id" header="Session ID"></Column>
            <Column field="vehicleId" header="Vehicle ID"></Column>
            <Column field="spotId" header="Spot ID"></Column>
            <Column field="startTime" header="Start Time">
              <template #body="slotProps">
                {{ formatDateTime(slotProps.data.startTime) }}
              </template>
            </Column>
          </DataTable>
        </template>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.home {
  padding: 1rem;
}

.stat-card {
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}
</style>
