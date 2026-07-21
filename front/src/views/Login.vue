<script setup>

  import {ref} from "vue"

  import {login} from "../api/user.js"

  import {useRouter} from "vue-router"

  const username = ref("")

  const password = ref("")

  const message = ref("")

  const router = useRouter()

  function handleLogin() {

    login({

      username: username.value,

      password: password.value,
    })
    .then(res => {

      if(res.data === 1){
        message.value = "登录成功"
        const token  = res.data.token;

        localStorage.setItem("user_token", token);
        alert('登录成功')

        router.push("/home")
      } else {
        message.value = res.data.msg || "用户名或密码错误";
      }

      console.log(res.data)


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