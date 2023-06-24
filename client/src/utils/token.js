import store from '@/store/index'
import jwtDecode from "jwt-decode";
export function getToken() {
    let token = store.state.token;
    if (!token){
        token = localStorage.getItem("token");
        store.commit('setToken',token);
    }
    return token;
}

export function decodeToken() {
    let jwtObj = store.state.jwtObj;
    if (!jwtObj){
        let token = getToken();
        jwtObj = token ? jwtDecode(token): null;
        store.commit('setUser',jwtObj);
    }
    return jwtObj;
}

export function setToken(token) {
    store.commit('setToken',token);
    localStorage.setItem("token",token);
}

export function removeToken() {
    localStorage.clear();
    sessionStorage.clear();
    store.commit('resetToken');
}
