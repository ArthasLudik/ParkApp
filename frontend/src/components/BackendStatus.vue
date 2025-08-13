<template>
  <div class="backend-status">
    <div v-if="!isConnected" class="status-warning">
      <i class="pi pi-exclamation-triangle"></i>
      <span>Backend недоступен. Убедитесь, что Spring Boot сервер запущен на порту 8080.</span>
      <Button label="Проверить соединение" icon="pi pi-refresh" size="small" @click="checkConnection" />
    </div>
    <div v-else class="status-success">
      <i class="pi pi-check-circle"></i>
      <span>Backend подключен</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import Button from 'primevue/button'

const isConnected = ref(false)
const checking = ref(false)

const checkConnection = async () => {
  checking.value = true
  try {
    await api.get('/spots/all')
    isConnected.value = true
  } catch (error) {
    isConnected.value = false
  } finally {
    checking.value = false
  }
}

onMounted(() => {
  checkConnection()
})
</script>

<style scoped>
.backend-status {
  margin-bottom: 1rem;
}

.status-warning {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  color: #856404;
}

.status-success {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  color: #155724;
}

.status-warning i,
.status-success i {
  font-size: 1.2rem;
}
</style> 