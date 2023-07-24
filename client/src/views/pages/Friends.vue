<template>
    <div>
        <van-row>
            <van-nav-bar title="朋友"></van-nav-bar>
            <van-search v-model="searchKey" placeholder="请输入搜索关键词" />
        </van-row>

    </div>
</template>

<script>

import {decodeToken} from '@/utils/token'
import UserList from '../../components/UserList.vue';
import{loadGroup} from '@/api/friend'

export default {
  components: { UserList },
    name: 'Friends',

    data() {
        return {
            searchKey:'',
            jwtObj:{},
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

</style>