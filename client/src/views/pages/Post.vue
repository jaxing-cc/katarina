<template>
  <div class="wrapper">
    <van-sticky>
      <van-grid direction="horizontal"
                :border="false" clickable icon-size="15"
                :column-num="4">
        <van-grid-item text="发现" :style="'font-weight:' + (routerIndex === 0?'bolder' :'normal')"
                       @click="goto(0)"/>
        <van-grid-item text="关注" :style="'font-weight:' + (routerIndex === 1?'bolder' :'normal')"
                       @click="goto(1)"/>
        <van-grid-item icon="plus" @click="goto(2)"/>
        <van-grid-item icon="search" @click="goto(3)"/>
      </van-grid>
    </van-sticky>
    <router-view/>
    <div style="height: 44px"></div>
  </div>
</template>

<script>
import GroupChatContext from "@/components/GroupChatContext";
import Chat from "@/views/pages/other/Chat";
import {getUserInfo} from "@/utils/token";

export default {
  name: "Post",
  components: {Chat, GroupChatContext},
  data() {
    return {
      loginUser: {},
      routerIndex: 0,
    }
  },
  methods: {
    goto(v) {
      this.routerIndex = v;
      if (v === 0) {
        this.$router.push("/")
      } else if (v === 1) {
        this.$router.push("/follow")
      }else if (v === 2) {
        this.$router.push("/write")
      }
    },
  },
  created() {
    this.loginUser = getUserInfo();
  },
}
</script>

<style scoped>
.test {
  font-weight: normal;
}

.wrapper {
  background-color: #f0f0ee;
  height: 100%;
}

.router {

}
</style>