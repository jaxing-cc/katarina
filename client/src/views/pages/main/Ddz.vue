<template>
  <div class="ddzListWrapper">
    <van-dialog v-model="create.switch" title="创建房间" show-cancel-button :before-close="beforeCloseDialog">
      <van-row>
        <van-field v-model="create.name" label="房间名" placeholder="请输入房间名"/>
      </van-row>
    </van-dialog>
    <van-nav-bar title="房间列表" :max-size="2 * 1024 * 1024" left-text="返回" @click-left="onClickLeft"></van-nav-bar>
    <van-row>
      <van-col span="20">
        <van-search
            v-model="searchName"
            show-action
            :clearable="false"
            placeholder="请输入搜索关键词"
            @search="onSearch"
            @cancel="onCancel"
            @focus="onfocus"/>
      </van-col>
      <van-col span="4">
        <van-button type="default" style="margin-top: 3px" icon="plus" @click="createDialog"/>
      </van-col>
    </van-row>
    <van-cell-group title="房间列表" inset>
      <van-cell v-for="item in list"
                :key="item.id"
                :title="item.name"
                :label="'ID:' + item.id + '  人数:' + item.playerSize"
                :value="item.status" is-link @click="join(item.id)"/>
    </van-cell-group>
  </div>
</template>

<script>
import {createRoom, joinRoom, roomList} from "@/api/ddz";
import {Toast} from "vant";

export default {
  name: "Ddz",
  data() {
    return {
      searchName: null,
      list: [],
      create: {
        switch: false,
        name: null
      }
    }
  },
  methods: {
    onClickLeft() {
      this.$router.push("/")
    },
    loadRoomList() {
      roomList(this.searchName).then(res => {
        if (res.success) {
          this.list = res.data;
        }
      })
    },
    beforeCloseDialog(action, done) {
      if (action === 'cancel') {
        this.create.name = null
        this.create.switch = false
        done()
      } else {
        if (!this.create.name) {
          Toast('房间名为空')
          done()
          return;
        }
        createRoom(this.create.name).then(res => {
          if (res.success) {
            done()
            this.loadRoomList()
          } else {
            Toast(res.msg)
            done()
          }
        })
      }
    },
    /**
     * 打开创建房间对话框
     */
    createDialog() {
      this.create.switch = true
    },
    join(id) {
      joinRoom(id).then(res => {
        if (!res.success){
          Toast(res.msg)
        }
      })
    },
    onSearch() {
      this.loadRoomList();
    },
    onCancel() {
      this.searchName = null;
      this.loadRoomList();
    },
    onfocus() {
    },
    chatMsgHandler(data){
      this.$router.push("/ddz/room")
    }
  },
  created() {
    window.addEventListener("msg@1002", this.chatMsgHandler)
    this.$socket.connect()
    this.loadRoomList()
  },
  destroyed() {
    window.removeEventListener("msg@1002", this.chatMsgHandler)
  }

}
</script>

<style scoped>
.ddzListWrapper {

}
</style>