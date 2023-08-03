<template>
  <div id="chatWrapper">
    <user-card class="chatHeader" :user="targetUser" :show-username="true"></user-card>
    <van-row class="chatBody">
      <chat-context :data="mock" :targetUser="targetUser" :loginUser="loginUser"></chat-context>
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
import {sendMessage} from "@/api/chat";
import ChatContext from "@/components/ChatContext";

export default {
  name: 'Chat',
  components: {ChatContext, UserCard},
  data() {
    return {
      targetUser: {},
      inputMessage: "",
      mock: [
        {
          messageId: "64cb1846d35ecf725e05a65f",
          from: "64882d8cb11b5f4c1827460b",
          to: "64b7af62f6c5071f233c6352",
          groupMessage: false,
          content: "hi",
          contentType: 0,
          offlineMessage: false,
          createTime: 1691031622553
        },
        {
          messageId: "64cb1846d35ecf725e05a66f",
          from: "64b7af62f6c5071f233c6352",
          to: "64882d8cb11b5f4c1827460b",
          groupMessage: false,
          content: "hi",
          contentType: 0,
          offlineMessage: false,
          createTime: 1691031622153
        },
      ]
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
      })
    }
  },

  props: ["targetUid", "loginUser"],

  updated() {
  },
  mounted() {
  },

  created() {
    getByUid(this.targetUid).then(res => {
      if (res.success) {
        if (!res.data) {
          Toast("用户不存在")
        } else {
          this.targetUser = res.data;
        }
      }
    })
  },

  destroyed() {
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