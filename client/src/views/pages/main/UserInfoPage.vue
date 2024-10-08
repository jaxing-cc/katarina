<template>
  <div class="UserInfoPageWrapper">
    <van-nav-bar title="个人信息" :max-size="2 * 1024 * 1024" left-text="返回" @click-left="onClickLeft"></van-nav-bar>
    <van-cell-group>
      <van-cell>
        <template #title>
          <div v-if="!loginUser.avatar">暂无头像</div>
          <van-uploader v-model="avatar"
                        :after-read="afterRead"
                        :deletable="false"
                        :max-count="2"
                        :before-read="beforeRead"
          />
        </template>
      </van-cell>
      <van-cell title="用户名" :label="loginUser.name" is-link @click="openUpdateDialog('name')"/>
      <van-cell title="性别" :label="loginUser.gender === 1 ? '男' : '女'" is-link @click="openUpdateDialog('gender')"/>
      <van-cell title="邮箱" :label="loginUser.email  ? loginUser.email  : '暂无'" is-link @click="openUpdateDialog('email')"/>
      <van-cell title="修改密码" is-link/>
    </van-cell-group>


    <van-dialog v-model="name.open" title="修改信息" show-cancel-button :before-close="beforeCloseDialog">
      <van-row>
        <van-field v-model="name.value" label="昵称" placeholder="请输入用户名"/>
      </van-row>
    </van-dialog>

    <van-dialog v-model="gender.open" title="修改信息" show-cancel-button :before-close="beforeCloseDialog">
      <van-row justify="center" type="flex">
        <van-radio-group v-model="gender.value" direction="horizontal">
          <van-radio :name="0">女</van-radio>
          <van-radio :name="1">男</van-radio>
        </van-radio-group>
      </van-row>
    </van-dialog>

    <van-dialog v-model="email.open" title="修改信息" show-cancel-button :before-close="beforeCloseDialog">
      <van-row>
        <van-field v-model="email.value" label="邮箱" placeholder="请输入用户名"/>
      </van-row>
    </van-dialog>

  </div>
</template>

<script>
import {decodeToken} from "@/utils/token";
import {getByUid, updateSelf} from "@/api/auth";
import {Toast} from "vant";
import {getFileUrl, upload} from "@/api/file";

export default {
  name: "UserInfoPage",
  components: {},
  data() {
    return {
      avatar: [],
      loginUser: {},
      updateUserInfo: {
        name: null,
      },
      currentDialog: null,
      name: {
        open : false,
        value: null,
      },
      gender: {
        open : false,
        value: null,
      },
      email: {
        open : false,
        value: null,
      }
    }
  },
  methods: {
    onClickLeft() {
      this.$router.push("/")
    },
    afterRead(file) {
      if (this.avatar.length > 1) {
        this.avatar[0] = this.avatar[1]
        this.avatar.splice(1, 1)
      }
      let item = this.avatar[0];
      item['status'] = 'uploading'
      item['message'] = '上传中...'

      upload(file.file).then(res => {
        if (res.success) {
          updateSelf({avatar: res.data}).then(updateRes => {
            if (updateRes.success) {
              Toast("修改成功")
              item['status'] = ''
              item['message'] = null
            } else {
              Toast("修改失败")
              item['status'] = 'failed'
              item['message'] = '修改失败'
            }
          })
        } else {
          Toast("上传失败")
          item['status'] = 'failed'
          item['message'] = '上传失败'
        }
      })
    },
    beforeRead(file) {
      if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
        Toast('请上传 jpg/png 格式图片');
        return false;
      }
      return true;
    },
    loadUser() {
      const jwtObj = decodeToken();
      getByUid(jwtObj.uid).then(res => {
        if (res.success) {
          if (!res.data) {
            Toast("用户不存在")
          } else {
            this.loginUser = res.data;
            if (this.loginUser.avatar) {
              this.avatar[0] = {
                url: getFileUrl(this.loginUser.avatar),
                isImage: true
              }
            }
          }
        }
      })
    },
    openUpdateDialog(name){
      this[name].open = true;
      this[name].value = this.loginUser[name]
      this.currentDialog = name;
    },
    beforeCloseDialog(action, done){
      if (action === 'confirm'){
        let obj = {}
        obj[this.currentDialog] = this[this.currentDialog].value;
        updateSelf(obj).then(updateRes => {
          if (updateRes.success) {
            Toast("修改成功")
            this.loadUser()
            done()
          } else {
            Toast("修改失败,请输入有效的数据")
            done()
          }
        })
      }else{
        done()
      }
    },
  }
  ,
  created() {
    this.loadUser()
  }
}
</script>

<style scoped>
.UserInfoPageWrapper {
  text-align: left;
}
</style>