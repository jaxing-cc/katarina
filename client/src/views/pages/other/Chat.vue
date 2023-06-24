<template>
    <div id="chatWrapper">
        <van-nav-bar
            v-if="targetUserInfo.userInfo != null"
            :title="targetUserInfo.userInfo.username + '  [' + (targetUserInfo.online?'在线':'离线') + ']'"
            left-text="返回"
            left-arrow
            @click-left="onClickLeft"/>
        <van-nav-bar
            v-if="targetUserInfo.userInfo == null"
            title="聊天"
            left-text="返回"
            left-arrow
            @click-left="onClickLeft"/>
        <van-row>
            {{ targetUserInfo }}
        </van-row>
        <div class="chatInput">
            <van-cell-group inset>
                <van-field
                    v-model="message"
                    rows="2"
                    autosize
                    type="textarea"
                    maxlength="80"
                    placeholder="请输入留言"
                    show-word-limit>
                    <template #button>
                        <van-button size="small" @click="sendMsg" type="primary">发送</van-button>
                    </template>
                </van-field>
            </van-cell-group>
        </div>
        
    </div>
</template>



<script>
import { getByUid } from '@/api/auth'
import { sendWSPush } from '@/utils/ws'
import { decodeToken,getToken } from '@/utils/token'

export default {
    name: 'ViewProtalChat',

    data() {
        return {
            uid:'',
            message:'',
            chatRecord:[],
            jwtObj:{},
            targetUserInfo:{}
        };
    },

    methods: {
        onClickLeft(){
            history.back()
        },
        sendMsg(){
            let that = this;
            console.log(this.message)
            if(that.message){
                let content = JSON.stringify({
                    fromId:that.jwtObj.jti,
                    toId: that.uid,
                    msg: that.message
                })
                sendWSPush({
                    type:2,
                    token: getToken(),
                    content: content
                })
                that.message = ""
            }
        },
        eventMsg(e){
            //接收到消息
            let date = e.detail.data
        },
        getTargetUserInfo(){
            getByUid(this.uid).then(res => {
                if(res.success){
                    this.targetUserInfo = res.data.user
                }
            })
        }
    },
    
    mounted(){
    },
    created(){
        this.jwtObj = decodeToken();
        this.uid = this.$route.params.uid
        this.getTargetUserInfo();
        window.addEventListener('onmessageWS', this.eventMsg)
    },

    destroyed(){
        window.removeEventListener('onmessageWS', this.eventMsg)
    },
};
</script>

<style lang="scss" scoped>
#chatWrapper{
    background: rgb(248, 248, 248);
    height: 100%;
}
.chatInput{
    width: 100%;
    position: absolute;
    bottom: 10px;
}
</style>