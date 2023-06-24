import Vue from 'vue'
import VueRouter from 'vue-router'
import {getToken} from '@/utils/token'
import Home from '@/views/Home.vue'
import ErrorPage from '../views/ErrorPage.vue'
import Login from '../views/pages/other/Login.vue'
import Chat from '../views/pages/other/Chat.vue'
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
  },
  {
    path: '/chato/:uid',
    component: Chat
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

export default router
