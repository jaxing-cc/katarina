<template>
  <van-row>
    <van-row>
      <van-col :span="1"/>
      <user-card :user="loginUser"/>
    </van-row>
    <search-user @click="selectSearchedUser"/>
    <van-row type="flex" class="title">
      <van-col span="1"/>
      聊天列表
    </van-row>

    <van-swipe-cell v-for="item in chatList.data" :key="item.item.chatTargetUid">
      <van-row>
        <van-col :span="1"/>
        <user-card :user="item"
                   :show-text="item.lastMessage? item.lastMessage : new Date(item.item.createTime).toLocaleString()"
                   @click="startChat(item.item.chatTargetUid)"
                   :img-size="30"
                   class="resultItem">
        </user-card>
      </van-row>
      <template #right>
        <van-button square type="danger" text="删除" @click="deleteChatListItem(item.item)"/>
      </template>
    </van-swipe-cell>

    <van-popup
        v-model="chat.switch" closeable round
        :close-on-click-overlay="false"
        position="bottom" :style="{ height: '95%' }">
      <chat v-if="chat.switch" :login-user="loginUser" :target-uid="chat.targetId"></chat>
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
import {addChatListItem, deleteChatListItem, loadChatList} from "@/api/chat";

export default {
  name: 'ChatList',
  components: {UserCard, SearchUser, Chat},
  data() {
    return {
      chat: {
        switch: false,
        targetId: '64b7af62f6c5071f233c6352'
      },
      loginUser: {},
      chatList: {
        data: [],
        page: 1
      }
    };
  },

  mounted() {

  },

  created() {
    window.addEventListener("msg@1001", this.messageListen)
    this.loadChatList();
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
  },

  destroyed() {
    window.removeEventListener("msg@1001", this.messageListen)
  },

  methods: {
    deleteChatListItem(data) {
      let that = this;
      deleteChatListItem(data.chatTargetUid).then(res => {
        if (res.success) {
          that.loadChatList();
        } else {
          Toast("删除失败，请重试")
        }
      })
    },
    loadChatList() {
      loadChatList(this.chatList.page).then(res => {
        if (res.success) {
          this.chatList.data = res.data
        } else {
          Toast("网络异常，请重试")
        }
      })
    }
    ,
    selectSearchedUser(uid) {
      let that = this;
      addChatListItem(uid).then(res => {
        if (res.success) {
          that.loadChatList()
          that.startChat(uid)
        } else {
          Toast("网络异常，请重试")
        }
      })
    },
    startChat(uid) {
      this.chat.targetId = uid
      this.chat.switch = true
    },
    messageListen(e) {
      e = e.detail
    }
    ,
  },
}
;
</script>

<style lang="scss" scoped>
.title {
  font-size: 12px;
}
</style>