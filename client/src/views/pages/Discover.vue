<template>
  <div>
    <van-tabs v-model="tabsKey" animated sticky>
      <van-tab>
        <template #title>
          <van-icon name="friends" /> 发现
        </template>
      </van-tab>
      <van-tab>
        <template #title>
          <van-icon name="star"/> 关注
        </template>
      </van-tab>
      <van-tab>
        <template #title>
          <van-icon name="star"/> 我的
        </template>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import GroupChatContext from "@/components/GroupChatContext";
import Chat from "@/views/pages/other/Chat";
import {getUserInfo} from "@/utils/token";

export default {
  name: "Discover",
  components: {Chat, GroupChatContext},
  data() {
    return {
      loginUser: {},
      tabsKey: 0
    }
  },
  created() {
    this.loginUser = getUserInfo();
  },
}
</script>

<style scoped>

</style>