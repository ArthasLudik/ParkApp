import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { parkingSpotApi } from '@/services/api'
import type { ParkingSpot, CreateParkingSpotDto, UpdateParkingSpotDto } from '@/types'
import { useToast } from 'primevue/usetoast'

export const useParkingStore = defineStore('parking', () => {
  const spots = ref<ParkingSpot[]>([])
  const loading = ref(false)
  const toast = useToast()

  const freeSpots = computed(() => spots.value.filter(spot => !spot.isOccupied))
  const occupiedSpots = computed(() => spots.value.filter(spot => spot.isOccupied))

  const fetchAllSpots = async () => {
    loading.value = true
    try {
      const response = await parkingSpotApi.getAll()
      spots.value = response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to fetch parking spots',
        life: 3000
      })
    } finally {
      loading.value = false
    }
  }

  const fetchFreeSpots = async () => {
    loading.value = true
    try {
      const response = await parkingSpotApi.getFree()
      spots.value = response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to fetch free spots',
        life: 3000
      })
    } finally {
      loading.value = false
    }
  }

  const createSpot = async (spotData: CreateParkingSpotDto) => {
    try {
      const response = await parkingSpotApi.create(spotData)
      spots.value.push(response.data)
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Parking spot created successfully',
        life: 3000
      })
      return response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to create parking spot',
        life: 3000
      })
      throw error
    }
  }

  const updateSpot = async (spotData: UpdateParkingSpotDto) => {
    try {
      const response = await parkingSpotApi.update(spotData)
      const index = spots.value.findIndex(spot => spot.spotId === spotData.spotId)
      if (index !== -1) {
        spots.value[index] = response.data
      }
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Parking spot updated successfully',
        life: 3000
      })
      return response.data
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to update parking spot',
        life: 3000
      })
      throw error
    }
  }

  const deleteSpot = async (id: number) => {
    try {
      await parkingSpotApi.delete(id)
      spots.value = spots.value.filter(spot => spot.spotId !== id)
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Parking spot deleted successfully',
        life: 3000
      })
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to delete parking spot',
        life: 3000
      })
      throw error
    }
  }

  return {
    spots,
    loading,
    freeSpots,
    occupiedSpots,
    fetchAllSpots,
    fetchFreeSpots,
    createSpot,
    updateSpot,
    deleteSpot
  }
}) 