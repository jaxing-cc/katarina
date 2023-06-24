import axios from 'axios'
import {Notify, Toast} from 'vant'
import {getToken, removeToken, setToken} from './token'
import router from '@/router/index'
import url from '@/utils/common'

const tokenHeader = "Authorization";

const service = axios.create({
    baseURL: url.baseURL,
    timeout: 5000
});

service.interceptors.request.use(
    config => {
        Toast.loading({
            duration: 0, // 持续展示 toast
            forbidClick: true,
            message: '处理中...',
        });
        let token = getToken();
        if (token) {
            config.headers[tokenHeader] = "Bearer " + token;
        }
        return config;
    },
    error => {
        console.log(error) // for debug
        return Promise.reject(error)
    }
)

// response interceptor
service.interceptors.response.use(
    /**
     * If you want to get http information such as headers or status
     * Please return  response => response
     */
    response => {
        Toast.clear();
        let res = response.data;
        if (!res.success) {
            Toast(res.msg)
        }
        if (response.config.url === "auth/login" || response.config.url === "auth/register") {
            return res;
        }
        if (!getToken() || res.msg === "Unauthorized") {
            removeToken();
            router.push("/login").then(() => {
                Notify("登陆过期啦，请重新登陆吧");
            })
        }
        return res;
    },
    error => {
        Toast.clear();
        Notify({
            message: error.message,
            color: '#ad0000',
            background: '#ffe1e1',
            duration: 2000,
        })
        return Promise.reject(error)
    }
)

export default service
