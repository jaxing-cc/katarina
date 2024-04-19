<template>
  <div>
    <van-nav-bar title="" :max-size="2 * 1024 * 1024" left-text="退出房间" @click-left="onClickLeft"></van-nav-bar>
    <div v-if="!info">
      加载失败
    </div>
    <van-row>
      <van-col span="12">
        {{ leftPlayerId }}
      </van-col>
      <van-col span="12">
        {{ rightPlayerId }}
      </van-col>
    </van-row>
    <van-row>

    </van-row>
    <van-row>
      {{ mainPlayerId }}
    </van-row>
  </div>
</template>

<script>
import {exitRoom} from "@/api/ddz";
import {Toast} from "vant";
import {decodeToken} from "@/utils/token";

export default {
  name: "DdzGamePage",
  data() {
    return {
      info: {
        "id": "13aa99",
        "name": "a",
        "ownerId": "64d204c182879a330e56f6f1",
        "players": [{
          "id": "64d204c182879a330e56f6f1",
          "name": "不爱吃早饭",
          "avatar": "66138b4ccb66d83ebfab8e67",
          "ready": false,
          "roomId": "13aa99"
        }, {
          "id": "64d204a282879a330e56f6f0",
          "name": "管理员",
          "avatar": "66138aabcb66d83ebfab8e65",
          "ready": false,
          "roomId": "13aa99"
        }, null],
        "playerMap": {"64d204c182879a330e56f6f1": 0, "64d204a282879a330e56f6f0": 1},
        "gameStatus": "WAIT",
        "size": 2,
        "master": null,
        "current": null,
        "callList": null,
        "lastPush": null,
        "pokerGroups": [[3, 19, 24], [21, 31, 53, 7, 0, 34, 30, 11, 36, 16, 18, 41, 47, 52, 22, 32, 25], [42, 38, 17, 35, 10, 29, 43, 46, 12, 15, 23, 44, 27, 40, 39, 28, 33], [45, 1, 6, 51, 37, 2, 20, 26, 48, 50, 14, 13, 49, 5, 9, 8, 4]]
      },
      loginInfo: {},
      mainPlayerId: "",
      leftPlayerId: null,
      rightPlayerId: null,

    }
  },
  methods: {
    onClickLeft() {
      exitRoom().then(res => {
        if (!res.success) {
          Toast(res.msg)
        }
        this.$router.push("/ddz")
      })
    },
    setPlayerId(){
      let players = this.info.players;
      let index = 0;
      for (let i = 0; i < players.length; i++) {
        if (players[i] && players[i].id === this.mainPlayerId) {
          index = i;
          break;
        }
      }
      this.leftPlayerId = players[(index + 1) % 3] ? players[(index + 1) % 3].id : null
      this.rightPlayerId = players[(index + 2) % 3] ? players[(index + 2) % 3].id : null
    },
    chatMsgHandler(data) {
      this.info = data.detail;
      this.setPlayerId()
    }
  },
  created() {
    this.loginInfo = decodeToken();
    this.mainPlayerId = this.loginInfo.uid
    // this.$socket.connect()
    window.addEventListener("msg@1002", this.chatMsgHandler)
    this.setPlayerId()
  },

  destroyed() {
    window.removeEventListener("msg@1002", this.chatMsgHandler)
  }
}
</script>

<style scoped>

</style>