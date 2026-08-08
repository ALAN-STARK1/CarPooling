<script>
export default {
  name: "DriverJourney"
}
</script>

<div class="order-item">
  <div></div>
</div>


<template>
  <journeyItem
    v-for="item in journey"
    :key="item.journeyNo"
    :order="item"
    @click="goDetail(item.journeyNo)"
  />
</template>

<style scoped>

</style>