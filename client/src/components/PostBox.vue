<template>
  <van-row class="wrapper">
    <van-row>
      <van-col :span="1"/>
      <van-col :span="22">
        <UserCard :user="post.user" :img-size="30" :showText="convertDate(post.createTime)" :follow="2"/>
      </van-col>
      <van-col :span="1"/>
    </van-row>

    <van-row>
      <van-col :span="2"/>
      <van-col :span="20">
        <div class="contentWrapper">
          <div class="contentText">
            {{ post.content }}
          </div>
          <div class="contentImages">
            {{ post.images }}
          </div>
        </div>
      </van-col>
      <van-col :span="2"/>
    </van-row>
  </van-row>
</template>

<script>
import {getFileUrl} from "@/api/file";
import UserCard from "@/components/UserCard";

export default {
  name: "PostBox",
  components: {UserCard},
  props: ["post"],
  data() {
    return {
      avatar: ''
    }
  },
  methods: {
    loadAvatar() {
      let user = this.post.user
      this.avatar = user.avatar ? getFileUrl(user.avatar) : 'avatar-' + (user.gender === 1 ? '1' : '2') + ".jpg";
    },
    convertDate(time) {
      const timeDifference = new Date().getTime() - time;
      const hoursDifference = Math.floor(timeDifference / (1000 * 60 * 60));
      if (hoursDifference < 24) {
        if (hoursDifference === 0){
          return "刚刚"
        }
        return hoursDifference + "小时前"
      } else if (hoursDifference < 240) {
        return Math.floor(hoursDifference / 24) + "天前"
      }
      return new Date(time).toLocaleString()
    }
  },
  updated() {
    this.loadAvatar()

  },
  created() {
    this.loadAvatar()
  }

}
</script>

<style scoped>
.wrapper {
  background-color: white;
  margin-bottom: 5px;
  border-radius: 3px;
}

.contentWrapper {
  font: 12px/1.5 Tahoma, Helvetica, Arial, '宋体', sans-serif;
}

.contentText {

}
</style>