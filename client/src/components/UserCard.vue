<template>
  <div>
    <van-cell center is-link value="">
      <template #title>
        <van-image class="userImg" width="40" height="40" round position="left" :src="user.avatar"/>
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
    return {};
  },

  mounted() {

  },

  props: ['user', 'showStatus'],

  methods: {},
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