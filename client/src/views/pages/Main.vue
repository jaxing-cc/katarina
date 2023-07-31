<template>
    <van-row class="wrapper">
        <van-nav-bar title="我"></van-nav-bar>

    </van-row>
</template>

<script>
import UserInfo from '../../components/UserInfo.vue';
import {decodeToken} from '@/utils/token'
export default {
  components: { UserInfo },
    name: 'Main',

    data() {
        return {
            jwtObj: {}
        };
    },

    mounted() {
        
    },

    methods: {
        
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
}
</style>