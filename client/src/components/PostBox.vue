<template>
  <van-row class="wrapper">
    <van-sticky v-if="!preview">
      <van-row>
        <van-col :span="1"/>
        <van-col :span="22">
          <UserCard v-if="post.user" :user="post.user" :img-size="30" :showText="convertDate(post.createTime)"
                    :follow="2"/>
        </van-col>
        <van-col :span="1"/>
      </van-row>
    </van-sticky>
    <van-row v-if="preview">
      <van-col :span="1"/>
      <van-col :span="22">
        <UserCard v-if="post.user" :user="post.user" :img-size="30" :showText="convertDate(post.createTime)"
                  :follow="2"/>
      </van-col>
      <van-col :span="1"/>
    </van-row>

    <van-row @click="openPostDetail">
      <van-col :span="2"/>
      <van-col :span="20">
        <div class="contentWrapper">
          <div v-if="post.title"
               :style="preview ? 'font-weight: bolder' : 'font-weight: bolder;font-size: 15px;margin-bottom: 10px;'">
            {{ post.title }}
          </div>
          <div v-if="!post.title || !preview" :class="preview ? 'contentText van-multi-ellipsis--l3' : 'contentEmpty'">
            {{ post.content }}
          </div>
          <br>
          <div v-for="(key,i) in images" :key="key" class="contentImageContainer">
            <van-image @click.stop="previewImage(i)" :src="key" fit="contain" lazy-load
                       :width="preview ? '80px': '100%'" class="contentImage">
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
        <van-grid-item @click="thumbup" :icon="thumbuped ? 'like': 'like-o'" :text="thumbupCount + ''"/>
      </van-grid>
    </van-row>
  </van-row>
</template>

<script>
import {getFileUrl} from "@/api/file";
import UserCard from "@/components/UserCard";
import {ImagePreview, Toast} from "vant";
import {thumbup} from "@/api/thumbup";
import {convertDate} from "@/utils/util";

export default {
  name: "PostBox",
  components: {UserCard},
  props: {
    post: {},
    preview: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      avatar: '',
      images: [],
      lastImagesSize: 0,
      thumbuped: false,
      thumbupCount: 0
    }
  },
  methods: {

    openPostDetail() {
      if (this.preview) {
        this.$router.push("/post/detail/" + this.post.id)
      }
    },
    loadAvatar() {
      let user = this.post.user
      if (user) {
        this.avatar = user.avatar ? getFileUrl(user.avatar) : 'avatar-' + (user.gender === 1 ? '1' : '2') + ".jpg";
      }
    },
    loadImage() {
      let file = [];
      let n = this.post.images.length;
      let limit = this.preview ? 4 : n;
      this.lastImagesSize = n < limit ? 0 : n - limit;
      for (let i = 0; i < limit && i < n; i++) {
        file.push(getFileUrl(this.post.images[i]))
      }
      this.images = file;
    },
    convertDate(time) {
      return convertDate(time)
    },
    previewImage(i) {
      ImagePreview({
        images: this.images,
        showIndex: true,
        closeable: true,
        startPosition: i,
        loop: false
      })
    },
    thumbup() {
      thumbup({
        targetId: this.post.id,
        type: 'post'
      }).then(res => {
        if (res.success) {
          this.thumbuped = !this.thumbuped
          console.log(this.post)
          if (res.data) {
            this.thumbupCount++;
            Toast('点赞成功')
          } else {
            this.thumbupCount--;
            Toast('已取消')
          }
        } else {
          Toast('网络异常')
        }
      });
    }
  },
  updated() {
  },
  created() {
    this.loadAvatar()
    this.loadImage()
    this.thumbuped = this.post.thumbuped
    this.thumbupCount = this.post.thumbupCount;
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
  font: 13px/1.5 Tahoma, Helvetica, Arial, '宋体', sans-serif;
}

.contentEmpty {

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