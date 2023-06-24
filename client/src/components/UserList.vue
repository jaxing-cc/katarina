<template>
    <van-row>
        <van-list
            :loading="loading"
            :finished="finished"
            @load="loadMore"
            finished-text="没有更多了">
            <van-cell center v-for="item in friends" :key="item.friendInfo.uid" is-link value="" :label="'备注:' + item.friendInfo.remark" :url="'#/chato/' + item.friendInfo.friendUid">
                <template #title>
                    <van-image class="userImg" width="40" height="40" round position="left" :src="item.friend.userInfo.img"/>
                    <span class="usernameFont">{{item.friend.userInfo.username}}</span>
                    <van-tag :type="item.friend.online? 'success':'warning'" class="userTag">{{item.friend.online?"on" : "off"}}</van-tag>
                </template>
            </van-cell>
        </van-list>
    </van-row>
</template>

<script>
import{loadFriends} from '@/api/friend'

export default {
    name: 'ViewProtalUserlist',

    data() {
        return {
            friendsParams:{
                gid: null,
                page: 1
            },
            finished:false,
            loading:false,
            friends:[
            ]
        };
    },

    mounted() {

    },
    created(){
    },
    props:['group'],
    methods: {
        loadMore(){
            this.loading = true;
            loadFriends(this.friendsParams).then(res => {
                if (res.data.total === 0){
                    this.finished = true;
                }else{
                    let resArray = res.data.result;
                    if(resArray){
                        for (let i = 0; i < resArray.length; i++) {
                            this.friends.push(resArray[i])
                        }
                        this.friendsParams.page++;
                    }
                }
                this.loading = false;
            })
        }
    },
};
</script>

<style lang="scss" scoped>
.usernameFont{
    margin-left: 13px;
    font-weight: bolder;
    vertical-align: middle;
}
.userImg{
    vertical-align: middle;
}
.userTag{
    margin-left: 5px;
}
</style>
