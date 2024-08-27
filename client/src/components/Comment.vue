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
              <van-button size="small" color="#3E7FCC" icon="success"></van-button>
            </template>
          </van-field>
        </van-sticky>
      </van-col>
      <van-col :span="1"/>
    </van-row>

    <van-divider>
      评论
    </van-divider>

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

export default {
  name: "Comment",
  components: {CommentList},
  props: ["id", "type"],
  data() {
    return {
      commentList: [],
      comment: {
        content: ''
      }
    }
  },
  methods: {
  },
  created() {
  }
}
</script>

<style scoped>

</style>