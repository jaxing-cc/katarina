<template>
  <van-row type="flex" @click="click" class="wrapper">

    <van-col span="4" class="marginTop">
      <van-badge dot :color="color">
        <van-image error-icon="smile-o" class="userImg" :width="imgSize" :height="imgSize"
                   round position="left" :src="avatar"/>
      </van-badge>
    </van-col>

    <van-col v-if="!showText" offset="1" span="14" class="marginTop">
      <van-row type="flex" class="usernameFont">
        {{ user.name }}
      </van-row>
    </van-col>

    <van-col v-if="showText" offset="1" span="14" class="marginTop">
      <van-row type="flex" style="font-weight: bolder; font-size: 13px" class="van-ellipsis">{{ user.name }}</van-row>
      <van-row type="flex" style="font-size: 9px;" class="van-ellipsis">{{ showText }}</van-row>
    </van-col>

    <van-col span="5" class="marginTop">
      <van-badge v-if="unread && unread !== 0" :content="unread"/>
      <div v-if="follow === 2 && this.userId !== user._id">
        <van-button v-if="followed()" size="mini" color="#D7DBDD" @click.stop="associate(0)">
          已关注
        </van-button>
        <van-button v-if="!followed()" size="mini" color="#d47982" @click.stop="associate(1)">
          关注
        </van-button>
      </div>
    </van-col>

  </van-row>
</template>

<script>
import {getFileUrl} from "@/api/file";
import {follow, followList} from "@/api/auth";
import {Toast} from "vant";
import store from "@/store";
import {decodeToken} from "@/utils/token";

export default {
  name: 'UserCard',

  data() {
    return {
      avatar: '',
      color: "red",
      userId: '',
    };
  },


  updated() {
    this.loadAvatar()
    this.loadBadge()
  },

  created() {
    this.userId = decodeToken()['uid'];
    this.loadAvatar()
    this.loadBadge()
  },
  props: {
    user: {},
    showText: {
      type: String
    },
    imgSize: {
      type: Number,
      default: 40
    },
    // 0不展示 1展示 2展示并可点击
    follow: {
      type: Number,
      default: 0
    },
    unread: {
      type: Number,
      default: 0
    }
  },

  methods: {
    click() {
      this.$emit("click", this.user)
    },
    loadAvatar() {
      if (this.user) {
        this.avatar = this.user.avatar ?
            getFileUrl(this.user.avatar) : 'avatar-' + (this.user.gender === 1 ? '1' : '2') + ".jpg";
      }
    },
    loadBadge() {
      this.color = this.user.online ? "#1989fa" : "red";
    },
    followed() {
      return this.$store.state.followList.has(this.user._id)
    },
    associate(action) {
      follow(this.user._id, action).then(res => {
        if (res.success) {
          followList().then(res => {
            if (res.success) {
              store.commit('setFollowList', res.data);
              Toast(action === 1 ? '关注成功' : '取关成功')
            }
          })
        } else {
          Toast('服务器异常')
        }
      })
    }
  },
};
</script>

<style lang="scss" scoped>
.usernameFont {
  height: 100%;
  font-weight: bolder;
}

.marginTop {
  margin-top: 10px;
}

.userImg {
  margin-left: 10px;
}

.wrapper {
  height: 55px;
}

.wrapper:hover {
  border-radius: 10px;
  background-color: #f7f8f6;
}
</style>