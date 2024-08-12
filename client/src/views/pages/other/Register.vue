<template>
  <div>
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
    <van-button round block to="login">
      返回
    </van-button>
  </div>

</template>

<script>
import {Toast} from "vant";
import {register} from "@/api/auth";

export default {
  name: "Register",
  data() {
    return {
      rules: {
        username: '',
        password: '',
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
  methods: {
    passwordValidator: function (val) {
      return /\S{8,16}/.test(val);
    },
    usernameValidator: function (val) {
      return /\S{1,10}/.test(val);
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

  }
}
</script>

<style scoped>

</style>