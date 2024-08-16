<template>
  <div>

    <van-nav-bar title="详情" left-text="返回"
                 left-arrow @click-left="back"/>
    <post-box v-if="post" :post="post" :preview="false"></post-box>
  </div>
</template>

<script>
import {findById} from "@/api/post";
import {Toast} from "vant";
import PostBox from "@/components/PostBox";
import UserCard from "@/components/UserCard";

export default {
  name: "PostDetail",
  components: {UserCard, PostBox},
  data() {
    return {
      id: '',
      post: null
    }
  },
  methods: {
    loadPostById() {
      findById(this.id).then(res => {
        if (res.success) {
          this.post = res.data
        } else {
          Toast('服务器异常')
        }
      })
    },
    back() {
      this.$router.go(-1)
    },
    convertDate(time) {
      const timeDifference = new Date().getTime() - time;
      const hoursDifference = Math.floor(timeDifference / (1000 * 60 * 60));
      if (hoursDifference < 24) {
        if (hoursDifference === 0) {
          return "刚刚"
        }
        return hoursDifference + "小时前"
      } else if (hoursDifference < 240) {
        return Math.floor(hoursDifference / 24) + "天前"
      }
      return new Date(time).toLocaleString()
    },
  },
  created() {
    this.id = this.$route.params.id;
    this.loadPostById()
  }

}
</script>

<style scoped>

</style>