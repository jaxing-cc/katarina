<template>
  <div id="chatWrapper">
    <UserCard :user="loginUser" :show-status="true"></UserCard>
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
      loginUser: ""
    };
  },

  methods: {},

  mounted() {

  },
  created() {
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