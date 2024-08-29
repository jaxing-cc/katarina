<template>
  <div>
    <van-row>
      <van-col :span="1"/>
      <van-col :span="22">
        <!--回复状态时显示-->
        <div v-if="headComment" class="van-hairline--right van-hairline--bottom">
          <UserCard v-if="headComment.user" :user="headComment.user" :img-size="25" :online="false"
                    :uname-size="10" :showText="convertDate(headComment.createTime)" style="height: 45px"/>
          <van-row>
            <van-col span="4"/>
            <van-col offset="1" :span="17" class="contentWrapper">
              <div>{{ headComment.content }}</div>
              <van-grid direction="horizontal" :border="false" clickable icon-size="15" :column-num="3">
                <van-grid-item/>
                <van-grid-item icon="comment-o" @click="reply(headComment)"></van-grid-item>
                <van-grid-item :text="'12'" icon="star-o"/>
              </van-grid>
            </van-col>
            <van-col span="1"/>
          </van-row>
          <hr>
        </div>

        <!--评论主体-->
        <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多评论了"
            @load="onLoad">
          <div v-for="(c, i) in commentList" :key="c.id" class="van-hairline--right van-hairline--bottom">
            <UserCard v-if="c.user" :user="c.user" :img-size="25" :online="false" :hover="false"
                      :uname-size="10" :showText="convertDate(c.createTime)" style="height: 45px"/>
            <van-row>
              <van-col span="4"/>
              <van-col offset="1" :span="17" class="contentWrapper">
                <div>{{ c.content }}</div>
                <div v-if="c.childSize > 0">
                  <van-button type="info" plain size="small" style="border: none" @click="loadChildComment(c)">
                    查看更多{{ c.childSize }}条回复
                  </van-button>
                </div>
                <van-grid direction="horizontal" :border="false" clickable icon-size="15" :column-num="3">
                  <van-grid-item/>
                  <van-grid-item icon="comment-o" @click="reply(c)"></van-grid-item>
                  <van-grid-item :text="'12'" icon="star-o"/>
                </van-grid>
              </van-col>
              <van-col span="1"/>
            </van-row>
          </div>

        </van-list>
      </van-col>
      <van-col :span="1"/>
    </van-row>


    <van-row class="bottom-input">
      <div v-if="comment.replyObj" class="replyText">
        <span style="margin-left: 5%" class="van-ellipsis">
          {{ "回复'" + comment.replyObj.user.name + "':" + comment.replyObj.content }}
        </span>
        <van-icon style="margin-right: 5%" name="cross" @click="cancelReply"/>
      </div>
      <van-field
          v-model="comment.content"
          name="content"
          :autosize="{ maxHeight: 100, minHeight: 20 }"
          placeholder="评论两句吧"
          ref="commentInput"
          type="textarea">
        <template #button>
          <van-button size="small" color="#3E7FCC" @click="saveComment" plain>评论</van-button>
        </template>
      </van-field>
    </van-row>

    <!--  子评论页面  -->
    <van-popup
        v-model="popup" round
        :close-on-click-overlay="true"
        closeable
        close-icon="close"
        @close="closeChildComment"
        position="bottom" :style="{ height: '95%' }">
      <comment-list v-if="replyComment" :head-comment="replyComment" :id="id" :type="type"
                    :replyId="replyComment._id"></comment-list>
    </van-popup>
  </div>
</template>

<script>
import {convertDate} from "@/utils/util";
import {queryComment, saveComment} from "@/api/comment";
import UserCard from "@/components/UserCard";
import {Dialog, Toast} from "vant";

export default {
  name: "CommentList",
  components: {UserCard},
  props: ["id", "type", "replyId", "headComment"],
  data() {
    return {
      popup: false,
      replyComment: null,
      loading: false,
      finished: false,
      page: 1,
      commentList: [],
      comment: {
        content: '',
        replyId: 'base',
        replyObj: null
      }
    }
  },
  methods: {
    convertDate(time) {
      return convertDate(time)
    },
    loadCommentFirstPage() {
      this.page = 1;
      this.finished = false;
      this.commentList = [];
      this.loadComment();
    },
    loadComment() {
      this.loading = true
      let replyId = this.replyId ? this.replyId : "base";
      queryComment(this.page, replyId, this.id, this.type).then(res => {
        this.loading = false
        if (res.success) {
          let data = res.data;
          if (!data || data.length === 0) {
            this.finished = true;
          } else {
            this.page++
            for (let i = 0; i < data.length; i++) {
              this.commentList.push(data[i])
            }
          }
        } else {
          Toast('网络异常')
        }
      })
    },
    loadChildComment(comment) {
      this.replyComment = comment
      this.popup = true;
    },
    closeChildComment() {
      this.replyComment = null
      this.popup = false;
    },
    onLoad() {
      this.loadComment();
    },
    /**
     * 回复评论
     */
    reply(comment) {
      this.comment.replyObj = comment
      this.comment.replyId = comment._id
      this.$refs.commentInput.focus()
    },
    /**
     * 取消回复
     */
    cancelReply() {
      this.comment.replyObj = null
      this.comment.replyId = 'base'
    },
    saveComment() {
      let replyId = this.comment.replyId;
      console.log(replyId)
      saveComment({
        replyId: replyId,
        content: this.comment.content,
        targetId: this.id,
        type: this.type,
      }).then(res => {
        if (res.success) {
          this.loadCommentFirstPage()
          Toast('评论成功')
        } else {
          Toast('评论失败')
        }
        this.cancelReply()
        this.comment.content = '';
      })

    },

  },
  created() {
    this.page = 1
    this.loadComment()
  }
}
</script>

<style scoped>
.contentWrapper {
  font: 12px/1.5 Tahoma, Helvetica, Arial, '宋体', sans-serif;
  word-wrap: break-word;
}

.bottom-input {
  position: fixed;
  bottom: 0;
  width: 100%;
  color: darkgrey;
}

.replyText {
  display: flex;
  align-items: center;    /* 垂直居中 */
  justify-content: space-between;
  font-size: 12px;
  background-color: #f7f8f6;
  height: 24px;
}
</style>