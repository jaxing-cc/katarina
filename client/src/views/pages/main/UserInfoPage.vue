<template>
  <div class="UserInfoPageWrapper">
    <van-nav-bar title="个人信息"  left-text="返回" @click-left="onClickLeft"></van-nav-bar>
    <van-cell-group >
      <van-cell value="修改" is-link>
        <template #title>
          <div v-if="!loginUser.avater">头像</div>
          <van-image error-icon="smile-o" v-if="loginUser.avater" class="userImg" width="40"
                     round position="left" src="avatar-1.jpg"/>
        </template>
      </van-cell>
      <van-cell title="用户名" :label="loginUser.name" is-link/>
      <van-cell title="性别" :label="loginUser.gender === 1 ? '男' : '女'" is-link/>
      <van-cell title="邮箱" :label="loginUser.email  ? loginUser.email  : '暂无'" is-link/>
      <van-cell title="修改密码"  is-link/>
    </van-cell-group>
  </div>
</template>

<script>
import {decodeToken} from "@/utils/token";
import {getByUid} from "@/api/auth";
import {Toast} from "vant";
export default {
  name: "UserInfoPage",
  components: {},
  data(){
    return {
      loginUser:{},
      updateUserInfo:{
        name:null,

      }
    }
  },
  methods:{
    onClickLeft(){
      this.$router.push("/")
    },
    updateInfo(){

    }
  },
  created() {
    const jwtObj = decodeToken();
    getByUid(jwtObj.uid).then(res => {
      if (res.success) {
        if (!res.data) {
          Toast("用户不存在")
        } else {
          this.loginUser = res.data;
        }
      }
    })
  }
}
</script>

<style scoped>
.UserInfoPageWrapper{
  text-align: left;
}
</style>