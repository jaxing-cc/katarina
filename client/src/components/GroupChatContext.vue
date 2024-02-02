<template>
  <div>
    <div v-if="data" v-for="(d,index) in data" :key="index">
      <span v-if="groupFlagMap && groupFlagMap[index]" class="chatTime">
        {{ new Date(groupFlagMap[index]).toLocaleString() }}
      </span>
      <van-row type="flex" class="messageRow" :class="{ right: myMessage(d.from) }">
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
            {{ getUserById(d.from) ? getUserById(d.from).name : "未知" }}
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
    </div>
  </div>
</template>

<script>
import {getFileUrl} from "@/api/file";
import {getUserInfo} from "@/utils/token";

export default {
  name: "GroupChatContext",
  data() {
    return {
      self:{},
      userMap: [],
      groupFlagMap: null
    }
  },
  props: ["data"],
  methods: {
    myMessage(fromId) {
      return fromId === this.self._id
    },
    getUserById(id) {
      return id === this.self._id ? this.self : this.targetUser
    },
    loadAvatar(id) {
      let user = this.getUserById(id);
      if (!user){
        return "avatar-1.jpg"
      }
      return user.avatar ? getFileUrl(user.avatar) : 'avatar-' + (user.gender === 1 ? '1' : '2') + ".jpg";
    },
    groupByTime() {
      let dataList = this.data;
      this.groupFlagMap = [];
      let last = 0;
      if (dataList && dataList.length > 0) {
        this.groupFlagMap[last] = dataList[last].createTime;
        for (let i = 1; i < dataList.length; i++) {
          // 五分钟以内分一组
          if ((dataList[i].createTime - this.groupFlagMap[last]) > (1000 * 60 * 10)) {
            this.groupFlagMap[i] = dataList[i].createTime;
            last = i;
          }
        }
      }
    }
  },
  watch: {
    data(oldV, newV) {
      this.groupByTime()
    }
  },
  updated() {

  },
  mounted() {
  },
  created() {
    this.self = getUserInfo()
  }
}
</script>

<style scoped>
.chatTime {
  font-size: 9px;
  color: darkgrey;
}

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