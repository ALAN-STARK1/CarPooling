<script setup>

import { ref } from "vue"

import Driver from "../components/Driver.vue"
import Passenger from "../components/Passenger.vue"
const currentPage = ref("passenger")

</script>

<template>

  <div class="menu">

    <button @click="currentPage='passenger'">
      乘客
    </button>

    <button @click="currentPage='driver'">
      司机
    </button>

  </div>

  <div class="content">

    <Passenger v-if="currentPage === 'passenger'" />

    <Driver v-if="currentPage === 'driver'" />

  </div>



</template>

<style scoped>

</style>