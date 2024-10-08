import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        jwtObj: null,
        token: null,
        followList: new Set(),
        userInfo: null
    },
    mutations: {
        setFollowList(state, list) {
            state.followList = new Set(list);
        },
        setUser(state, user) {
            state.jwtObj = user;
        },
        setUserInfo(state, userInfo) {
            state.userInfo = userInfo;
        },
        setToken(state, token) {
            state.token = token;
        },
        resetToken(state) {
            state.token = null
            state.jwtObj = null
        }
    },
    actions: {},
    modules: {}
})
