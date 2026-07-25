<template>
  <div class="carpool-page">
    <!-- 地图容器 -->
    <div id="carpool-map" class="map-container"></div>

    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <!-- 角色切换 -->
      <div class="role-switch">
        <button
            :class="{ active: role === 'passenger' }"
            @click="role = 'passenger'"
        >
          🚶 我是乘客
        </button>
        <button
            :class="{ active: role === 'driver' }"
            @click="role = 'driver'"
        >
          🚗 我是司机
        </button>
      </div>

      <!-- 乘客面板 -->
      <div v-if="role === 'passenger'" class="panel">
        <h3>查找顺风车</h3>

        <PlacePicker
          ref="passengerStartPickerRef"
          v-model="passengerForm.startAddress"
          v-model:city="passengerStartCity"
          show-city
          label="📍 出发地"
          full-width
          placeholder="在出发城市内搜索关键词"
          :selected="passengerForm.startPoint"
          @select="onPassengerStartSelect"
          @clear="passengerForm.startPoint = null"
        >
          <template #extra>
            <button @click="locateMe('passengerStart')">定位</button>
          </template>
        </PlacePicker>

        <PlacePicker
          ref="passengerEndPickerRef"
          v-model="passengerForm.endAddress"
          v-model:city="passengerEndCity"
          show-city
          label="🏁 目的地"
          full-width
          placeholder="在目的城市内搜索关键词"
          :selected="passengerForm.endPoint"
          @select="onPassengerEndSelect"
          @clear="passengerForm.endPoint = null"
        />

        <div class="input-group">
          <label>🕐 出发时间</label>
          <input type="datetime-local" v-model="passengerForm.departTime" />
        </div>

        <button @click="searchTrips" :disabled="searching">
          {{ searching ? '搜索中...' : '查找顺路车' }}
        </button>

        <!-- 搜索结果 -->
        <div v-if="tripResults.length > 0" class="trip-list">
          <div
              v-for="trip in tripResults"
              :key="trip.id"
              class="trip-card"
              @click="selectTrip(trip)"
          >
            <div class="driver-info">
              <img :src="trip.driverAvatar" class="avatar" />
              <span>{{ trip.driverName }}</span>
              <span class="rating">⭐ {{ trip.driverRating }}</span>
            </div>
            <div class="route-info">
              <p>🛣️ {{ trip.distanceText }} | ⏱️ {{ trip.durationText }}</p>
              <p>💰 ¥{{ trip.price }} | 🪑 剩余{{ trip.seatsLeft }}座</p>
              <p>🕐 {{ formatTime(trip.departTime) }}</p>
            </div>
            <button @click.stop="bookTrip(trip.id)">立即预订</button>
          </div>
        </div>
      </div>

      <!-- 司机面板 -->
      <div v-else class="panel">
        <h3>发布行程</h3>

        <PlacePicker
          ref="driverStartPickerRef"
          v-model="driverForm.startAddress"
          v-model:city="driverStartCity"
          show-city
          label="📍 起点"
          full-width
          placeholder="在起点城市内搜索关键词"
          :selected="driverForm.startPoint"
          @select="onDriverStartSelect"
          @clear="onDriverStartClear"
        >
          <template #extra>
            <button @click="locateMe('driverStart')">定位</button>
          </template>
        </PlacePicker>

        <PlacePicker
          ref="driverEndPickerRef"
          v-model="driverForm.endAddress"
          v-model:city="driverEndCity"
          show-city
          label="🏁 终点"
          full-width
          placeholder="在终点城市内搜索关键词"
          :selected="driverForm.endPoint"
          @select="onDriverEndSelect"
          @clear="onDriverEndClear"
        />

        <div class="input-group">
          <label>⏰ 出发时间</label>
          <input type="datetime-local" v-model="driverForm.departTime" />
        </div>

        <div class="input-group">
          <label>🪑 空座位数</label>
          <input type="number" v-model="driverForm.seats" min="1" max="6" />
        </div>

        <div class="input-group">
          <label>💰 每人价格</label>
          <input type="number" v-model="driverForm.price" />
        </div>

        <button @click="publishTripHandler" :disabled="publishing">
          {{ publishing ? '发布中...' : '发布行程' }}
        </button>

        <!-- 路线预览 -->
        <div v-if="driverRoute" class="route-preview">
          <p>📏 总距离: {{ driverRoute.distanceText }}</p>
          <p>⏱️ 预计时间: {{ driverRoute.durationText }}</p>
        </div>
      </div>
    </div>

    <!-- 实时位置面板 -->
    <div v-if="currentTrip" class="location-panel">
      <h4>📍 实时位置</h4>
      <p>{{ currentLocation?.address }}</p>
      <button @click="startTracking" v-if="!isTracking">开始导航</button>
      <button @click="stopTracking" v-else>结束导航</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import PlacePicker from '@/components/PlacePicker.vue'
import { baiduMapService } from '@/utils/baiduMapService'
import { BAIDU_MAP_CONFIG } from '@/config/map'
import {
  publishTrip,
  searchTrips as apiSearchTrips,
  matchTrips,
  bookSeat,
  updateLocation
} from '@/api/carpool'

// 状态
const role = ref('passenger')
const sidebarCollapsed = ref(false)
const searching = ref(false)
const publishing = ref(false)
const tripResults = ref([])
const currentTrip = ref(null)
const isTracking = ref(false)
const currentLocation = ref(null)
let watchId = null

// 乘客表单
const passengerForm = ref({
  startAddress: '',
  endAddress: '',
  startPoint: null,
  endPoint: null,
  departTime: ''
})

// 司机表单
const driverForm = ref({
  startAddress: '',
  endAddress: '',
  startPoint: null,
  endPoint: null,
  departTime: '',
  seats: 3,
  price: 0
})

const driverRoute = ref(null)
const passengerStartCity = ref(BAIDU_MAP_CONFIG.DEFAULT_CITY)
const passengerEndCity = ref(BAIDU_MAP_CONFIG.DEFAULT_CITY)
const driverStartCity = ref(BAIDU_MAP_CONFIG.DEFAULT_CITY)
const driverEndCity = ref(BAIDU_MAP_CONFIG.DEFAULT_CITY)
const passengerStartPickerRef = ref(null)
const passengerEndPickerRef = ref(null)
const driverStartPickerRef = ref(null)
const driverEndPickerRef = ref(null)

// 初始化
onMounted(async () => {
  await baiduMapService.initMap('carpool-map')
})

const onPassengerStartSelect = (place) => {
  passengerForm.value.startPoint = place
  baiduMapService.clearOverlays()
  baiduMapService.addMarker(place.lng, place.lat, { label: '起点' })
}

const onPassengerEndSelect = (place) => {
  passengerForm.value.endPoint = place
}

const onDriverStartSelect = (place) => {
  driverForm.value.startPoint = place
  if (driverForm.value.endPoint) {
    previewDriverRoute()
  }
}

const onDriverEndSelect = (place) => {
  driverForm.value.endPoint = place
  if (driverForm.value.startPoint) {
    previewDriverRoute()
  }
}

const onDriverStartClear = () => {
  driverForm.value.startPoint = null
  driverRoute.value = null
}

const onDriverEndClear = () => {
  driverForm.value.endPoint = null
  driverRoute.value = null
}

// ========== 定位 ==========

const locateMe = async (target) => {
  try {
    const pos = await baiduMapService.getCurrentPosition()
    const address = await baiduMapService.reverseGeocode(pos.lng, pos.lat)
    const place = {
      lng: pos.lng,
      lat: pos.lat,
      name: address.address,
      address: address.address,
    }

    if (target === 'passengerStart') {
      place.city = passengerStartCity.value
      passengerForm.value.startPoint = place
      passengerStartPickerRef.value?.setSelected(place, address.address)
      baiduMapService.clearOverlays()
      baiduMapService.addMarker(pos.lng, pos.lat, { label: '起点' })
    } else if (target === 'driverStart') {
      place.city = driverStartCity.value
      driverForm.value.startPoint = place
      driverStartPickerRef.value?.setSelected(place, address.address)
      baiduMapService.addMarker(pos.lng, pos.lat, { label: '起点' })
      if (driverForm.value.endPoint) {
        previewDriverRoute()
      }
    }
  } catch (err) {
    alert('定位失败: ' + err.message)
  }
}

// ========== 路线预览 ==========

const previewDriverRoute = async () => {
  if (!driverForm.value.startPoint || !driverForm.value.endPoint) return

  try {
    baiduMapService.clearOverlays()

    const route = await baiduMapService.planDrivingRoute(
        driverForm.value.startPoint,
        driverForm.value.endPoint
    )

    driverRoute.value = route

    // 绘制路线
    baiduMapService.drawRoute(route.path)

    // 添加标记
    baiduMapService.addMarker(
        driverForm.value.startPoint.lng,
        driverForm.value.startPoint.lat,
        { label: '起点', color: 'green' }
    )
    baiduMapService.addMarker(
        driverForm.value.endPoint.lng,
        driverForm.value.endPoint.lat,
        { label: '终点', color: 'red' }
    )

    // 调整视野
    baiduMapService.setViewport([
      driverForm.value.startPoint,
      driverForm.value.endPoint
    ])

    // 自动计算建议价格
    const distanceKm = route.distance / 1000
    driverForm.value.price = Math.ceil(distanceKm * 1.5) // 1.5元/公里

  } catch (err) {
    console.error('路线规划失败:', err)
  }
}

// ========== 发布行程 ==========

const publishTripHandler = async () => {
  if (!driverForm.value.startPoint || !driverForm.value.endPoint) {
    alert('请从列表中选择起点和终点的具体位置')
    return
  }

  publishing.value = true
  try {
    const route = driverRoute.value
    await publishTrip({
      start: driverForm.value.startPoint,
      end: driverForm.value.endPoint,
      departTime: driverForm.value.departTime,
      seats: driverForm.value.seats,
      price: driverForm.value.price,
      distance: route.distance,
      duration: route.duration,
      routePath: route.path.map(p => ({ lng: p.lng, lat: p.lat }))
    })

    alert('行程发布成功！')
  } catch (err) {
    alert('发布失败: ' + err.message)
  } finally {
    publishing.value = false
  }
}

// ========== 搜索行程 ==========

const searchTrips = async () => {
  if (!passengerForm.value.startPoint || !passengerForm.value.endPoint) {
    alert('请从列表中选择出发地和目的地的具体位置')
    return
  }

  searching.value = true
  try {
    // 1. 先规划乘客的路线
    const passengerRoute = await baiduMapService.planDrivingRoute(
        passengerForm.value.startPoint,
        passengerForm.value.endPoint
    )

    baiduMapService.clearOverlays()
    baiduMapService.drawRoute(passengerRoute.path, { color: '#ccc', weight: 2 })

    // 2. 调用后端匹配算法
    const { data } = await matchTrips({
      originLng: passengerForm.value.startPoint.lng,
      originLat: passengerForm.value.startPoint.lat,
      destLng: passengerForm.value.endPoint.lng,
      destLat: passengerForm.value.endPoint.lat,
      maxDistance: 3000,  // 接人距离3km内
      timeWindow: 3600    // 时间窗口1小时
    })

    tripResults.value = data

    // 3. 在地图上显示结果
    data.forEach(trip => {
      baiduMapService.addMarker(
          trip.startPoint.lng,
          trip.startPoint.lat,
          {
            label: `¥${trip.price}`,
            onClick: () => showTripDetail(trip)
          }
      )
    })

  } catch (err) {
    alert('搜索失败: ' + err.message)
  } finally {
    searching.value = false
  }
}

// ========== 预订 ==========

const bookTrip = async (tripId) => {
  try {
    await bookSeat(tripId, {
      passengerId: localStorage.getItem('userId'),
      pickupPoint: passengerForm.value.startPoint,
      dropoffPoint: passengerForm.value.endPoint
    })
    alert('预订成功！')
  } catch (err) {
    alert('预订失败: ' + err.message)
  }
}

// ========== 实时追踪 ==========

let lastTrackAddressFetch = 0

const startTracking = () => {
  isTracking.value = true
  watchId = navigator.geolocation.watchPosition(
      async (position) => {
        const { longitude, latitude } = position.coords
        try {
          const baiduPoint = await baiduMapService.convertWgs84ToBd09(longitude, latitude)

          let address = `${baiduPoint.lng.toFixed(6)}, ${baiduPoint.lat.toFixed(6)}`
          const now = Date.now()
          if (now - lastTrackAddressFetch > 10000) {
            lastTrackAddressFetch = now
            const result = await baiduMapService.reverseGeocode(baiduPoint.lng, baiduPoint.lat)
            address = result.address
          }

          currentLocation.value = {
            lng: baiduPoint.lng,
            lat: baiduPoint.lat,
            address
          }

          if (currentTrip.value) {
            await updateLocation(currentTrip.value.id, currentLocation.value)
          }

          baiduMapService.clearOverlays()
          baiduMapService.addMarker(baiduPoint.lng, baiduPoint.lat, {
            label: '当前位置'
          })
        } catch (err) {
          console.error('位置更新失败:', err)
        }
      },
      (err) => console.error('定位错误:', err),
      { enableHighAccuracy: true, maximumAge: 5000 }
  )
}

const stopTracking = () => {
  isTracking.value = false
  if (watchId !== null) {
    navigator.geolocation.clearWatch(watchId)
    watchId = null
  }
}

// ========== 工具函数 ==========

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleString('zh-CN')
}

onUnmounted(() => {
  stopTracking()
})
</script>

<style scoped>
.carpool-page {
  display: flex;
  height: 100vh;
}

.map-container {
  flex: 1;
  height: 100%;
}

.sidebar {
  width: 380px;
  background: #fff;
  box-shadow: -2px 0 8px rgba(0,0,0,0.1);
  padding: 20px;
  overflow-y: auto;
}

.sidebar.collapsed {
  width: 60px;
}

.role-switch {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.role-switch button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  background: #f0f0f0;
}

.role-switch button.active {
  background: #1890ff;
  color: white;
}

.input-group {
  margin-bottom: 15px;
}

.input-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  color: #666;
}

.input-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.city-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.city-group label {
  margin-bottom: 0;
  white-space: nowrap;
}

.city-group input {
  flex: 1;
}

.trip-list {
  margin-top: 20px;
}

.trip-card {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
}

.trip-card:hover {
  background: #f5f5f5;
}

.driver-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.location-panel {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
}
</style>