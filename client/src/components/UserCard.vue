<template>
  <van-cell is-link to="/">
    <template #title>
      <van-row type="flex">
        <van-col span="4">
          <van-badge dot :color="color">
            <van-image error-icon="smile-o" class="userImg" width="40" height="40" round position="left" :src="avatar"/>
          </van-badge>
        </van-col>
        <van-col offset="1" span="19" style="height: 100%;">
          <van-row type="flex" class="usernameFont">
            {{ user.name }}
          </van-row>
        </van-col>
      </van-row>
    </template>
  </van-cell>
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
  props: ['user', 'showStatus'],

  methods: {
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
  margin-top: 7px
}

.userImg {
}

.userTag {
  margin-left: 5px;
}
</style>