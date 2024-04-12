<template>
  <van-pull-refresh v-model="loading" @refresh="onRefresh">
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
        <van-col :span="16" :class="myMessage(d.from)? 'right_msg' : 'left_msg'" @click="demo(d)">
          <div class="messageContentHeader">
            {{ getUserById(d.from).name }}
          </div>
<!--          <van-row type="flex" :class="{ right: myMessage(d.from) }">-->
<!--            <van-col :span="4" v-if="myMessage(d.from)" >-->
<!--              <van-button round type="info" icon="plus" size="mini"/>-->
<!--            </van-col>-->
<!--            <van-col :span="20" :class="myMessage(d.from)? 'right_msg' : 'left_msg'">-->
<!--              <div class="messageContent">-->
<!--                {{ d.content }}-->
<!--              </div>-->
<!--            </van-col>-->
<!--            <van-col :span="4" v-if="!myMessage(d.from)" >-->
<!--              <van-button round type="info" icon="plus" size="mini"/>-->
<!--            </van-col>-->
<!--          </van-row>-->
<!--          <div class="messageContentHeader">-->
<!--            {{ getUserById(d.from).name }}-->
<!--          </div>-->
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
  </van-pull-refresh>
</template>

<script>
import {getFileUrl} from "@/api/file";

export default {
  name: "ChatContext",
  data() {
    return {
      popover: {
        show: false,
        actions: [{text: '选项一'}, {text: '选项二'}, {text: '选项三'}],
      },
      userMap: [],
      groupFlagMap: null,
      list: {},
      loading: false,
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
    },
    onRefresh() {
      let that = this;
      this.$emit("onLoad", () => {
        that.loading = false
      })
    },
    demo(data) {
      console.log(data)
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