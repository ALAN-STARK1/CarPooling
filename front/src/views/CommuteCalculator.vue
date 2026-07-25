<template>
  <div class="commute-calculator">
    <div id="map-container" style="width: 100%; height: 400px;"></div>

    <div class="address-inputs">
      <section class="place-block">
        <h4>起点</h4>
        <PlacePicker
          ref="startPickerRef"
          v-model="startAddress"
          v-model:city="startCity"
          show-city
          label="关键词"
          placeholder="输入关键词，在起点城市内搜索"
          :selected="startPoint"
          @select="onStartSelect"
          @clear="onStartClear"
        >
          <template #extra>
            <button @click="locateStart">📍 定位</button>
          </template>
        </PlacePicker>
      </section>

      <section class="place-block">
        <h4>终点</h4>
        <PlacePicker
          ref="endPickerRef"
          v-model="endAddress"
          v-model:city="endCity"
          show-city
          label="关键词"
          placeholder="输入关键词，在终点城市内搜索"
          :selected="endPoint"
          @select="onEndSelect"
          @clear="onEndClear"
        />
      </section>

      <button @click="calculateRoute" :disabled="calculating">
        {{ calculating ? '计算中...' : '计算通勤时间' }}
      </button>
    </div>

    <div v-if="routeInfo" class="result">
      <div class="info-card">
        <h3>🚗 驾车路线</h3>
        <p><strong>起点：</strong>{{ routeInfo.startLabel }}</p>
        <p><strong>终点：</strong>{{ routeInfo.endLabel }}</p>
        <p><strong>距离：</strong>{{ routeInfo.distance }}</p>
        <p><strong>预计时间：</strong>{{ routeInfo.duration }}</p>
        <p><strong>详细路线：</strong>{{ routeInfo.description }}</p>
      </div>
    </div>

    <div class="realtime-location" v-if="currentLocation">
      <h4>📍 当前位置</h4>
      <p>{{ currentLocation.address }}</p>
      <p>坐标：{{ currentLocation.lng.toFixed(6) }}, {{ currentLocation.lat.toFixed(6) }}</p>
    </div>

    <div class="tracking-controls">
      <button @click="startTracking" v-if="!isTracking">开始实时追踪</button>
      <button @click="stopTracking" v-else>停止追踪</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import PlacePicker from '@/components/PlacePicker.vue'
import { baiduMap } from '@/utils/baiduMap'
import { BAIDU_MAP_CONFIG } from '@/config/map'

const startCity = ref(BAIDU_MAP_CONFIG.DEFAULT_CITY)
const endCity = ref(BAIDU_MAP_CONFIG.DEFAULT_CITY)
const startAddress = ref('')
const endAddress = ref('')
const startPoint = ref(null)
const endPoint = ref(null)
const routeInfo = ref(null)
const calculating = ref(false)
const currentLocation = ref(null)
const isTracking = ref(false)
const startPickerRef = ref(null)
const endPickerRef = ref(null)
let watchId = null

const onStartSelect = (place) => {
  startPoint.value = place
  routeInfo.value = null
}

const onEndSelect = (place) => {
  endPoint.value = place
  routeInfo.value = null
}

const onStartClear = () => {
  startPoint.value = null
  routeInfo.value = null
}

const onEndClear = () => {
  endPoint.value = null
  routeInfo.value = null
}

onMounted(async () => {
  try {
    await baiduMap.initMap('map-container')
  } catch (err) {
    console.error('地图初始化失败:', err)
    alert('地图加载失败：' + err.message)
  }
})

const locateStart = async () => {
  if (!startCity.value?.trim()) {
    alert('请先填写起点城市')
    return
  }
  try {
    const pos = await baiduMap.getCurrentPosition()
    const place = {
      lng: pos.lng,
      lat: pos.lat,
      name: pos.address,
      address: pos.address,
      city: startCity.value,
    }
    startPoint.value = place
    currentLocation.value = pos
    startPickerRef.value?.setSelected(place, pos.address)
  } catch (err) {
    alert('定位失败：' + err.message)
  }
}

const calculateRoute = async () => {
  if (!startPoint.value) {
    alert('请先确认起点城市，再定位或从列表选择起点')
    return
  }
  if (!endPoint.value) {
    alert('请先确认终点城市，再从列表选择终点')
    return
  }

  calculating.value = true
  try {
    const route = await baiduMap.getDrivingRoute(startPoint.value, endPoint.value)
    routeInfo.value = {
      ...route,
      startLabel: `${startPoint.value.name}（${startPoint.value.lat.toFixed(4)}, ${startPoint.value.lng.toFixed(4)}）`,
      endLabel: `${endPoint.value.name}（${endPoint.value.lat.toFixed(4)}, ${endPoint.value.lng.toFixed(4)}）`,
    }
  } catch (err) {
    alert('路线计算失败：' + err.message)
  } finally {
    calculating.value = false
  }
}

const startTracking = () => {
  isTracking.value = true
  watchId = baiduMap.watchPosition(
    (position) => {
      currentLocation.value = position
    },
    () => stopTracking()
  )
}

const stopTracking = () => {
  isTracking.value = false
  if (watchId != null) {
    baiduMap.clearWatch(watchId)
    watchId = null
  }
}

onUnmounted(() => {
  stopTracking()
})
</script>

<style scoped>
.commute-calculator {
  padding: 20px;
}

.address-inputs {
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.place-block {
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fafafa;
}

.place-block h4 {
  margin: 0 0 10px;
  font-size: 15px;
}

.result {
  margin-top: 20px;
  padding: 15px;
  background: #f0f8ff;
  border-radius: 8px;
}

.info-card {
  line-height: 2;
}

.realtime-location {
  margin-top: 20px;
  padding: 15px;
  background: #f0fff0;
  border-radius: 8px;
}

.tracking-controls {
  margin-top: 15px;
}
</style>
