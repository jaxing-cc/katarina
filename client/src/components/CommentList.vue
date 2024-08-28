<template>
  <div>
    <div v-if="!commentList || commentList.length === 0">
      <van-divider dashed>暂无评论</van-divider>
    </div>

    <!--回复状态时显示-->
    <div v-if="headComment" class="van-hairline--surround">
      <UserCard v-if="headComment.user" :user="headComment.user" :img-size="25" :online="false"
                :uname-size="10" :showText="convertDate(headComment.createTime)" style="height: 45px"/>
      <van-row>
        <van-col span="4"/>
        <van-col offset="1" :span="17" class="contentWrapper">
          <div>{{ headComment.content }}</div>
          <van-grid direction="horizontal" :border="false" clickable icon-size="15" :column-num="3">
            <van-grid-item/>
            <van-grid-item text="回复" icon="comment-o"></van-grid-item>
            <van-grid-item :text="'12'" icon="star-o"/>
          </van-grid>
        </van-col>
        <van-col span="1"/>
      </van-row>
      <hr>
    </div>

    <!--评论主体-->
    <div v-for="(c, i) in commentList" :key="c.id" class="van-hairline--surround">
      <UserCard v-if="c.user" :user="c.user" :img-size="25" :online="false"
                :uname-size="10" :showText="convertDate(c.createTime)" style="height: 45px"/>
      <van-row>
        <van-col span="4"/>
        <van-col offset="1" :span="17" class="contentWrapper">
          <div>{{ c.content }}</div>
          <div v-if="c.childSize > 0">
            <van-button type="info" plain size="small" style="border: none" @click="loadChildComment(c)">查看更多{{ c.childSize }}条回复</van-button>
          </div>
          <van-grid direction="horizontal" :border="false" clickable icon-size="15" :column-num="3">
              <van-grid-item/>
              <van-grid-item text="回复" icon="comment-o"></van-grid-item>
              <van-grid-item :text="'12'" icon="star-o"/>
            </van-grid>
        </van-col>
        <van-col span="1"/>
      </van-row>
    </div>

    <van-popup
        v-model="popup" round
        :close-on-click-overlay="true"
        closeable
        close-icon="close"
        @close="closeChildComment"
        position="bottom" :style="{ height: '95%' }">
      <van-divider>回复</van-divider>
      <comment-list v-if="replyComment" :head-comment="replyComment" :id="id" :type="type" :replyId="replyComment._id"></comment-list>
    </van-popup>
  </div>
</template>

<script>
import {convertDate} from "@/utils/util";
import {queryComment} from "@/api/comment";
import UserCard from "@/components/UserCard";
import {Toast} from "vant";

export default {
  name: "CommentList",
  components: {UserCard},
  props: ["id", "type", "replyId", "headComment"],
  data() {
    return {
      popup: false,
      replyComment: null,
      commentList:[]
    }
  },
  methods: {
    convertDate(time) {
      return convertDate(time)
    },
    loadComment() {
      let replyId = this.replyId ? this.replyId : "base";
      queryComment(1, replyId, this.id, this.type).then(res => {
        if (res.success) {
          this.commentList = res.data
        }else{
          Toast('网络异常')
        }
      })
    },
    loadChildComment(comment){
      this.replyComment = comment
      this.popup = true;
    },
    closeChildComment(){
      this.replyComment = null
      this.popup = false;
    },
  },
  created() {
    this.loadComment()
  }
}
</script>

<style scoped>
.contentWrapper {
  font: 12px/1.5 Tahoma, Helvetica, Arial, '宋体', sans-serif;
}
</style>