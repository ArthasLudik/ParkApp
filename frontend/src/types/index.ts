export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN'
}

export enum SpotType {
  REGULAR = 'REGULAR',
  DISABLED = 'DISABLED',
  ELECTRIC = 'ELECTRIC'
}

export enum VehicleType {
  CAR = 'CAR',
  MOTORCYCLE = 'MOTORCYCLE',
  TRUCK = 'TRUCK'
}

export interface User {
  id: number
  name: string
  role: UserRole
}

export interface CreateUserDto {
  name: string
  role: UserRole
}

export interface ParkingSpot {
  spotId: number
  spotNumber: string
  type: SpotType
  isOccupied: boolean
  hourlyRate: number
  status: string
}

export interface CreateParkingSpotDto {
  spotNumber: string
  type: SpotType
  hourlyRate: number
}

export interface UpdateParkingSpotDto {
  spotId: number
  spotNumber: string
  type: SpotType
  hourlyRate: number
}

export interface Vehicle {
  id: number
  licensePlate: string
  type: VehicleType
  userId?: number
}

export interface CreateVehicleDto {
  licensePlate: string
  type: VehicleType
  userId?: number
}

export interface ParkingSession {
  id: number
  vehicleId: number
  spotId: number
  operatorId: number
  startTime: string
  endTime?: string
  totalCost?: number
  isActive: boolean
}

export interface CreateParkingSessionDto {
  vehicleId: number
  spotId: number
  operatorId: number
}

export interface CompleteParkingSessionDto {
  sessionId: number
  endTime: string
} 