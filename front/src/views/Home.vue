<script setup>
import { ref, defineAsyncComponent } from 'vue'

// 异步加载，避免某个子组件旧缓存拖垮整页跳转
const Passenger = defineAsyncComponent(() => import('../components/Passenger.vue'))
const Driver = defineAsyncComponent(() => import('../components/Driver.vue'))

const currentPage = ref('passenger')
</script>

<template>
  <div class="menu">
    <button type="button" @click="currentPage = 'passenger'">乘客</button>
    <button type="button" @click="currentPage = 'driver'">司机</button>
  </div>

  <div class="content">
    <Passenger v-if="currentPage === 'passenger'" />
    <Driver v-if="currentPage === 'driver'" />
  </div>
</template>

<style scoped>
.menu {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
</style>
