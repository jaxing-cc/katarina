import Vue from 'vue'
import VueRouter from 'vue-router'
import {getToken} from '@/utils/token'
import Home from '@/views/Home.vue'
import ErrorPage from '../views/ErrorPage.vue'
import Login from '../views/pages/other/Login.vue'
import UserInfoPage from "@/views/pages/main/UserInfoPage";
import Ddz from "@/views/pages/main/Ddz";
import DdzGamePage from "@/views/pages/main/DdzGamePage";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
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

router.beforeEach( (to,from,next) =>{
  let token;
  if (to.path === '/login' || ((token = getToken()) && token.length > 10)){
    return next();
  }
  return next('/login');
});

const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(to) {
  return VueRouterPush.call(this, to).catch(err => err)
}

export default router
