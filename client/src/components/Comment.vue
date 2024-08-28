<template>
  <van-row class="wrapper">


    <van-row>
      <van-col :span="2"/>
      <van-col :span="21">
        <van-sticky>
          <van-field
              v-model="comment.content"
              name="content"
              rows="2"
              :autosize="true"
              type="textarea"
              maxlength="300"
              placeholder="评论两句吧"
              show-word-limit>
            <template #button>
              <van-button size="small" color="#3E7FCC" @click="saveComment" plain>评论</van-button>
            </template>
          </van-field>
        </van-sticky>
      </van-col>
      <van-col :span="1"/>
    </van-row>

    <van-row>
      <van-col :span="1"/>
      <van-col :span="22">
        <comment-list :id="id" :type="type"/>
      </van-col>
      <van-col :span="1"/>
    </van-row>
  </van-row>
</template>

<script>
import CommentList from "@/components/CommentList";
import {saveComment} from "@/api/comment";
import {Dialog, Toast} from "vant";

export default {
  name: "Comment",
  components: {CommentList},
  props: ["id", "type"],
  data() {
    return {
      commentList: [],
      comment: {
        content: '',
        replyId: 'base'
      }
    }
  },
  methods: {
    saveComment() {
      Dialog.confirm({
        message: '确认发布评论吗?',
      }).then(() => {
        if (!this.comment.content) {
          Toast('评论内容不能为空')
          return
        }
        saveComment({
          replyId: this.comment.replyId,
          content: this.comment.content,
          targetId: this.id,
          type: this.type,
        }).then(res => {
          if (res.success) {
            Toast('保存成功')
          } else {
            Toast('保存失败')
          }
          this.comment.content = '';
        })
      }).catch(() => {

      });


    }
  },
  created() {
  }
}
</script>

<style scoped>

</style>