<template>
  <van-row type="flex" @click="click" class="wrapper">

    <van-col span="4" class="marginTop">
      <van-badge dot :color="color">
        <van-image error-icon="smile-o" class="userImg" :width="imgSize"
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

    <van-col v-if="showText && other" span="4" class="marginTop">
      <van-badge v-if="unread && unread !== 0" :content="unread"/>
    </van-col>

  </van-row>
</template>

<script>
export default {
  name: 'UserCard',

  data() {
    return {
      avatar: '',
      color: "red"
    };
  },


  updated() {
    this.loadAvatar()
    this.loadBadge()
  },

  created() {
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
    other: {
      type: Boolean,
      default: false
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
      this.avatar = this.user.avatar ?
          this.user.avatar : 'avatar-' + (this.user.gender === 1 ? '1' : '2') + ".jpg";
    },
    loadBadge() {
      this.color = this.user.online ? "#1989fa" : "red";
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
}

.wrapper {
  height: 55px;
}

.wrapper:hover {
  border-radius: 10px;
  background-color: #f7f8f6;
}
</style>