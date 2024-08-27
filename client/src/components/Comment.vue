<template>
  <van-row class="wrapper">
    <van-row>
      <van-col :span="1"/>
      <van-col :span="22">
        <div v-for="(c, i) in commentList" :key="c.id">
          <UserCard v-if="c.user" :user="c.user"
                    :img-size="25" :online="false"
                    :uname-size="10" :showText="convertDate(c.createTime)" style="height: 45px"/>
          <van-row>
            <van-col span="4"/>
            <van-col offset="1" :span="17" class="contentWrapper">
<!--              {{ c.content }}-->
              啊啊啊啊啊啊啊啊啊啊啊啊，啊啊啊啊啊啊啊啊啊啊啊啊，啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊，
              啊啊啊啊啊啊啊啊啊啊啊啊，啊啊啊啊啊啊啊啊啊啊啊啊，啊啊啊啊啊啊啊啊啊啊啊啊，

            </van-col>
            <van-col  span="1"/>

          </van-row>
        </div>
      </van-col>
      <van-col :span="1"/>
    </van-row>
  </van-row>
</template>

<script>
import {queryComment} from "@/api/comment";
import {convertDate} from "@/utils/util";
import UserCard from "@/components/UserCard";

export default {
  name: "Comment",
  components: {UserCard},
  props: ["id", "type"],
  data() {
    return {
      commentList: [{
        "replyId": "base",
        "content": "A",
        "targetId": null,
        "targetType": null,
        "uid": "64d204c182879a330e56f6f1",
        "deleted": false,
        "createTime": 1724737727670,
        "user": {
          "name": "不爱吃早饭",
          "username": "cjxingxcr",
          "password": null,
          "avatar": "66138b4ccb66d83ebfab8e67",
          "email": null,
          "gender": 1,
          "roles": null,
          "online": false,
          "disable": false,
          "_id": null
        },
        "_id": "66cd68bfef28973064f8301a"
      }, {
        "replyId": "base",
        "content": "B",
        "targetId": null,
        "targetType": null,
        "uid": "64d204c182879a330e56f6f1",
        "deleted": false,
        "createTime": 1724737727670,
        "user": {
          "name": "不爱吃早饭",
          "username": "cjxingxcr",
          "password": null,
          "avatar": "66138b4ccb66d83ebfab8e67",
          "email": null,
          "gender": 1,
          "roles": null,
          "online": false,
          "disable": false,
          "_id": null
        },
        "_id": "66cd69bc851900004b0056cb"
      }, {
        "replyId": "base",
        "content": "C",
        "targetId": null,
        "targetType": null,
        "uid": "64d204c182879a330e56f6f1",
        "deleted": false,
        "createTime": 1724737727670,
        "user": {
          "name": "不爱吃早饭",
          "username": "cjxingxcr",
          "password": null,
          "avatar": "66138b4ccb66d83ebfab8e67",
          "email": null,
          "gender": 1,
          "roles": null,
          "online": false,
          "disable": false,
          "_id": null
        },
        "_id": "66cd69c2851900004b0056cc"
      }, {
        "replyId": "base",
        "content": "D",
        "targetId": null,
        "targetType": null,
        "uid": "64d204c182879a330e56f6f1",
        "deleted": false,
        "createTime": 1724737727670,
        "user": {
          "name": "不爱吃早饭",
          "username": "cjxingxcr",
          "password": null,
          "avatar": "66138b4ccb66d83ebfab8e67",
          "email": null,
          "gender": 1,
          "roles": null,
          "online": false,
          "disable": false,
          "_id": null
        },
        "_id": "66cd69c8851900004b0056cd"
      }, {
        "replyId": "base",
        "content": "E",
        "targetId": null,
        "targetType": null,
        "uid": "64d204c182879a330e56f6f1",
        "deleted": false,
        "createTime": 1724738822161,
        "user": {
          "name": "不爱吃早饭",
          "username": "cjxingxcr",
          "password": null,
          "avatar": "66138b4ccb66d83ebfab8e67",
          "email": null,
          "gender": 1,
          "roles": null,
          "online": false,
          "disable": false,
          "_id": null
        },
        "_id": "66cd6d06e3609c67c6377d79"
      }]
    }
  },
  methods: {
    loadComment() {
      queryComment(1, "", this.id, this.type).then(res => {
        if (res.success) {
          this.commentList = res.data
        }
      })
    },
    convertDate(time) {
      return convertDate(time)
    },
  },
  created() {
    // this.loadComment()
  }
}
</script>

<style scoped>
.contentWrapper {
  font: 12px/1.5 Tahoma, Helvetica, Arial, '宋体', sans-serif;
}
</style>