<script>
import SearchPath from "@/views/SearchPath.vue";

const path = ref(null)

export default {
  name: "Driver",
  components: {SearchPath}
}
</script>

<template>
  <h2>请选择起始点</h2>
  <SearchPath ref="path"/>

</template>

<style scoped>

</style>