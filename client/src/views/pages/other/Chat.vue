<template>
  <div id="chatWrapper">
    <div v-if="!this.group">
      <user-card class="chatHeader" :user="targetUser" :show-username="true" :follow="2"></user-card>
      <div class="chatBody">
        <chat-context :data="messageRecordInfo.data" :targetUser="targetUser" :loginUser="loginUser"
                      @onLoad="loadMore"></chat-context>
      </div>
    </div>
    <div v-if="this.group">
      <user-card class="chatHeader" :user="{name: groupInfo.name, online: true, avatar:groupInfo.avatar}" ></user-card>
      <div class="chatBody">
        <group-chat-context :data="messageRecordInfo.data" @onLoad="loadMore"></group-chat-context>
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
        end: false,
        page: 1,
        size: 5
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
    loadHistoryMessage(moveToBottom) {
      if (this.messageRecordInfo.end) {
        Toast('别翻了,到顶了')
        return;
      }
      loadMessageRecord(this.targetId, this.group, this.messageRecordInfo.page, this.messageRecordInfo.size).then(res => {
        if (res.success) {
          let data = res.data;
          if (data.length === 0) {
            this.messageRecordInfo.end = true;
            Toast('别翻了,到顶了');
          } else {
            this.messageRecordInfo.data = res.data.concat(this.messageRecordInfo.data);
            if (moveToBottom) {
              this.$nextTick(() => {
                this.moveToBottom();
              });
            }
          }
        }
      })
    },
    chatMsgHandler(e) {
      if (!this.group) {
        if (this.targetId === e.detail.from) {
          this.messageRecordInfo.data.push(e.detail)
          this.$nextTick(() => {
            this.moveToBottom();
          });
        }
      } else {
        this.messageRecordInfo.data.push(e.detail)
        this.$nextTick(() => {
          this.moveToBottom();
        });
      }
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
      this.emoji.showEmoji = false
      this.emoji.icon = "smile-o"
    },
    exit() {
      this.$emit("exit")
    },
    loadMore(end) {
      this.messageRecordInfo.page++
      this.loadHistoryMessage();
      end()
    }
  },

  props: {
    targetId: "",
    loginUser: {},
    group: {
      type: Boolean,
      default: false,
    },
    groupInfo: {}
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
            that.loadHistoryMessage(true)
          }
        }
      })
    } else {
      that.loadHistoryMessage(true);
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