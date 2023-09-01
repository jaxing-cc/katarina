<template>
  <div class="UserInfoPageWrapper">
    <van-nav-bar title="个人信息" left-text="返回" @click-left="onClickLeft"></van-nav-bar>
    <van-cell-group>
      <van-cell>
        <template #title>
          <div v-if="!loginUser.avatar">暂无头像</div>
          <van-uploader v-model="avatar" :after-read="afterRead"/>
        </template>
      </van-cell>
      <van-cell title="用户名" :label="loginUser.name" is-link/>
      <van-cell title="性别" :label="loginUser.gender === 1 ? '男' : '女'" is-link/>
      <van-cell title="邮箱" :label="loginUser.email  ? loginUser.email  : '暂无'" is-link/>
      <van-cell title="修改密码" is-link/>
    </van-cell-group>
  </div>
</template>

<script>
import {decodeToken} from "@/utils/token";
import {getByUid} from "@/api/auth";
import {Toast} from "vant";
import URL from '@/utils/common'

export default {
  name: "UserInfoPage",
  components: {},
  data() {
    return {
      avatar: [],
      loginUser: {},
      updateUserInfo: {
        name: null,

      }
    }
  },
  methods: {
    onClickLeft() {
      this.$router.push("/")
    },
    updateInfo() {

    },
    afterRead() {

    },
    loadUser() {
      const jwtObj = decodeToken();
      getByUid(jwtObj.uid).then(res => {
        if (res.success) {
          if (!res.data) {
            Toast("用户不存在")
          } else {
            this.loginUser = res.data;
            if (this.loginUser.avatar) {
              let url = URL.baseURL + "/file/" + this.loginUser.avatar
              console.log(url)
              this.avatar[0] = {url: url}
            }
          }
        }
      })
    }
  },
  created() {
    this.loadUser()
  }
}
</script>

<style scoped>
.UserInfoPageWrapper {
  text-align: left;
}
</style>