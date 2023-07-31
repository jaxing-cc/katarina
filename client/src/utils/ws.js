import URL from '@/utils/common'
import {getToken} from "@/utils/token";
import {Toast} from 'vant'

let Socket = ''
let setIntervalWesocketPush = null
/**
 * 建立websocket连接
 */
export const createSocket = () => {
    if (Socket && Socket.readyState !== 1) {
        Socket.close()
    }
    if (!Socket) {
        console.log('建立websocket连接')
        const token = getToken();
        Socket = new WebSocket(URL.wsURL + "?Authorization=" + token)
        Socket.onopen = onopen
        Socket.onmessage = onmessage
        Socket.onerror = onerror
        Socket.onclose = onclose
    } else {
        console.log('websocket已连接')
    }
}

/**打开WS之后发送心跳 */
const onopen = () => {
    sendPing()
}

/**连接失败重连 */
const onerror = () => {
    Socket.close()
    clearInterval(setIntervalWesocketPush)
    console.log('连接失败重连中')
    if (Socket.readyState !== 3) {
        Socket = null
        createSocket()
    }
}

/**WS数据接收统一处理 */
const onmessage = e => {
    window.dispatchEvent(new CustomEvent('onmessage', {
        detail: {
            data: e.data
        }
    }))
}

/**
 * 发送数据但连接未建立时进行处理等待重发
 * @param {any} message 需要发送的数据
 */
const connecting = message => {
    setTimeout(() => {
        if (Socket.readyState === 0) {
            connecting(message)
        } else {
            Socket.send(JSON.stringify(message))
        }
    }, 1000)
}

/**
 * 发送数据
 * @param {any} message 需要发送的数据
 */
export const send = message => {
    if (Socket.readyState === 1) {
        Socket.send(JSON.stringify(message))
    } else if (Socket.readyState === 0) {
        connecting(message)
    } else {
        Toast('网络异常，请重试');
    }
}

/**断开重连 */
const onclose = () => {
    clearInterval(setIntervalWesocketPush)
    console.log('websocket已断开....正在尝试重连')
    if (Socket.readyState !== 2) {
        Socket = null
        createSocket()
    }
}
/**发送心跳
 * @param {number} time 心跳间隔毫秒 默认5000
 * @param {string} ping 心跳名称 默认字符串ping
 */
export const sendPing = (time = 5000, ping = JSON.stringify({
    type: 10000,
    data: 'ping',
    createTime: new Date().getTime()
})) => {
    clearInterval(setIntervalWesocketPush)
    Socket.send(ping)
    setIntervalWesocketPush = setInterval(() => {
        Socket.send(ping)
    }, time)
}