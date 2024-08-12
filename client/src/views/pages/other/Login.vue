<template>
  <div id="loginWrapper">
    <van-row class="placeholderRow"/>
        <van-form @submit="loginSubmit">
          <van-cell-group inset>
            <van-field name="account"
                       v-model="loginInfo.username" required
                       label="用户名/id"
                       placeholder="请输入用户名/id"
                       :rules="[{ validator: accountValidator, message: '该框不为空' }]"/>

            <van-field name="password"
                       v-model="loginInfo.password"
                       required type="password"
                       label="密码"
                       placeholder="请输入密码"
                       :rules="[{ validator: passwordValidator, message: '8-16字符' }]"/>
          </van-cell-group>
          <van-row class="placeholderRow"></van-row>
          <div style="margin: 8px;">
            <van-button round block native-type="submit">
              登录
            </van-button>
          </div>
        </van-form>
        <div style="margin: 8px;">
          <van-button round block to="/register">
            注册
          </van-button>
        </div>
  </div>
</template>

<script>

import {login, register} from '@/api/auth'
import {setToken} from '@/utils/token'

export default {
  name: 'ViewProtalLogin',

  data() {
    return {
      rules: {
        username: '',
        password: '',
      },
      page: '',
      loginInfo: {
        username: '',
        password: ''
      },
      register: {
        name: '',
        username: '',
        password: '',
        passwordACK: '',
        gender: 0
      }
    };
  },

  mounted() {

  },

  methods: {
    accountValidator(val) {
      return /\S+/.test(val);
    },
    passwordValidator: function (val) {
      return /\S{8,16}/.test(val);
    },
    usernameValidator: function (val) {
      return /\S{1,10}/.test(val);
    },
    loginSubmit() {
      login(this.loginInfo).then(res => {
        if (res.success && res.data) {
          if (res.data) {
            setToken(res.data)
          }
          this.$socket.connect()
          this.$router.push('/')
        }
      })
    },

  },
};
</script>

<style lang="scss" scoped>
#loginWrapper {
  background: rgb(248, 248, 248);
  height: 100%;
}

.placeholderRow {
  height: 20px;
}

.loginTitle {
  color: rgb(175, 184, 188);
  font-size: 25px;
  font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
}
</style>
