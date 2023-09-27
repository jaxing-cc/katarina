import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        jwtObj: null,
        token: null,
        followList:[],
    },
    mutations: {
        setFollowList(state, list) {
            state.followList = list;
        },
        setUser(state, user) {
            state.jwtObj = user;
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
