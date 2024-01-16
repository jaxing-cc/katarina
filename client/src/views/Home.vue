<template>
  <div class="home">
    <van-tabbar v-model="active">
      <van-tabbar-item name="chat" icon="chat-o" :badge="newChatMessageCount" @click="chatClick">
        聊天
      </van-tabbar-item>
      <van-tabbar-item name="friends" icon="friends-o">发现</van-tabbar-item>
      <van-tabbar-item name="main" icon="home-o">我</van-tabbar-item>
    </van-tabbar>

    <chat v-if="active === 'chat'"></chat>
    <friends v-if="active === 'friends'"></friends>
    <main-page v-if="active === 'main'"></main-page>
  </div>
</template>

<script>
import Chat from './pages/ChatList.vue'
import Friends from './pages/Discover.vue'
import MainPage from './pages/Main.vue'
import store from '@/store/index'
import {followList, getByUid} from "@/api/auth";
import {Toast} from "vant";
import {decodeToken} from "@/utils/token";
export default {
  components: {Chat, Friends, MainPage},
  name: 'Home',
  data() {
    return {
      active: 'chat',
      newChatMessageCount: null
    }
  },
  methods: {
    chatMsgHandler(e) {
      e = e.detail
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
          store.commit('setUserInfo',res.data);
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
