import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { parkingSessionApi } from '@/services/api'
import type { ParkingSession, CreateParkingSessionDto, CompleteParkingSessionDto } from '@/types'
import { useToast } from 'primevue/usetoast'

export const useSessionsStore = defineStore('sessions', () => {
  const sessions = ref<ParkingSession[]>([])
  const loading = ref(false)
  const toast = useToast()

  const activeSessions = computed(() => sessions.value.filter(session => session.isActive))
  const completedSessions = computed(() => sessions.value.filter(session => !session.isActive))

  const fetchAllSessions = async () => {
    loading.value = true
    try {
      const response = await parkingSessionApi.getAll()
      sessions.value = response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to fetch parking sessions',
        life: 3000
      })
    } finally {
      loading.value = false
    }
  }

  const createSession = async (sessionData: CreateParkingSessionDto) => {
    try {
      const response = await parkingSessionApi.create(sessionData)
      sessions.value.push(response.data)
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Parking session started successfully',
        life: 3000
      })
      return response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to start parking session',
        life: 3000
      })
      throw error
    }
  }

  const completeSession = async (sessionData: CompleteParkingSessionDto) => {
    try {
      const response = await parkingSessionApi.complete(sessionData)
      const index = sessions.value.findIndex(session => session.id === sessionData.sessionId)
      if (index !== -1) {
        sessions.value[index] = response.data
      }
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Parking session completed successfully',
        life: 3000
      })
      return response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to complete parking session',
        life: 3000
      })
      throw error
    }
  }

  const deleteSession = async (id: number) => {
    try {
      await parkingSessionApi.delete(id)
      sessions.value = sessions.value.filter(session => session.id !== id)
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Parking session deleted successfully',
        life: 3000
      })
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to delete parking session',
        life: 3000
      })
      throw error
    }
  }

  return {
    sessions,
    loading,
    activeSessions,
    completedSessions,
    fetchAllSessions,
    createSession,
    completeSession,
    deleteSession
  }
}) 