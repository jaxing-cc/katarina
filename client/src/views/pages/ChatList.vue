<template>
  <van-row>
    <user-card :user="loginUser"/>
    <search-user @click="selectUser"/>
    <van-row type="flex" class="title">
      <van-col span="1"/>
      聊天列表
    </van-row>




    <van-popup
        v-model="chat.openSwitch" closeable round
        :close-on-click-overlay="false"
        position="bottom" :style="{ height: '95%' }">
      <chat v-if="chat.openSwitch" :login-user="loginUser" :target-uid="chat.targetId"></chat>
    </van-popup>
  </van-row>

</template>

<script>

import Chat from "@/views/pages/other/Chat";
import SearchUser from "@/components/Search";
import {decodeToken} from "@/utils/token";
import {getByUid} from "@/api/auth";
import {Toast} from "vant";
import UserCard from "@/components/UserCard";

export default {
  name: 'ChatList',
  components: {UserCard, SearchUser, Chat},
  data() {
    return {
      chat: {
        openSwitch: false,
        targetId: '64b7af62f6c5071f233c6352'
      },
      loginUser:{}
    };
  },

  mounted() {

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
    this.$socket.connect()
    window.addEventListener("onmessage", this.messageListen)
  },

  destroyed() {
    window.removeEventListener("onmessage", this.messageListen)
  },

  methods: {
    selectUser(user){
      this.chat.targetId = user._id
      this.chat.openSwitch = true
    },
    messageListen(e) {
      e = e.detail
      if (e.type === 1001) {
        console.log(e)
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.title {
  font-size: 12px;
}
</style>