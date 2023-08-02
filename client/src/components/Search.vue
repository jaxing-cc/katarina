<template>
  <van-row type="center">
    <van-search
        v-model="searchKey"
        show-action
        :clearable="false"
        placeholder="请输入搜索关键词"
        @search="onSearch"
        @cancel="onCancel"/>
    <van-row v-if="showResult">
      <van-row type="flex" class="title">
        <van-col span="1"/>
        搜索结果
      </van-row>
      <van-row v-if="!result || result.length === 0" type="flex" class="title">
        <van-col span="1"/>
        暂无结果
      </van-row>
      <van-row v-if="result && result.length > 0" v-for="item in result" :key="item._id">
        <van-col span="1"/>
        <user-card :user="item" :show-username="true" @click="selectUser" class="resultItem"></user-card>
      </van-row>
    </van-row>
  </van-row>
</template>

<script>
import UserCard from "@/components/UserCard";
import {getByKey} from "@/api/auth";

export default {
  name: 'SearchUser',
  components: {UserCard},
  data() {
    return {
      searchKey: null,
      showResult: false,
      result: []
    };
  },

  mounted() {

  },

  methods: {
    onSearch() {
      this.showResult = true
      getByKey(this.searchKey).then(res => {
        if (res.success) {
          this.result = res.data
        }
      })
    },
    onCancel() {
      this.showResult = false
      this.searchKey = null
    },
    selectUser(user) {
      this.$emit("click", user)
    }
  },
};
</script>

<style lang="scss" scoped>
.title {
  font-size: 12px;
}
</style>