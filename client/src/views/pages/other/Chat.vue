<template>
  <div id="chatWrapper">
    <user-card class="chatHeader" :user="targetUser" :show-username="true"></user-card>
    <van-row class="chatBody">
      <chat-context :data="messageRecordInfo.data" :targetUser="targetUser" :loginUser="loginUser"></chat-context>
    </van-row>
    <van-row class="chatInput">
      <van-field
          v-model="inputMessage"
          rows="1"
          :autosize="{ maxHeight: 50, minHeight: 50 }"
          type="textarea"
          maxlength="200"
          show-word-limit>
        <template #button>
          <van-button size="small" color="#814f56" icon="guide-o" @click="send(0)"></van-button>
        </template>
      </van-field>
    </van-row>
  </div>
</template>


<script>
import {getByUid} from "@/api/auth";
import {Toast} from "vant";
import UserCard from "@/components/UserCard";
import {loadMessageRecord, sendMessage} from "@/api/chat";
import ChatContext from "@/components/ChatContext";

export default {
  name: 'Chat',
  components: {ChatContext, UserCard},
  data() {
    return {
      targetUser: {},
      inputMessage: "",
      messageRecordInfo: {
        data: [],
        page: 1,
        size: 10
      },
    };
  },

  methods: {
    send(type) {
      sendMessage({
        groupMessage: false,
        content: this.inputMessage,
        contentType: type,
        to: this.targetUser._id,
        createTime: new Date().getTime()
      }).then(res => {
        if (res.success) {
          this.messageRecordInfo.data.push(res.data)
        } else {
          Toast("网络异常，请重试")
        }
      })
    },
    loadHistoryMessage() {
      loadMessageRecord(this.targetUser._id, this.messageRecordInfo.page, this.messageRecordInfo.size).then(res => {
        if (res.success) {
          if (res.data) {
            this.messageRecordInfo.data = res.data;
          } else {
            Toast("网络异常，请重试")
          }
        }
      })
    },
    chatMsgHandler(e) {
      this.messageRecordInfo.data.push(e.detail)
    }
  },

  props: ["targetUid", "loginUser"],

  updated() {
  },
  mounted() {
  },

  created() {
    let that = this;
    window.addEventListener("msg@1001", this.chatMsgHandler)
    getByUid(this.targetUid).then(res => {
      if (res.success) {
        if (!res.data) {
          Toast("用户不存在")
        } else {
          this.targetUser = res.data;
          that.loadHistoryMessage()
        }
      }
    })
  },

  destroyed() {
    window.removeEventListener("msg@1001", this.chatMsgHandler)
  },
};
</script>

<style lang="scss" scoped>
#chatWrapper {
  background: rgb(248, 248, 248);
  height: 100%;
}

.chatHeader {
  width: 100%;
  position: absolute;
  top: 0;
}

.chatBody {
  overflow: scroll;
  width: 100%;
  top: 55px;
  position: absolute;
  bottom: 90px;
}

.chatInput {
  width: 100%;
  position: absolute;
  bottom: 0;
}
</style>