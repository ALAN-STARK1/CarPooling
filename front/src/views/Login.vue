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

  async function handleLogin() {
    try {
      const res = await login({
        username: username.value,
        password: password.value,
      })
      const result = res.data
      if (!result.success) {
        message.value = result.errorMsg || "用户名或密码错误"
        return
      }

      const data = result.data
      const token = typeof data === "string" ? data : data?.token
      const user = typeof data === "string" ? null : data?.user

      if (!token) {
        message.value = "登录成功但未返回 token"
        return
      }

      localStorage.setItem("user_token", token)
      if (user) {
        localStorage.setItem("user_info", JSON.stringify(user))
      }

      message.value = "登录成功"
      try {
        await router.push("/home")
      } catch (navError) {
        console.error("首页跳转失败：", navError)
        message.value =
          "登录成功，但首页加载失败。请按 Ctrl+F5 强制刷新后再试。" +
          (navError?.message ? `（${navError.message}）` : "")
      }
    } catch (error) {
      console.log("登录异常：", error)
      message.value = error?.message
        ? `登录失败：${error.message}`
        : "系统繁忙，登录失败"
    }
  }</script>

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