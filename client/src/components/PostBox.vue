<template>
  <van-row class="wrapper">
    <van-row>
      <van-col :span="1"/>
      <van-col :span="22">
        <UserCard :user="post.user" :img-size="30" :showText="convertDate(post.createTime)" :follow="2"/>
      </van-col>
      <van-col :span="1"/>
    </van-row>

    <van-row @click="openPostDetail">
      <van-col :span="2"/>
      <van-col :span="20">
        <div class="contentWrapper">
          <div v-if="post.title" style="font-weight: bolder">{{ post.title }}</div>
          <div v-if="!post.title" class="contentText van-multi-ellipsis--l3">
            {{ post.content }}
          </div>
          <br>
          <div v-for="(key,i) in images" :key="key" class="contentImageContainer">
            <van-image :src="key" fit="contain" lazy-load width="80" class="contentImage">
              <template v-slot:loading>
                <van-loading type="spinner"/>
              </template>
            </van-image>
            <div v-if="i === images.length - 1 && lastImagesSize > 0" class="contentImageLast">
              <span class="contentImageTag">+{{ lastImagesSize }}</span>
            </div>
          </div>
        </div>
      </van-col>
      <van-col :span="2"/>
    </van-row>

    <van-row>
      <van-grid direction="horizontal"
                :border="false" clickable icon-size="15"
                :column-num="3">
        <van-grid-item/>
        <van-grid-item icon="comment-o" text="120"/>
        <van-grid-item icon="star-o" text="12"/>
      </van-grid>
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
      avatar: '',
      images: [],
      lastImagesSize: 0,
    }
  },
  methods: {

    openPostDetail() {
      console.log(this.post.id)
      this.$router.push("/post/detail/" + this.post.id)
    },
    loadAvatar() {
      let user = this.post.user
      this.avatar = user.avatar ? getFileUrl(user.avatar) : 'avatar-' + (user.gender === 1 ? '1' : '2') + ".jpg";
    },
    loadImage() {
      let file = [];
      let n = this.post.images.length;
      this.lastImagesSize = n < 4 ? 0 : n - 3;
      for (let i = 0; i < 4 && i < n; i++) {
        file.push(getFileUrl(this.post.images[i]))
      }
      this.images = file;
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
  updated() {
    this.loadAvatar()
    this.loadImage()
  },
  created() {
    this.loadAvatar()
    this.loadImage()
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

.contentImageContainer {
  position: relative; /* 使子元素可以绝对定位 */
  display: inline-block; /* 使容器适应内容 */
}

.contentImage {

}

.contentImageLast {
  height: 100%;
  width: 100%;
  position: absolute; /* 绝对定位 */
  top: 50%; /* 垂直居中 */
  left: 50%; /* 水平居中 */
  transform: translate(-50%, -50%); /* 使文字完全居中 */
  color: white; /* 文字颜色 */
  font-size: 24px; /* 文字大小 */
  background-color: rgba(0, 0, 0, 0.5); /* 背景颜色，半透明 */
}

.contentImageTag {
  position: absolute; /* 绝对定位 */
  top: 50%; /* 垂直居中 */
  left: 50%; /* 水平居中 */
  transform: translate(-50%, -50%); /* 使文字完全居中 */
}
</style>