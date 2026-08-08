<script setup>
import { ref } from 'vue'
import SearchPath from '@/views/SearchPath.vue'
import { submitOrder } from '@/api/route'

/** 子组件 SearchPath 的引用 */
const pathRef = ref(null)
const submitting = ref(false)

/**
 * 从子组件取出已选起终点，提交订单
 */
async function handleSubmitOrder() {
  const child = pathRef.value
  if (!child) {
    alert('地图组件未就绪')
    return
  }

  // defineExpose 暴露的 ref，通过父组件 ref 访问时会自动解包
  const boarding = child.boardingExact
  const dropOff = child.dropOffExact

  if (!boarding || !dropOff) {
    alert('请先选择起点和终点')
    return
  }

  submitting.value = true
  try {
    const res = await submitOrder({

      submitType: '司机',

      boardingExact: {
        name: boarding.name,
        city: boarding.city,
        lng: boarding.lng,
        lat: boarding.lat,
      },
      dropOffExact: {
        name: dropOff.name,
        city: dropOff.city,
        lng: dropOff.lng,
        lat: dropOff.lat,
      },
    })

    if (res.data?.success) {
      alert(res.data.data || '订单提交成功')
    } else {
      alert(res.data?.errorMsg || '订单提交失败')
    }
  } catch (e) {
    alert(e.message || '网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }

}

</script>


<template>
  <div class="driver-page">
    <h2>请选择起始点</h2>
    <SearchPath ref="pathRef" />
    <button
        type="button"
        class="submit-btn"
        :disabled="submitting"
        @click="handleSubmitOrder"
    >
      {{ submitting ? '提交中...' : '提交订单' }}
    </button>
  </div>
</template>

<style scoped>
.driver-page {
  padding-bottom: 24px;
}
</style>
