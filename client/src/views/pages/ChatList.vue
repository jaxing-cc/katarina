<template>
  <van-row class="chatListWrapper">
    <van-row class="chatListHeader" v-if="!searchStatus">
      <van-col :span="1"/>
      <user-card :user="loginUser"/>
    </van-row>
    <search-user @click="selectSearchedUser" @focus="searchStatus = true" @cancel="searchFocusCancel"/>

    <div class="chatListBody">
      <van-row type="flex" class="title" v-if="!searchStatus">
        <van-col span="1"/>
        聊天列表
      </van-row>
      <van-list
          class="custom_list"
          v-if="!searchStatus && offlineMsgInit"
          v-model="chatList.loading"
          @load="loadChatList"
          offset="300"
          :finished="chatList.finished"
          finished-text="没有更多了">
        <van-swipe-cell v-for="item in chatList.data" :key="item.info.chatTargetUid">
          <van-col :span="1"/>
          <user-card :user="item.user"
                     :show-text="item.lastMessage? item.lastMessage : new Date(item.info.createTime).toLocaleString()"
                     @click="startChat(item.info.chatTargetUid)"
                     :img-size="30"
                     :unread="chatList.unreadCount[item.info.chatTargetUid]"
                     class="resultItem">
          </user-card>
          <template #right>
            <van-button square type="danger" text="删除" @click="deleteChatListItem(item.info)"/>
          </template>
        </van-swipe-cell>

      </van-list>
    </div>

    <van-popup
        v-model="chat.switch" closeable round
        :close-on-click-overlay="false"
        @close="closeChat(chat.targetId)"
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
import {addChatListItem, clearOfflineMsg, deleteChatListItem, loadChatList, loadOfflineMsgCount} from "@/api/chat";

export default {
  name: 'ChatList',
  components: {UserCard, SearchUser, Chat},
  data() {
    return {
      chat: {
        switch: false,
        targetId: null,
      },
      loginUser: {},
      searchStatus: false,
      offlineMsgInit: false,
      chatList: {
        loading: false,
        finished: false,
        unreadCount: {},
        data: [],
        page: 1,
        size: 10,
      }
    };
  },

  mounted() {

  },

  created() {
    window.addEventListener("msg@1001", this.chatMsgHandler)
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
    this.loadOfflineMsgCountAndChatList()
  },

  destroyed() {
    window.removeEventListener("msg@1001", this.chatMsgHandler)
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
      this.chatList.loading = true;
      loadChatList(this.chatList.page, this.chatList.size).then(res => {
        if (res.success) {
          if (res.data && res.data.length > 0) {
            for (let i = 0; i < res.data.length; i++) {
              this.chatList.data.push(res.data[i])
            }
            this.chatList.page++;
          } else {
            this.chatList.finished = true
          }
        } else {
          Toast("网络异常，请重试")
        }
        that.chatList.loading = false;
      })
    },

    loadOfflineMsgCountAndChatList() {
      this.offlineMsgInit = false
      loadOfflineMsgCount().then(res => {
        if (res.success) {
          this.chatList.unreadCount = res.data
          this.offlineMsgInit = true
        } else {
          Toast("消息加载失败，请重试")
        }
      });
    },

    selectSearchedUser(uid) {
      this.addItemToChatList(uid,()=>{});
      this.startChat(uid)
    },

    addItemToChatList(uid, callback) {
      addChatListItem(uid).then(res => {
        if (res.success) {
          callback()
        } else {
          Toast("网络异常，请重试")
        }
      })
    },

    startChat(uid) {
      this.chat.targetId = uid
      this.chat.switch = true
      this.addItemToChatList(uid, () => {
        clearOfflineMsg(uid)
      })
    },

    closeChat(uid) {
      this.chat.targetId = null
      this.chat.switch = false
      this.updateOfflineMsgCount(uid, () => null)
    },
    updateOfflineMsgCount(uid, callback) {
      let obj = {}
      const keys = Object.keys(this.chatList.unreadCount);
      Object.values(this.chatList.unreadCount).forEach((item, index) => obj[keys[index]] = item);
      obj[uid] = callback(obj[uid])
      this.chatList.unreadCount = obj;
    },
    chatMsgHandler(e) {
      e = e.detail
      //不是正在聊天的用户则+1
      if (e.from !== this.chat.targetId && !this.chat.switch) {
        //未读计数+1
        this.updateOfflineMsgCount(e.from, num => num ? num + 1 : 1)
      }
      let data = this.chatList.data;
      let hasFromUser = false;
      for (let i = 0; i < data.length; i++) {
        if (data[i].info.chatTargetUid === e.from) {
          hasFromUser = true
          break;
        }
      }
      if (!hasFromUser) {
        getByUid(e.from).then(res => {
          if (res.success && res.data) {
            data.unshift({
              user: res.data,
              lastMessage: null,
              info: {
                chatTargetUid: e.from,
                uid: e.to,
                createTime: new Date().getTime()
              }
            })
          }
        })
      }
    },

    searchFocusCancel() {
      this.searchStatus = false
      this.reloadChatList()
    },
  },
}
;
</script>

<style lang="scss" scoped>
.title {
  font-size: 12px;
}

.chatListWrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chatListHeader {

}

.chatListBody {
  overflow: scroll;
  //height: 100%;
  flex: 1;
}

</style>