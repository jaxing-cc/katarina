<template>
  <div id="chatWrapper">
    <div v-if="!this.group">
      <user-card class="chatHeader" :user="targetUser" :show-username="true" :follow="2"></user-card>
      <div class="chatBody">
        <chat-context :data="messageRecordInfo.data" :targetUser="targetUser" :loginUser="loginUser"></chat-context>
      </div>
    </div>
    <div v-if="this.group">
      <div class="chatBody">
        <group-chat-context :data="messageRecordInfo.data"></group-chat-context>
      </div>
    </div>
    <van-row class="chatInput" @click.stop>
      <VEmojiV2 v-if="emoji.showEmoji" :showCategories="false" :showSearch="false" :continuousList="true"
                @select="selectEmoji"/>
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
      <van-grid :column-num="4" direction="horizontal" clickable :icon-size="13">
        <van-grid-item icon="revoke" @click="exit"/>
        <van-grid-item :icon="emoji.icon" @click="clickEmojiSwitch"/>
        <van-grid-item icon="photo-o"/>
        <van-grid-item icon="ellipsis"/>
      </van-grid>
    </van-row>
  </div>
</template>


<script>
import {getByUid} from "@/api/auth";
import {Toast} from "vant";
import UserCard from "@/components/UserCard";
import {loadMessageRecord, sendMessage} from "@/api/chat";
import ChatContext from "@/components/ChatContext";
import GroupChatContext from "@/components/GroupChatContext";


export default {
  name: 'Chat',
  components: {GroupChatContext, ChatContext, UserCard},
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
      if (!this.inputMessage) {
        return;
      }
      sendMessage({
        groupMessage: this.group,
        content: this.inputMessage,
        contentType: type,
        to: this.targetId,
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
      let body = document.getElementsByClassName("chatBody")
      body[0].scrollTo(0, body[0].scrollHeight)
    },
    loadHistoryMessage() {
      loadMessageRecord(this.targetId, this.group, this.messageRecordInfo.page, this.messageRecordInfo.size).then(res => {
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
    },
    exit() {
      this.$emit("exit")
    }
  },

  props: {
    targetId: "",
    loginUser: {},
    group: {
      type: Boolean,
      default: false
    }
  },

  updated() {
  },
  mounted() {
    document.addEventListener("click", this.closeEmojiSwitch);
  },

  created() {
    let that = this;
    window.addEventListener("msg@1001", this.chatMsgHandler)
    if (!this.group) {
      getByUid(this.targetId).then(res => {
        if (res.success) {
          if (!res.data) {
            Toast("用户不存在")
          } else {
            this.targetUser = res.data;
            that.loadHistoryMessage()
          }
        }
      })
    }else{
      that.loadHistoryMessage();
    }
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
  bottom: 120px;
}

.chatInput {
  width: 100%;
  position: absolute;
  bottom: 0;
}

.chatInputIcon {
  background-color: white;
  margin-top: 5px;
}
</style>