<script setup>

  import {ref} from "vue"

  import {login, register} from "../api/user.js"

  import {useRouter} from "vue-router"


  const username = ref("")

  const password = ref("")

  const message = ref("")

  const router = useRouter()

  function handleRegister() {

    register({
      username: username.value,
      password: password.value,
    })
        .then(res => {
          if (res.data.success) {
            message.value = "注册成功"
          } else {
            message.value = res.data.errorMsg || "注册失败";
          }
        })
        .catch(error => {
          console.log("注册异常：",error);
          message.value = "系统繁忙，注册失败"
        })

  }

  function handleLogin() {

    login({
      username: username.value,
      password: password.value,
    })
    .then(res => {
      const result = res.data
      if (result.success) {
        message.value = "登录成功"
        const data = result.data

        localStorage.setItem("user_token", data.token)
        localStorage.setItem("user_info", JSON.stringify(data.user))
        alert('登录成功')

        router.push("/home")
      } else {
        message.value = result.errorMsg || "用户名或密码错误";
      }
    })
    .catch(error => {
      console.log("登录异常：",error);
      message.value = "系统繁忙，登录失败"
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

    <button @click = "handleRegister">注册</button>

    <button @click = "handleLogin">登录</button>

    <p>
      {{message}}
    </p>

  </div>

</template>