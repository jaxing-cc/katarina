<template>
    <div class="wrapper">
      <van-cell-group title="账号设置" inset>
        <van-cell title="个人信息" is-link to="info"/>
        <van-cell title="我关注的" is-link/>
        <van-cell title="关注我的" is-link/>
        <van-cell title="退出账号" is-link @click="exit"/>
      </van-cell-group>
      <van-cell-group title="其他" inset>
        <van-cell title="斗地主" label="开发中..." is-link to="ddz"/>
      </van-cell-group>
    </div>
</template>

<script>
import {decodeToken, removeToken} from '@/utils/token'
export default {
  components: { },
    name: 'Main',

    data() {
        return {
            jwtObj: {}
        };
    },

    mounted() {
        
    },

    methods: {
        exit(){
          removeToken()
          this.$router.push('/login')
        }
    },

    created(){
        this.jwtObj = decodeToken();
    }
};
</script>

<style lang="scss" scoped>
.wrapper{
    background: rgb(248, 248, 248);
    height: 100%;
    text-align: left;
}
</style>