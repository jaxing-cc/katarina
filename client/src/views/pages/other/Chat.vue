<template>
  <div id="chatWrapper">
    <UserCard :user="targetUser" :show-status="true"></UserCard>

    <van-row class="chatInput">
      <van-field
          v-model="inputMessage"
          rows="1"
          :autosize="{ maxHeight: 100, minHeight: 50 }"
          type="textarea"
          maxlength="350"
          clickable="true"
          show-word-limit>
        <template #button>
          <van-button size="small" color="#814f56" icon="guide-o"></van-button>
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

export default {
  name: 'Chat',
  components: {UserCard},
  data() {
    return {
      targetUser: "",
      loginUser: "",
      inputMessage: "",
    };
  },

  methods: {},

  mounted() {

  },
  created() {
    this.$socket.connect()
    const jwtObj = decodeToken();
    getByUid(this.$route.params.uid).then(res => {
      if (res.success) {
        if (!res.data) {
          Toast("用户不存在")
        } else {
          this.targetUser = res.data;
        }
      }
    })
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