<template>
  <div>
    <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad">
      <PostBox v-for="(post,i) in posts" :preview="true" :post="post" :key="i"/>
    </van-list>
  </div>
</template>

<script>
import {search} from "@/api/post";
import PostBox from "@/components/PostBox";

export default {
  name: "Discover",
  components: {PostBox},
  data() {
    return {
      posts: [],
      page: 1,
      loading: false,
      finished: false
    }
  },
  methods: {
    loadPost() {
      this.loading = true;
      search(null, false, this.page).then(res => {
        if (!res.data || res.data.length === 0) {
          this.finished = true
          return;
        }
        for (let i = 0; i < res.data.length; i++) {
          this.posts.push(res.data[i])
        }
        this.loading = false;
      })
    },
    onLoad() {
      this.loadPost();
      this.page++;
    },
  },
  created() {
  }
}
</script>

<style scoped>

</style>