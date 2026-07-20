<script setup>

  import {ref} from "vue"

  import {login} from "../api/user.js"

  const username = ref("")

  const password = ref("")

  const message = ref("")

  function handleLogin() {

    login({

      username: username.value,

      password: password.value,
    })
    .then(res => {

      if(res.code === 200 || res.data.success){
        message.value = "登录成功"
      } else {
        message.value = res.data.msg || "用户名或密码错误";
      }

      console.log(res.data)

      message.value="登录成功"

    })
        .catch(error=>{
          console.log(error)
          message.value =
              "登录失败"
        })


  }



</script>

<template>

  <div class="login">

    <h2>
      用户登录
    </h2>

    <input v-model="username" placeholder="用户名"/>

    <br>

    <input
      type="password"
      v-model="password"
      placeholder="密码"
    />

    <br>

    <button @click = "handleLogin">登录</button>

    <p>
      {{message}}
    </p>

  </div>

</template>