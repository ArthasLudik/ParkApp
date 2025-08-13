import axios from 'axios'
import type {
  User,
  CreateUserDto,
  ParkingSpot,
  CreateParkingSpotDto,
  UpdateParkingSpotDto,
  Vehicle,
  CreateVehicleDto,
  ParkingSession,
  CreateParkingSessionDto,
  CompleteParkingSessionDto,
  UserRole,
  SpotType,
  VehicleType
} from '@/types'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

api.interceptors.request.use(
  (config) => {
    console.log(`Making request to: ${config.url}`)
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(error)
)

// ===== Helpers: mapping frontend <-> backend =====
const mapUserRoleToBackend = (role?: UserRole): 'ADMIN' | 'OPERATOR' => {
  if (role === 'ADMIN') return 'ADMIN'
  return 'OPERATOR'
}

const ensureCreateUserPayload = (data: CreateUserDto) => {
  if (!(data as any).name) throw new Error('Username is required')
  return {
    username: (data as any).name,
    passwordHash: (data as any).passwordHash ?? 'password123',
    role: mapUserRoleToBackend(data.role)
  }
}

const mapSpotTypeToBackend = (type?: SpotType): 'STANDARD' | 'DISABLED' | 'ELECTRIC' | 'TRUCK' => {
  switch (type) {
    case 'DISABLED':
      return 'DISABLED'
    case 'ELECTRIC':
      return 'ELECTRIC'
    default:
      return 'STANDARD'
  }
}

const ensureCreateSpotPayload = (data: CreateParkingSpotDto) => {
  const number = (data as any).spotNumber ?? `SPOT-${Date.now()}`
  const hourlyRate = data.hourlyRate ?? 1
  return {
    id: (data as any).id ?? Date.now(),
    number,
    spotType: mapSpotTypeToBackend((data as any).type),
    isOccupied: (data as any).isOccupied ?? false,
    hourlyRate
  }
}

const ensureUpdateSpotPayload = (data: UpdateParkingSpotDto) => {
  if (!(data as any).spotId) throw new Error('spotId is required')
  const number = (data as any).spotNumber ?? ''
  const hourlyRate = data.hourlyRate ?? 1
  return {
    spotId: (data as any).spotId,
    number,
    spotType: mapSpotTypeToBackend((data as any).type),
    isOccupied: (data as any).isOccupied ?? false,
    hourlyRate
  }
}

const ensureCreateVehiclePayload = (data: CreateVehicleDto) => {
  return {
    id: (data as any).id ?? Date.now(),
    licensePlate: data.licensePlate ?? '',
    model: (data as any).model ?? '',
    color: (data as any).color ?? '',
    vehicleType: (data.type as unknown as VehicleType) ?? 'CAR'
  }
}

const ensureCreateSessionPayload = (data: CreateParkingSessionDto) => {
  if (!data.vehicleId || !data.spotId) throw new Error('vehicleId and spotId are required')
  return {
    id: (data as any).id ?? Date.now(),
    vehicleId: data.vehicleId,
    spotId: data.spotId,
    operatorId: (data as any).operatorId ?? 1
    // entryTime не отправляем — сервер сам выставит (@PrePersist)
  }
}

const ensureCompleteSessionPayload = (data: CompleteParkingSessionDto) => {
  const sessionId = (data as any).sessionId
  if (!sessionId) throw new Error('sessionId is required')
  return {
    id: sessionId,
    sessionId
  }
}

// Backend -> Frontend mappers
const normalizeSpotTypeFromBackend = (t: string): SpotType => {
  if (t === 'DISABLED') return 'DISABLED' as SpotType
  if (t === 'ELECTRIC') return 'ELECTRIC' as SpotType
  return 'REGULAR' as SpotType
}

const mapSpotFromBackend = (b: any): ParkingSpot => ({
  spotId: b.spotId ?? b.id ?? 0,
  spotNumber: b.spotNumber ?? b.number ?? '',
  type: normalizeSpotTypeFromBackend(b.type ?? b.spotType ?? 'STANDARD'),
  isOccupied: !!(b.isOccupied ?? b.occupied),
  hourlyRate: Number(b.hourlyRate ?? 0),
  status: b.status ?? ((b.isOccupied ?? b.occupied) ? 'OCCUPIED' : 'FREE')
})

const mapSessionFromBackend = (b: any): ParkingSession => ({
  id: b.sessionId ?? b.id ?? 0,
  vehicleId: b.vehicleId ?? 0,
  spotId: b.spotId ?? 0,
  operatorId: b.operatorId ?? 0,
  startTime: b.entryTime ?? b.startTime ?? '',
  endTime: b.exitTime ?? b.endTime,
  totalCost: b.paidAmount ?? b.totalCost,
  isActive: !b.exitTime
})

const mapVehicleFromBackend = (b: any): Vehicle => ({
  id: b.id,
  licensePlate: b.licensePlate,
  type: b.type as VehicleType,
  userId: (b as any).userId
})

const mapArray = <T>(arr: any[], mapper: (x: any) => T): T[] => (arr || []).map(mapper)

export const userApi = {
  create: (data: CreateUserDto) => api.post<User>('/user', ensureCreateUserPayload(data)).then(r => r),
  getById: (id: number) => api.get<User>(`/user/${id}`),
  getAll: () => api.get<User[]>('/user/all'),
  delete: (id: number) => api.delete(`/user/${id}`),
  updateRole: (id: number, role: UserRole) => api.patch<User>(`/user/${id}`, mapUserRoleToBackend(role))
}

export const parkingSpotApi = {
  create: (data: CreateParkingSpotDto) => api.post('/spots', ensureCreateSpotPayload(data)).then(r => ({ ...r, data: mapSpotFromBackend(r.data) })),
  getAll: () => api.get('/spots/all').then(r => ({ ...r, data: mapArray<ParkingSpot>(r.data, mapSpotFromBackend) })),
  getById: (id: number) => api.get(`/spots/${id}`).then(r => ({ ...r, data: mapSpotFromBackend(r.data) })),
  getFree: () => api.get('/spots/free').then(r => ({ ...r, data: mapArray<ParkingSpot>(r.data, mapSpotFromBackend) })),
  update: (data: UpdateParkingSpotDto) => api.put('/spots', ensureUpdateSpotPayload(data)).then(r => ({ ...r, data: mapSpotFromBackend(r.data) })),
  delete: (id: number) => api.delete(`/spots/${id}`)
}

export const vehicleApi = {
  create: (data: CreateVehicleDto) => api.post('/vehicles', ensureCreateVehiclePayload(data)).then(r => ({ ...r, data: mapVehicleFromBackend(r.data) })),
  getAll: () => api.get('/vehicles/all').then(r => ({ ...r, data: mapArray<Vehicle>(r.data, mapVehicleFromBackend) })),
  delete: (id: number) => api.delete(`/vehicles/${id}`)
}

export const parkingSessionApi = {
  create: (data: CreateParkingSessionDto) => api.post('/session', ensureCreateSessionPayload(data)).then(r => ({ ...r, data: mapSessionFromBackend(r.data) })),
  getAll: () => api.get('/session').then(r => ({ ...r, data: mapArray<ParkingSession>(r.data, mapSessionFromBackend) })),
  getById: (id: number) => api.get(`/session/${id}`).then(r => ({ ...r, data: mapSessionFromBackend(r.data) })),
  complete: (data: CompleteParkingSessionDto) => api.put('/session', ensureCompleteSessionPayload(data)).then(r => ({ ...r, data: mapSessionFromBackend(r.data) })),
  delete: (id: number) => api.delete(`/session/${id}`)
}

export default api
