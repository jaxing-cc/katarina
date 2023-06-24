<template>
    <div>
        <van-row>
            <van-nav-bar title="朋友"></van-nav-bar>
            <van-search v-model="searchKey" placeholder="请输入搜索关键词" />
        </van-row>

        <van-row>
            <van-cell-group title="我的分组">
                <van-cell v-if="friendGroup.length == 0" title="空"></van-cell>
                <van-cell v-for="item in friendGroup" :key="item.id" :title="item.groupName" is-link :url="'#/group/' + item.id" />
            </van-cell-group>
            <van-cell-group title="全部好友">
                <user-list group="0"></user-list>
            </van-cell-group>
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
            friendGroup:[]
        };
    },

    mounted() {
        
    },

    methods: {
        loadAllGroup(){
            loadGroup().then(res => {
                if (res.success){
                    this.friendGroup = res.data.group;
                }
            })
        }
    },
    created(){
        this.jwtObj = decodeToken();
        this.loadAllGroup()
    }
};
</script>

<style lang="scss" scoped>

</style>