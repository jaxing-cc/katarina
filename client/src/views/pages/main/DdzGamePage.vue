<template>
  <div class="wrapper">
    <van-nav-bar title="" :max-size="2 * 1024 * 1024" left-text="退出房间" @click-left="onClickLeft"></van-nav-bar>
    <div v-if="!info">
      加载失败
    </div>
    <div v-if="info" class="content">
      <van-row>
        <van-col span="12">
          <div v-if="lid">
            <van-row type="flex">
              <van-col span="8">
                <van-image error-icon="smile-o" class="userImg" width="35" height="35" round position="left"
                           :src="avatar(player(lid).avatar)"/>
              </van-col>
              <van-col span="16" class="usernameFont">
                {{ player(lid).name }}
              </van-col>
            </van-row>
            <div class="markFont" v-if="info.current != null && info.playerMap[lid] === info.current">
              出牌中...
            </div>
            <!--准备阶段-->
            <van-row style="font-size: 13px" v-if="info.gameStatus === 'WAIT'">
              <van-icon name="success" color="#1989fa" v-if="info.players[info.playerMap[lid]].ready">已准备</van-icon>
              <van-icon name="cross" color="darkseagreen" v-if="!info.players[info.playerMap[lid]].ready">未准备</van-icon>
            </van-row>
          </div>
          <div v-if="!lid">
            等待加入...
          </div>
        </van-col>

        <van-col span="12">
          <div v-if="rid">
            <van-row type="flex">
              <van-col span="8">
                <van-image error-icon="smile-o" class="userImg" width="35" height="35" round position="left"
                           :src="avatar(player(rid).avatar)"/>
              </van-col>
              <van-col span="16" class="usernameFont">
                {{ player(rid).name }}
              </van-col>
            </van-row>
            <div class="markFont" v-if="info.current != null && info.playerMap[rid] === info.current">
              出牌中...
            </div>
            <!--准备阶段-->
            <van-row style="font-size: 13px" v-if="info.gameStatus === 'WAIT'">
              <van-icon name="success" color="#1989fa" v-if="info.players[info.playerMap[rid]].ready">已准备</van-icon>
              <van-icon name="cross" color="darkseagreen" v-if="!info.players[info.playerMap[rid]].ready">未准备</van-icon>
            </van-row>
          </div>
          <div v-if="!rid">
            等待加入...
          </div>
        </van-col>
      </van-row>

      <div style="margin-top: 10%">
        <div v-for="p in info.lastPush" :key="p" class="poker_unselected">
          {{ pokerValue(p) }}
        </div>
      </div>
    </div>

    <van-row v-if="info" class="footer">
      <!--用户头像-->
      <van-row type="flex">
        <van-col span="4">
          <van-image error-icon="smile-o" class="userImg" width="35" height="35"
                     round position="left" :src="avatar(player(mid).avatar)"/>
        </van-col>
        <van-col span="10" class="usernameFont">
          {{ player(mid).name }}
        </van-col>
        <van-col span="10" class="markFont" v-if="info.current != null && info.playerMap[mid] === info.current">
          你的回合
        </van-col>
      </van-row>
      <!--用户牌组-->
      <div v-if="info.gameStatus === 'CALL' || info.gameStatus === 'UNDERWAY'">
        <div v-for="p in info.pokerGroups[info.playerMap[mid]]" :key="p"
             :class="selected.has(p) ? 'poker_selected':'poker_unselected'" @click="selectPoker(p)">
          {{ pokerValue(p) }}
        </div>
      </div>
      <!--准备阶段-->
      <van-row style="font-size: 13px;margin: 5px" v-if="info.gameStatus === 'WAIT'">
        <van-button round type="default" size="normal" block @click="ready(true)" v-if="!info.players[info.playerMap[mid]].ready">
          准备
        </van-button>
        <van-button round type="warning" size="normal" block @click="ready(false)" v-if="info.players[info.playerMap[mid]].ready">
          取消准备
        </van-button>
      </van-row>
    </van-row>
  </div>
</template>

<script>
import {exitRoom, readyRequest} from "@/api/ddz";
import {Toast} from "vant";
import {decodeToken} from "@/utils/token";
import UserCard from "@/components/UserCard";
import {getAvatarUrlOrDefault} from "@/api/file";
import pokerMap from "@/utils/poker";

export default {
  name: "DdzGamePage",
  components: {UserCard},
  data() {
    return {
      info: {
        "id": "13aa99",
        "name": "a",
        "ownerId": "64d204c182879a330e56f6f1",
        "players": [{
          "id": "64d204c182879a330e56f6f1",
          "name": "测试2",
          "avatar": "66138b4ccb66d83ebfab8e67",
          "gender": 1,
          "ready": false,
          "roomId": "13aa99"
        },
          {
            "id": "64d204a282879a330e56f6f0",
            "name": "测试1",
            "avatar": "66138aabcb66d83ebfab8e65",
            "gender": 1,
            "ready": false,
            "roomId": "13aa99"
          },
          {
            "id": "64d204a282879a330e56f6f2",
            "name": "测试3",
            "avatar": null,
            "gender": 1,
            "ready": false,
            "roomId": "13aa99"
          }],
        "playerMap": {"64d204c182879a330e56f6f1": 0, "64d204a282879a330e56f6f0": 1, "64d204a282879a330e56f6f2": 2},
        "gameStatus": "WAIT",
        "size": 3,
        "master": null,
        "current": null,
        "callList": null,
        "lastPush": null,
        "pokerGroups": [
          [1, 6, 8, 10, 14, 13, 17, 21, 24, 27, 29, 30, 34, 43, 44, 50, 53],
          [2, 4, 7, 5, 11, 9, 12, 19, 18, 20, 33, 32, 38, 39, 37, 40, 47],
          [3, 0, 15, 23, 22, 25, 26, 28, 31, 35, 36, 42, 41, 45, 46, 49, 51],
          [16, 48, 52]
        ]
      },
      loginInfo: {},
      selected: new Set(),
      mid: null,
      lid: null,
      rid: null,
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

    pokerValue(index) {
      let poker = pokerMap[index];
      let type;
      if (poker.type === 0) {
        type = '♠'
      } else if (poker.type === 1) {
        type = '♥'
      } else if (poker.type === 2) {
        type = '♣'
      } else if (poker.type === 3) {
        type = '♦'
      }
      if (poker.value === 1) {
        return type + "A";
      } else if (poker.value === 11) {
        return type + "J";
      } else if (poker.value === 12) {
        return type + "Q";
      } else if (poker.value === 13) {
        return type + "K";
      } else if (poker.value === 14) {
        return "😅";
      } else if (poker.value === 15) {
        return "🤣";
      } else {
        return type + poker.value;
      }
    },
    avatar(key) {
      return getAvatarUrlOrDefault(key);
    },
    /**
     * 准备/取消准备
     */
    ready(ready) {
      readyRequest().then(res => {
        if (res.success) {
          Toast(ready ? "已准备": "已取消准备")
        } else {
          Toast(res.msg)
        }
      })
    },
    player(id) {
      let index = this.info.playerMap[id];
      return this.info.players[index] ? this.info.players[index] : {}
    },
    /**
     * 选择出牌
     */
    selectPoker(id) {
      let set = new Set(this.selected);
      if (this.selected.has(id)) {
        set.delete(id)
      } else {
        set.add(id)
      }
      this.selected = set;
    },
    /**
     * 设置三个用户的id
     */
    setPlayerId() {
      let players = this.info.players;
      let index = 0;
      for (let i = 0; i < players.length; i++) {
        if (players[i] && players[i].id === this.mid) {
          index = i;
          break;
        }
      }
      this.lid = players[(index + 1) % 3] ? players[(index + 1) % 3].id : null
      this.rid = players[(index + 2) % 3] ? players[(index + 2) % 3].id : null
    },
    /**
     * 服务器消息处理
     * @param data
     */
    chatMsgHandler(data) {
      this.info = data.detail;
      this.setPlayerId()
    }
  },
  created() {
    this.loginInfo = decodeToken();
    this.mid = this.loginInfo.uid
    this.$socket.connect()
    window.addEventListener("msg@1002", this.chatMsgHandler)
    this.setPlayerId()
  },

  destroyed() {
    window.removeEventListener("msg@1002", this.chatMsgHandler)
  }
}
</script>

<style scoped>

.wrapper {
  min-height: 100%;
  position: relative;
  box-sizing: border-box;
  padding-bottom: 100px;
}

.content {
}

.footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}

.usernameFont {
  font-weight: bolder;
}

.markFont {
  font-weight: bolder;
  font-size: 15px;
  color: darkseagreen;
}

.poker_selected {
  font-size: 10px;
  font-weight: bolder;
  display: inline-block;
  border-radius: 5px;
  border-style: solid;
  border-width: 1px;
  height: 35px;
  width: 23px;
  margin: 2px;
  background-color: darkgrey;
}

.poker_unselected {
  font-size: 10px;
  font-weight: bolder;
  display: inline-block;
  border-radius: 5px;
  border: 1px solid #afb8bc;
  height: 35px;
  width: 23px;
  margin: 2px;
  background-color: wheat;
}
</style>