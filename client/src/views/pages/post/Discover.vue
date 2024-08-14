<template>
  <div>
    <PostBox v-for="(post,i) in posts" :post="post" :key="i">

    </PostBox>
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
      posts: []
    }
  },
  methods: {
    loadPost() {
      search().then(res => {
        this.posts = res.data;
      })
    }
  },
  created() {
    this.loadPost()
  }
}
</script>

<style scoped>

</style>