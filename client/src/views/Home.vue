<template>
  <div class="home">
    <van-tabbar v-model="active">
      <van-tabbar-item name="chat" icon="chat-o">聊天</van-tabbar-item>
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
import Friends from './pages/Friends.vue'
import MainPage from './pages/Main.vue'
import { sendWSPush } from '@/utils/ws'
import { decodeToken,getToken,removeToken } from '@/utils/token'
import { Toast } from 'vant'
export default {
  components: { Chat, Friends, MainPage },
  name: 'Home',
  data(){
    return{
      active:'chat',
      webSocket:{}
    }
  },
  methods:{
  },
  mounted(){
  },
  created() {
  },
  destroyed() {
  }
}
</script>
<style scoped>
.home{
  height: 100%;
}
</style>
