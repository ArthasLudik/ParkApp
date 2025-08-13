<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Menubar from 'primevue/menubar'
import Toast from 'primevue/toast'
import ConfirmDialog from 'primevue/confirmdialog'

const router = useRouter()

const menuItems = ref([
  {
    label: 'Dashboard',
    icon: 'pi pi-home',
    command: () => router.push('/')
  },
  {
    label: 'Parking Spots',
    icon: 'pi pi-map-marker',
    command: () => router.push('/spots')
  },
  {
    label: 'Sessions',
    icon: 'pi pi-clock',
    command: () => router.push('/sessions')
  },
  {
    label: 'Users',
    icon: 'pi pi-users',
    command: () => router.push('/users')
  },
  {
    label: 'Vehicles',
    icon: 'pi pi-car',
    command: () => router.push('/vehicles')
  }
])
</script>

<template>
  <div id="app">
    <Toast />
    <ConfirmDialog />
    
    <Menubar :model="menuItems" class="mb-4">
      <template #start>
        <div class="flex align-items-center">
          <i class="pi pi-car text-xl mr-2"></i>
          <span class="font-bold text-xl">Parking System</span>
        </div>
      </template>
    </Menubar>

    <div class="container mx-auto px-4">
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
#app {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.container {
  max-width: 1200px;
}
</style>
