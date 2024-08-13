import Vue from 'vue'
import VueRouter from 'vue-router'
import {getToken} from '@/utils/token'
import Home from '@/views/Home.vue'
import ErrorPage from '../views/ErrorPage.vue'
import Login from '../views/pages/other/Login.vue'
import UserInfoPage from "@/views/pages/main/UserInfoPage";
import Ddz from "@/views/pages/main/Ddz";
import DdzGamePage from "@/views/pages/main/DdzGamePage";
import Register from "@/views/pages/other/Register";
import Discover from "@/views/pages/post/Discover";
import Follow from "@/views/pages/post/Follow";
import ChatList from "@/views/pages/ChatList";
import Post from "@/views/pages/Post";
import Main from "@/views/pages/Main";
import WritePost from "@/views/pages/post/WritePost";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        redirect: '/',
        children: [
            {
                path: '/chat-list',
                name: 'ChatList',
                component: ChatList,
            },
            {
                path: '/',
                name: 'post',
                component: Post,
                redirect: '/discover',
                children: [
                    {
                        path: '/discover',
                        name: 'Discover',
                        component: Discover,
                    },
                    {
                        path: '/follow',
                        name: 'Follow',
                        component: Follow,
                    }
                ]
            },
            {
                path: '/main',
                name: 'main',
                component: Main,
            },
        ]
    },
    {
        path: '/write',
        name: 'WritePost',
        component: WritePost,
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/error/page',
        component: ErrorPage
    }
    ,
    {
        path: '/info',
        component: UserInfoPage
    }
    ,
    {
        path: '/ddz',
        component: Ddz
    }
    ,
    {
        path: '/ddz/room',
        component: DdzGamePage
    }
]

const router = new VueRouter({
    routes
})

router.beforeEach((to, from, next) => {
    let token;
    if (to.path === '/login' || to.path === '/register' || ((token = getToken()) && token.length > 10)) {
        return next();
    }
    return next('/login');
});

const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(to) {
    return VueRouterPush.call(this, to).catch(err => err)
}

export default router
