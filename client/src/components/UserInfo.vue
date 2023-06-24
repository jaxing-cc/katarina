<template>
    <van-row class="userCardWrapper">
        <van-row>
            <van-image style="margin-top: 20px" round width="60" height="60" src="https://img.yzcdn.cn/vant/cat.jpeg"/>
            <div class="username">{{currentUser.username}}</div>
        </van-row>
        <van-row>
            <van-grid :column-num="3">
                <van-grid-item icon="friends" text="2" />
                <van-grid-item icon="add" :text="'ID:' + currentUser.uid"/>
                <van-grid-item icon="underway" :text="currentTime" />
            </van-grid>
        </van-row>
    </van-row>
</template>

<script>
import {getByUid} from '@/api/auth'

export default {
    name: 'ViewProtalUserinfo',

    data() {
        return {
            currentUser:'',
            currentTime:''
        };
    },

    mounted() {
        
    },
    props:['uid'],
    methods: {
        loadUserInfo(){
            getByUid(this.uid).then(res => {
                if(res.success){
                    this.currentUser = res.data.user.userInfo
                }
            })
        },
        loadCurrentTime(){
            let date = new Date();
            this.currentTime = (date.getHours() < 10 ? "0" + date.getHours() :date.getHours()) + ":"
                + (date.getMinutes() < 10 ? "0" + date.getMinutes() :date.getMinutes()) + ":" 
                + (date.getSeconds() < 10 ? "0" + date.getSeconds() :date.getSeconds())
            let that = this;
            setInterval(function (){
                let date = new Date();
                that.currentTime = (date.getHours() < 10 ? "0" + date.getHours() :date.getHours()) + ":"
                    + (date.getMinutes() < 10 ? "0" + date.getMinutes() :date.getMinutes()) + ":" 
                    + (date.getSeconds() < 10 ? "0" + date.getSeconds() :date.getSeconds())
            },1000)
            
        }
    },
    created(){
        this.loadUserInfo();
        this.loadCurrentTime()
    }
};
</script>

<style lang="scss" scoped>
.username{
    color: rgb(175,184,188);
    font-size: 30px;
    font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
}
.userCardWrapper{
    margin: 20px;
    background: white;
    border-radius: 10px;
    height: 200px;
}
</style>