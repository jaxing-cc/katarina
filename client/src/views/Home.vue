<template>
  <div class="home">
    <van-tabbar v-model="active">
      <van-tabbar-item name="chat" icon="chat-o">聊天</van-tabbar-item>
      <van-tabbar-item name="friends" icon="friends-o">发现</van-tabbar-item>
      <van-tabbar-item name="main" icon="home-o">我</van-tabbar-item>
    </van-tabbar>
    <van-button @click="close">asdad</van-button>

    <chat v-if="active === 'chat'"></chat>
    <friends v-if="active === 'friends'"></friends>
    <main-page v-if="active === 'main'"></main-page>
  </div>
</template>

<script>
import Chat from './pages/ChatList.vue'
import Friends from './pages/Discover.vue'
import MainPage from './pages/Main.vue'

export default {
  components: {Chat, Friends, MainPage},
  name: 'Home',
  data() {
    return {
      active: 'chat',
      webSocket: {}
    }
  },
  methods: {
    eventListen(evt) {
      console.log(evt.detail)
    },
    close(){
      window.removeEventListener('onmessage', this.eventListen)
    }
  },

  mounted() {
    window.addEventListener("onmessage", this.eventListen)
  },
  created() {
    this.$socket.connect()
  },
  destroyed() {
    window.removeEventListener("onmessage", this.eventListen)
  }
}
</script>
<style scoped>
.home {
  height: 100%;
}
</style>
