<template>
  <van-row>
    <van-row type="flex" v-if="data" v-for="(d,index) in data" :key="d.messageId" class="messageRow">
      <van-col :span="4">
        <van-image
            v-if="!myMessage(d.from)"
            error-icon="smile-o"
            class="userImg"
            width="30" height="30"
            round
            position="left" :src="loadAvatar(d.from)"/>
      </van-col>

      <van-col :span="16" :style="myMessage(d.from)? 'text-align: right;':'text-align: left;'">
        <div class="messageContentHeader">
          {{ getUserById(d.from).name + " " + new Date(d.createTime).toLocaleString() }}
        </div>
        <div class="messageContent">
          {{ d.content }}
        </div>
      </van-col>

      <van-col :span="4">
        <van-image
            v-if="myMessage(d.from)"
            error-icon="smile-o"
            class="userImg"
            width="30" height="30"
            round
            position="left" :src="loadAvatar(d.from)"/>
      </van-col>
    </van-row>
  </van-row>
</template>

<script>
export default {
  name: "ChatContext",
  data() {
    return {
      userMap: [],
    }
  },
  props: ["data", "targetUser", "loginUser"],
  methods: {
    myMessage(fromId) {
      return fromId === this.loginUser._id
    },
    getUserById(id) {
      return id === this.loginUser._id ? this.loginUser : this.targetUser
    },
    loadAvatar(id) {
      let user = this.getUserById(id);
      return user.avatar ? user.avatar : 'avatar-' + (user.gender === 1 ? '1' : '2') + ".jpg";
    },
  },
  updated() {
  },
  created() {
  }
}
</script>

<style scoped>
.messageRow {
  margin-top: 10px;
}

.messageBox {
  text-align: left;
}

.messageContentHeader {
  font-size: 9px;
  font-weight: bolder;
  color: darkgrey;
  margin-bottom: 5px;
}

.messageContent {
  font-size: 13px;
  background-color: darkgrey;
  border-radius: 10px;
  padding: 10px;
}
</style>