<template>
  <div>
    {{ post }}
  </div>
</template>

<script>
import {findById} from "@/api/post";
import {Toast} from "vant";

export default {
  name: "PostDetail",
  data() {
    return {
      id: '',
      post: ''
    }
  },
  methods: {
    loadPostById() {
      findById(this.id).then(res => {
        if (res.success) {
          this.post = res.data
        } else {
          Toast('服务器异常')
        }
      })
    }
  },
  created() {
    this.id = this.$route.params.id;
    this.loadPostById()
  }

}
</script>

<style scoped>

</style>