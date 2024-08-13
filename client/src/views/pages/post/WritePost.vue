<template>
  <div>
    <van-nav-bar title=""
                 left-text="返回" right-text="发布"
                 left-arrow @click-left="back" @click-right="onClickPublish"/>
    <van-grid direction="horizontal" :border="false" clickable icon-size="15"
              :column-num="4">
      <van-grid-item text="动态" :style="'font-weight:' + (!markdown?'bolder' :'normal')" @click="markdown = false"/>
      <!--      <van-grid-item text="文章" :style="'font-weight:' + (markdown?'bolder' :'normal')" @click="markdown = true"/>-->
    </van-grid>
    <van-row v-if="!markdown">
      <van-form>
        <van-field
            v-model="form.title"
            name="title"
            placeholder="标题（选填）"/>
        <van-field
            v-model="form.content"
            name="content"
            rows="2"
            :autosize="true"
            type="textarea"
            maxlength="1000"
            placeholder="内容（必填）"
            show-word-limit/>
        <van-uploader v-model="form.imageFiles" :max-size="5 * 1024 * 1024" :max-count="9"
                      @oversize="oversize" :after-read="afterRead" :before-read="beforeRead"
                      :before-delete="beforeDelete"/>
      </van-form>
    </van-row>
  </div>
</template>

<script>
import {Dialog, Toast} from "vant";
import {remove, upload} from "@/api/file";
import {save} from "@/api/post";

export default {
  name: "WritePost",
  data() {
    return {
      markdown: 0,
      form: {
        title: null,
        content: null,
        cover: null,
        imageFiles: [],
      }
    }
  },
  methods: {
    onClickPublish() {
      Dialog.confirm({
        message: '确认发布吗?',
      }).then(() => {
        if (!this.form.content || this.form.content.length > 10000) {
          Toast('内容为空或超出限制')
          return true;
        }
        let images = []
        for (const imageFile of this.form.imageFiles) {
          images.push(imageFile['key'])
        }
        console.log(images)
        save({
          title: this.form.title,
          content: this.form.content,
          images: images,
          markdown: false
        }).then(res => {
          if (res.success) {
            Toast("发布成功")
            this.form.title = null;
            this.form.content = null;
            this.form.imageFiles = [];
          } else {
            Toast("发布失败")
          }
        })
      }).catch(() => {
      });
    },
    back() {
      this.$router.go(-1)
    },
    onSubmit() {

    },
    oversize() {
      Toast('文件大小不超过5MB')
    },
    afterRead(file) {
      file['status'] = 'uploading'
      file['message'] = '上传中...'
      upload(file.file).then(res => {
        if (res.success) {
          file['key'] = res.data
          file['status'] = ''
          file['message'] = null
        } else {
          Toast("上传失败")
          file['status'] = 'failed'
          file['message'] = '上传失败'
        }
      })
    },
    beforeRead(file) {
      if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
        Toast('请上传 jpg/png 格式图片');
        return false;
      }
      return true;
    },
    beforeDelete(file) {
      remove(file.key).then(res => {
        if (!res.success) {
          Toast("网络异常")
        }
      })
      return true;
    },
  }
}
</script>

<style scoped>

</style>