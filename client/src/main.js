import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './assets/css/global.css'
import './plugins/vant'
import ws from '@/utils/ws'
import '@vant/touch-emulator';
import VEmojiV2 from 'v-emoji-v2'
import 'v-emoji-v2/src/index.css'
Vue.use(VEmojiV2)

Vue.prototype.$socket = ws.Instance;
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
