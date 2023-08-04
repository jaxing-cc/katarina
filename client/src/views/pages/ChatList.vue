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


    <van-list :v-model="chatList.loading"
              @load="loadChatList"
              :finished="chatList.finished"
              finished-text="没有更多了">
      <van-swipe-cell v-for="item in chatList.data" :key="item.info.chatTargetUid">
        <van-row>
          <van-col :span="1"/>
          <user-card :user="item.user"
                     :show-text="item.lastMessage? item.lastMessage : new Date(item.info.createTime).toLocaleString()"
                     @click="startChat(item.info.chatTargetUid)"
                     :img-size="30"
                     class="resultItem">
          </user-card>
        </van-row>
        <template #right>
          <van-button square type="danger" text="删除" @click="deleteChatListItem(item.info)"/>
        </template>
      </van-swipe-cell>
    </van-list>


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
        loading: false,
        finished: false,
        data: [],
        page: 1,
        size: 10,
      }
    };
  },

  mounted() {

  },

  created() {
    window.addEventListener("msg@1001", this.msgHandler)
    // this.reloadChatList()
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
    window.removeEventListener("msg@1001", this.msgHandler)
  },

  methods: {
    deleteChatListItem(data) {
      let that = this;
      deleteChatListItem(data.chatTargetUid).then(res => {
        if (res.success) {
          that.reloadChatList()
        } else {
          Toast("删除失败，请重试")
        }
      })
    },
    reloadChatList() {
      this.chatList.page = 1
      this.chatList.loading = false
      this.chatList.finished = false
      this.chatList.data = []
      this.loadChatList();
    },
    loadChatList() {
      let that = this;
      this.chatList.loading = this;
      console.log(this.chatList.page, this.chatList.finished,'loadstart')
      loadChatList(this.chatList.page, this.chatList.size).then(res => {
        if (res.success) {
          if (res.data && res.data.length > 0) {
            this.chatList.data = res.data
            this.chatList.page++;
          } else {
            this.chatList.finished = true
            Toast("已经到底了...")
          }
        } else {
          Toast("网络异常，请重试")
        }
        console.log(this.chatList.page, this.chatList.finished,'loadend')
        that.chatList.loading = false;
      })
    }
    ,
    selectSearchedUser(uid) {
      let that = this;
      addChatListItem(uid).then(res => {
        if (res.success) {
          that.reloadChatList()
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
    msgHandler(e) {
      e = e.detail
      let list = this.chatList.data;
      for (let i = 0; i < list.length; i++) {
        console.log(list[i])
      }
    },
  },
}
;
</script>

<style lang="scss" scoped>
.title {
  font-size: 12px;
}
</style>