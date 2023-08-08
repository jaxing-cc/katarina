<template>
  <div id="loginWrapper">
    <van-tabs :active="page">
      <van-tab title="登录">
        <van-row class="placeholderRow"></van-row>
        <van-row>
          <van-col span="8">
            <span class="loginTitle">CHAT</span>
          </van-col>
        </van-row>
        <van-row class="placeholderRow"></van-row>
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
          <div style="margin: 16px;">
            <van-button round block native-type="submit">
              登录
            </van-button>
          </div>
        </van-form>


        <van-row class="placeholderRow"></van-row>

      </van-tab>
      <van-tab title="注册">
        <van-form @submit="registerSubmit">
          <van-cell-group inset>
            <van-field name="name" v-model="register.name" required label="昵称" placeholder="请输入昵称"
                       :rules="[{ validator: usernameValidator, message: '用户名1-10字符' }]"/>
            <van-field name="username" v-model="register.username" required label="账号" placeholder="请输入账号"
                       :rules="[{ validator: usernameValidator, message: '用户名1-10字符' }]"/>
            <van-field name="password" v-model="register.password" required type="password" label="密码" placeholder="密码"
                       :rules="[{ validator: passwordValidator, message: '密码8-16字符' }]"/>
            <van-field name="passwordACK" v-model="register.passwordACK" required type="password" label="确认密码"
                       placeholder="确认密码" :rules="[{ validator: passwordValidator, message: '密码8-16字符' }]"/>
            <van-field name="gender" label="单选框">
              <template #input>
                <van-radio-group v-model="register.gender" direction="horizontal">
                  <van-radio :name="1">男</van-radio>
                  <van-radio :name="2">女</van-radio>
                  <van-radio :name="0">保密</van-radio>
                </van-radio-group>
              </template>
            </van-field>
          </van-cell-group>
          <div style="margin: 16px;">
            <van-button round block native-type="submit">
              注册
            </van-button>
          </div>
        </van-form>

      </van-tab>
    </van-tabs>

  </div>
</template>

<script>

import {Toast} from 'vant'
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
    registerSubmit() {
      if (this.register.password !== this.register.passwordACK) {
        Toast('两次密码不一致')
        return;
      }
      register({
        name: this.register.name,
        username: this.register.username,
        password: this.register.password,
        gender: this.register.gender
      }).then(res => {
        if (res.success) {
          Toast.success('注册成功,请前往登录')
        }
      }).catch(() => {
        Toast.clear();
      })
    }
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
