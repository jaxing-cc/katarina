<template>
  <div id="chatWrapper">
    <UserCard :user="targetUser" :show-username="true"></UserCard>

    <van-row class="chatInput">
      <van-field
          v-model="inputMessage"
          rows="1"
          :autosize="{ maxHeight: 100, minHeight: 50 }"
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
import {decodeToken} from "@/utils/token";
import UserCard from "@/components/UserCard";
import {sendMessage} from "@/api/chat";

export default {
  name: 'Chat',
  components: {UserCard},
  data() {
    return {
      targetUser: {},
      inputMessage: "",
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

.chatInput {
  width: 100%;
  position: absolute;
  bottom: 10px;
}
</style>