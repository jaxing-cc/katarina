import URL from '@/utils/common'
import {getToken} from "@/utils/token";
import {Toast} from 'vant'

export default class SocketService {

    socket = null;

    instance = null;

    setIntervalRes = null;

    connected = false;

    static get Instance() {
        if (!this.instance) {
            this.instance = new SocketService();
        }
        return this.instance;
    }

    connect() {
        if (!this.connected) {
            console.log('建立websocket连接')
            this.socket = new WebSocket(URL.wsURL + "?Authorization=" + getToken())
            this.socket.onopen = this.onopen
            this.socket.onmessage = this.onmessage
            this.socket.onerror = this.onerror
            this.socket.onclose = this.onclose
        } else {
            console.log('websocket已连接')
        }
    }

    /**发送心跳
     * @param {number} time 心跳间隔毫秒 默认10000
     * @param {string} ping 心跳名称 默认字符串ping
     */
    sendPing(time = 3000, ping = JSON.stringify({
        type: 10000,
        data: 'ping',
        createTime: new Date().getTime()
    })) {
        clearInterval(this.setIntervalRes)
        this.setIntervalRes = setInterval(() => {
            this.socket.send(ping)
        }, time)
    }

    onopen() {
        let service = SocketService.Instance;
        service.connected = true;
        service.sendPing()
    }

    onmessage(e) {
        window.dispatchEvent(new CustomEvent('onmessage', {
            detail: JSON.parse(e.data)
        }))
    }

    onerror() {
        let service = SocketService.Instance;
        if (service.socket){
            service.socket.close()
        }
        service.connected = false;
        clearInterval(service.setIntervalRes)
        console.log('连接失败')
    }

    onclose() {
        let service = SocketService.Instance;
        service.connected = false;
        clearInterval(service.setIntervalRes)
        console.log('websocket已断开，正在尝试重连')
        setTimeout(() => {
            service.connect();
        }, 1000);
    }

    send(message) {
        if (this.socket.readyState === 1) {
            this.socket.send(JSON.stringify(message))
        } else if (this.socket.readyState === 0) {
            this.connecting(message)
        } else {
            Toast('网络异常，请重试');
        }
    }

    connecting(message) {
        setTimeout(() => {
            if (this.socket.readyState === 0) {
                this.connecting(message)
            } else {
                this.socket.send(JSON.stringify(message))
            }
        }, 1000)
    }
}