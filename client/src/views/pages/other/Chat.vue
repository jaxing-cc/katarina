<template>
  <div id="chatWrapper">
    <user-card class="chatHeader" :user="targetUser" :show-username="true"></user-card>
    <div class="chatBody" id="body">
      <chat-context :data="messageRecordInfo.data" :targetUser="targetUser" :loginUser="loginUser"></chat-context>
    </div>
    <van-row class="chatInput" @click.stop>
      <VEmojiV2 v-if="emoji.showEmoji" :showCategories="false" :showSearch="false" :continuousList="true" @select="selectEmoji"/>
      <van-row>
        <van-col :span="4" class="chatInputIcon">
          <van-icon size="20" :name="emoji.icon" @click="clickEmojiSwitch"></van-icon>
        </van-col>
      </van-row>
      <van-field
          v-model="inputMessage"
          :autosize="{ maxHeight: 50, minHeight: 50 }"
          type="textarea"
          placeholder="200字以内"
          maxlength="200">
        <template #button>
          <van-button size="small" color="#3E7FCC" @click="send(0)" icon="success"></van-button>
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
      active: '',
      emoji: {
        showEmoji: false,
        icon: "smile-o"
      },
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
          this.closeEmojiSwitch()
          this.inputMessage = null
          this.$nextTick(() => {
            this.moveToBottom();
          });
        } else {
          Toast("网络异常，请重试")
        }
      })
    },
    moveToBottom() {
      let body = document.getElementById("body")
      body.scrollTo(0, body.scrollHeight)
    },
    loadHistoryMessage() {
      loadMessageRecord(this.targetUser._id, this.messageRecordInfo.page, this.messageRecordInfo.size).then(res => {
        if (res.success) {
          if (res.data) {
            this.messageRecordInfo.data = res.data;
            this.$nextTick(() => {
              this.moveToBottom();
            });
          } else {
            Toast("网络异常，请重试")
          }
        }
      })
    },
    chatMsgHandler(e) {
      this.messageRecordInfo.data.push(e.detail)
    },
    clickEmojiSwitch() {
      this.emoji.showEmoji = !this.emoji.showEmoji
      if (this.emoji.showEmoji) {
        this.emoji.icon = "smile"
      } else {
        this.emoji.icon = "smile-o"
      }
    },
    closeEmojiSwitch() {
      this.emoji.showEmoji = false
      this.emoji.icon = "smile-o"
    },
    selectEmoji(e) {
      this.inputMessage += e.data
    }
  },

  props: ["targetUid", "loginUser"],

  updated() {
  },
  mounted() {
    document.addEventListener("click", this.closeEmojiSwitch);
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
    document.removeEventListener("click", this.closeEmojiSwitch);
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
  overflow-y: scroll;
  width: 100%;
  top: 55px;
  position: absolute;
  bottom: 100px;
}

.chatInput {
  width: 100%;
  position: absolute;
  bottom: 0;
}

.chatInputIcon {
  background-color: white;
  border-radius: 5px;
  margin-top: 5px;
}
</style>