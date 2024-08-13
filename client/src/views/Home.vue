<template>
  <div class="home">
    <van-tabbar v-model="active">
      <van-tabbar-item name="chat" icon="chat-o" :badge="newChatMessageCount" @click="chatClick" to="chat-list">
        聊天
      </van-tabbar-item>
      <van-tabbar-item name="post" icon="friends-o" to="/">动态</van-tabbar-item>
      <van-tabbar-item name="main" icon="home-o" to="main">我</van-tabbar-item>
    </van-tabbar>
    <router-view/>
  </div>
</template>

<script>
import store from '@/store/index'
import {followList, getByUid} from "@/api/auth";
import {Toast} from "vant";
import {decodeToken} from "@/utils/token";

export default {
  name: 'Home',
  data() {
    return {
      active: 'post',
      newChatMessageCount: null
    }
  },
  methods: {
    chatMsgHandler(e) {
      e = e.detail
      console.log(e)
      if (this.active !== 'chat') {
        this.newChatMessageCount = this.newChatMessageCount ? this.newChatMessageCount + 1 : 1
      }
    },
    chatClick() {
      this.newChatMessageCount = null
    }
  },

  mounted() {

  },

  created() {
    this.$socket.connect()
    if (this.$route.path.startsWith("/chat-list")) {
      this.active = "chat"
    } else if (this.$route.path.startsWith("/main")) {
      this.active = "main"
    }else{
      this.active = "post"
    }
    window.addEventListener("msg@1001", this.chatMsgHandler)
    followList().then(res => {
      if (res.success) {
        store.commit('setFollowList', res.data);
      }
    })
    getByUid(decodeToken().uid).then(res => {
      if (res.success) {
        if (!res.data) {
          Toast("用户不存在")
        } else {
          store.commit('setUserInfo', res.data);
        }
      }
    })
  },

  destroyed() {
    window.removeEventListener("msg@1001", this.chatMsgHandler)
  }
}
</script>
<style scoped>
.home {
  height: 100%;
}
</style>
