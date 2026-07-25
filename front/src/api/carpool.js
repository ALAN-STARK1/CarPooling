import axios from 'axios'
import { BACKEND_API } from '@/config/map'

const api = axios.create({
    baseURL: BACKEND_API,
    timeout: 10000
})

// 请求拦截器添加 token
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

/**
 * 发布行程
 */
export const publishTrip = (data) => api.post('/carpool/trip', {
    startPoint: data.start,
    endPoint: data.end,
    waypoints: data.waypoints || [],
    departTime: data.departTime,
    seats: data.seats,
    price: data.price,
    distance: data.distance,
    duration: data.duration,
    routePath: data.routePath
})

/**
 * 搜索附近行程
 */
export const searchTrips = (params) => api.get('/carpool/trips/search', {
    params: {
        lng: params.lng,
        lat: params.lat,
        destinationLng: params.destinationLng,
        destinationLat: params.destinationLat,
        radius: params.radius || 5000,  // 默认5公里
        departTime: params.departTime
    }
})

/**
 * 匹配顺路行程
 */
export const matchTrips = (data) => api.post('/carpool/match', {
    originLng: data.originLng,
    originLat: data.originLat,
    destLng: data.destLng,
    destLat: data.destLat,
    maxDistance: data.maxDistance || 2000,  // 最大接人距离2km
    timeWindow: data.timeWindow || 1800     // 时间窗口30分钟
})

/**
 * 预订座位
 */
export const bookSeat = (tripId, passengerInfo) =>
    api.post(`/carpool/trip/${tripId}/book`, passengerInfo)

/**
 * 获取行程详情
 */
export const getTripDetail = (tripId) =>
    api.get(`/carpool/trip/${tripId}`)

/**
 * 实时位置上报
 */
export const updateLocation = (tripId, location) =>
    api.post(`/carpool/trip/${tripId}/location`, {
        lng: location.lng,
        lat: location.lat,
        timestamp: Date.now()
    })