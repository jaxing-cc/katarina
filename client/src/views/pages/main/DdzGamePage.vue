<template>
  <div>
    <van-nav-bar title="xxx" :max-size="2 * 1024 * 1024" left-text="退出房间" @click-left="onClickLeft"></van-nav-bar>
    {{info}}
  </div>
</template>

<script>
import {exitRoom} from "@/api/ddz";
import {Toast} from "vant";

export default {
  name: "DdzGamePage",
  data() {
    return {
      info: null
    }
  },
  methods:{
    onClickLeft() {
      exitRoom().then(res => {
        if (res.success){
          this.$router.push("/ddz")
        }else{
          Toast(res.msg)
        }
      })
    },
    chatMsgHandler(data){
      this.info = data.detail
    }
  },
  created() {
    window.addEventListener("msg@1002", this.chatMsgHandler)
    this.$socket.connect()
  },
  destroyed() {
    window.removeEventListener("msg@1002", this.chatMsgHandler)
  }
}
</script>

<style scoped>

</style>