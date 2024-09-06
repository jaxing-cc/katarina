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
            <van-tag mark type="primary" v-if="info.master === info.playerMap[lid]">地主</van-tag>
            <span class="markFont" v-if="myTurn(lid)">
              出牌中...
            </span>
            <!--准备阶段-->
            <van-row style="font-size: 13px" v-if="info.gameStatus === 'WAIT'">
              <van-icon name="success" color="#1989fa" v-if="player(lid).ready">已准备</van-icon>
              <van-icon name="cross" color="darkseagreen" v-if="!player(lid).ready">未准备</van-icon>
            </van-row>
            <!--CALL阶段-->
            <div class="markFont" v-if="info.gameStatus === 'CALL' && !myTurn(lid)">
              {{ getCallValue(lid) }}
            </div>
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
            <van-tag mark type="primary" v-if="info.master === info.playerMap[rid]">地主</van-tag>
            <span class="markFont" v-if="myTurn(rid)">
              出牌中...
            </span>
            <!--准备阶段-->
            <van-row style="font-size: 13px" v-if="info.gameStatus === 'WAIT'">
              <van-icon name="success" color="#1989fa" v-if="player(rid).ready">已准备</van-icon>
              <van-icon name="cross" color="darkseagreen" v-if="!player(rid).ready">未准备</van-icon>
            </van-row>
            <!--CALL阶段-->
            <div class="markFont" v-if="info.gameStatus === 'CALL' && !myTurn(rid)">
              {{ getCallValue(rid) }}
            </div>
          </div>
          <div v-if="!rid">
            等待加入...
          </div>
        </van-col>
      </van-row>

      <div style="margin-top: 10%">
        <div>
          游戏阶段: {{ convertGameState(info.gameStatus) }}
        </div>
        <div v-if="info.gameStatus === 'UNDERWAY'">
          <span v-if="info.lastPushIndex">
            {{'出牌人<' + info.players[info.lastPushIndex].name + '>' }}
          </span>
          <div v-for="p in info.lastPush" :key="p" class="poker_unselected">
            {{  convertPokerValue(p) }}
          </div>
        </div>
        <div v-if="info.gameStatus === 'CALL'">
          <div v-for="p in info.pokerGroups[3]" :key="p" class="poker_unselected">
            {{ convertPokerValue(p) }}
          </div>
        </div>
        <div v-if="info.gameStatus === 'FINISH'">
          <div v-if="mid === info.ownerId">
            <van-button round type="warning" size="normal" @click="reset" block>
              开始新游戏
            </van-button>
          </div>
          <div v-if="mid !== info.ownerId">
            游戏已结束，等待房主开始新游戏
          </div>
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
        <van-col span="10" class="markFont">
          <van-tag mark type="primary" v-if="info.master === info.playerMap[mid]">地主</van-tag>
          <span v-if="myTurn(mid)"> 你的回合</span>
          <span v-if="info.gameStatus === 'CALL' && !myTurn(mid)">
            {{ getCallValue(mid) }}
          </span>
        </van-col>
      </van-row>
      <!--用户牌组-->
      <div v-if="info.gameStatus === 'CALL' || info.gameStatus === 'UNDERWAY'">
        <div v-for="p in info.pokerGroups[info.playerMap[mid]]" :key="p"
             :class="selected.has(p) ? 'poker_selected':'poker_unselected'" @click="selectPoker(p)">
          {{ convertPokerValue(p) }}
        </div>
      </div>
      <!--准备阶段-->
      <van-row style="font-size: 13px;margin: 5px" v-if="info.gameStatus === 'WAIT'">
        <van-button round type="default" size="normal" block @click="ready(true)" v-if="!player(mid).ready">
          准备
        </van-button>
        <van-button round type="warning" size="normal" block @click="ready(false)" v-if="player(mid).ready">
          取消准备
        </van-button>
      </van-row>
      <!--叫地主阶段-->
      <van-row style="font-size: 13px;margin: 5px" v-if="info.gameStatus === 'CALL' && myTurn(mid)">
        <van-col span="6">
          <van-button round type="default" size="normal" @click="call(0)" block>
            不叫
          </van-button>
        </van-col>
        <van-col span="6">
          <van-button round type="default" size="normal" @click="call(1)" block>
            1分
          </van-button>
        </van-col>
        <van-col span="6">
          <van-button round type="default" size="normal" @click="call(2)" block>
            2分
          </van-button>
        </van-col>
        <van-col span="6">
          <van-button round type="default" size="normal" @click="call(3)" block>
            3分
          </van-button>
        </van-col>


      </van-row>
      <!--进行阶段-->
      <van-row style="font-size: 13px;margin: 5px" v-if="info.gameStatus === 'UNDERWAY' && myTurn(mid)">
        <van-col span="8">
          <van-button round type="default" size="normal" @click="pop(false)" block>
            出牌
          </van-button>
        </van-col>
        <van-col span="8">
          <van-button round type="default" size="normal" @click="pop(true)" block>
            不出
          </van-button>
        </van-col>
        <van-col span="8">
          <van-button round type="default" size="normal" @click="resetPoker" block>
            重置
          </van-button>
        </van-col>
      </van-row>

    </van-row>
  </div>
</template>

<script>
import {callMaster, exitRoom, pop, readyRequest, restart} from "@/api/ddz";
import {decodeToken} from "@/utils/token";
import {getAvatarUrlOrDefault} from "@/api/file";
import {Toast} from "vant";
import UserCard from "@/components/UserCard";
import pokerMap from "@/utils/poker";

export default {
  name: "DdzGamePage",
  components: {UserCard},
  data() {
    return {
      info: {
        "id": null,
        "name": null,
        "ownerId": null,
        "players": [],
        "playerMap": {},
        "gameStatus": "CALL",
        "size": 3,
        "master": null,
        "current": 1,
        "callList": [null, null, null],
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

    /**
     * 编码转换
     */
    convertPokerValue(index) {
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
    convertGameState(state) {
      if (state === 'WAIT') {
        return '准备中'
      }
      if (state === 'CALL') {
        return '叫地主'
      }
      if (state === 'UNDERWAY') {
        return '进行中'
      }
      if (state === 'FINISH') {
        return '游戏结束'
      }
    },

    /**
     * 获取头像
     */
    avatar(key) {
      return getAvatarUrlOrDefault(key);
    },
    /**
     * 准备/取消准备
     */
    ready(ready) {
      readyRequest().then(res => {
        if (res.success) {
          Toast(ready ? "已准备" : "已取消准备")
        } else {
          Toast(res.msg)
        }
      })
    },
    /**
     * id获取用户信息
     */
    player(id) {
      let index = this.info.playerMap[id];
      return this.info.players[index] ? this.info.players[index] : {}
    },
    /**
     * id获取叫地主信息
     */
    getCallValue(id) {
      let index = this.info.playerMap[id];
      if (this.info.callList) {
        if (this.info.callList[index] === null) {
          return ''
        } else if (this.info.callList[index] === 0) {
          return '不叫'
        } else {
          return '叫地主:' + this.info.callList[index] + '分';
        }
      }
      return ''
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
     * 设置三个用户的id: lid/rid/mid
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
     * 是否是我的回合
     */
    myTurn(id) {
      if (this.info) {
        return this.info.current != null && this.info.playerMap[id] === this.info.current
      } else {
        return false
      }
    },

    /**
     * 叫地主
     */
    call(v) {
      callMaster(v).then(res => {
        if (res.success) {
          Toast(v > 0 ? '叫地主!' : '不叫')
        } else {
          Toast(res.msg)
        }
      })
    },

    /**
     * 出牌
     */
    pop(pass) {
      let pokers = []
      for (const item of this.selected) {
        pokers.push(item)
      }
      if (pokers.length === 0 && !pass){
        Toast('请选择牌组')
        return
      }
      let data = {
        ids: pass ? [] : pokers
      }
      pop(data).then(res => {
        if (!res.success) {
          Toast(res.msg)
        } else {
          this.resetPoker()
        }
      })
    },

    /**
     * 重置
     */
    resetPoker() {
      this.selected = new Set();
    },
    reset() {
      restart().then(res => {
        if (!res.success) {
          Toast(res.msg)
        }
      })
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