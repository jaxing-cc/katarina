<template>
  <van-row class="wrapper">
    <comment-list :id="id" :type="type" />
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
        replyId: 'base',
        replyObj: null
      }
    }
  },
  methods: {
    /**
     * 输入框失去焦点去掉回复内容
     */
    onBlur(){
      this.comment.replyObj = null
      this.comment.replyId = 'base'
    }
  },
  created() {
  }
}
</script>

<style scoped>
</style>