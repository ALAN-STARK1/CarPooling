<script setup>
import {ref} from "vue"

import {login} from "../api/user.js"

import {useRouter} from "vue-router"

const boardingPoint = ref("")

const dropOffPoint = ref("")

const startTime = ref("")

const endTime = ref("")

const router = useRouter()

const timeRange = ref([])

function handleSubmitOrder(){
  submitOrder({
    boardingPoint: boardingPoint.value,
    dropOffPoint: dropOffPoint.value,
    timeRange: timeRange.value,
  })
  if(res.data.success){
    console.log("提交成功");
  }
  else{
    console.log("系统异常:" + error);
  }


}

</script>

<template>
  <input v-model="boardingPoint" placeholder="请输入上车点"/>
  <input v-model="dropOffPoint" placeholder="请输入终点"/>
  <button @click = "handleSubmitOrder">提交信息</button>

  <div class="timeRange">
    <el-date-picker
      v-model="timeRange"
      type="datetimerange"
      start-placeholder="开始时间"
      end-placeholder="结束时间"
      format="yyyy-MM-dd HH:mm:ss"
      value-format="yyyy-MM-dd HH:mm:ss"
    />
  </div>


</template>

<style scoped>

</style>