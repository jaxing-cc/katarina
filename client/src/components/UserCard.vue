<template>
  <div>
    <van-cell center is-link value="">
      <template #title>
        <van-image class="userImg" width="40" height="40" round position="left"
                   :src="avatar"/>
        <span class="usernameFont">{{ user.name }}</span>
        <van-tag v-if="showStatus" :type="user.online? 'success':'warning'" class="userTag">
          {{ user.online ? "on" : "off" }}
        </van-tag>
      </template>
    </van-cell>
  </div>
</template>

<script>
export default {
  name: 'UserCard',

  data() {
    return {
      avatar: ''
    };
  },
  

  updated() {
    this.loadAvatar()
  },

  created() {
    this.loadAvatar()
  },
  props: ['user', 'showStatus'],

  methods: {
    loadAvatar(){
      this.avatar = this.user.avatar ?
          this.user.avatar : 'avatar-' + (this.user.gender === 1 ? '1' : '2') + ".jpg";
    }
  },
};
</script>

<style lang="scss" scoped>
.usernameFont {
  margin-left: 13px;
  font-weight: bolder;
  vertical-align: middle;
}

.userImg {
  vertical-align: middle;
}

.userTag {
  margin-left: 5px;
}
</style>