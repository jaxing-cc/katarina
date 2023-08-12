<template>
  <van-row>
    <van-row type="flex" v-if="data" v-for="(d,index) in data" :key="d._id" class="messageRow"
             :class="{ right: myMessage(d.from) }">
      <van-col v-if="!myMessage(d.from)" :span="4">
        <van-image
            error-icon="smile-o"
            class="userImg"
            width="30" height="30"
            round
            position="left" :src="loadAvatar(d.from)"/>
      </van-col>

      <van-col :span="16" :class="myMessage(d.from)? 'right_msg' : 'left_msg'">
        <div class="messageContentHeader">
          {{ getUserById(d.from).name + " " + new Date(d.createTime).toLocaleString() }}
        </div>
        <div class="messageContent">
          {{ d.content }}
        </div>
      </van-col>

      <van-col v-if="myMessage(d.from)" :span="4">
        <van-image
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

.messageRow.right {
  justify-content: end;
}

.right_msg {
  text-align: right;
}

.left_msg {
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
  font-weight: bolder;
  line-height: 1.5;
  border-radius: 10px;
  word-break: break-all;
  padding: 10px;
  text-align: left;
  width: fit-content;
  display: inline-block;
  background-color: #f0f0ee;
}

</style>