export function convertDate(time) {
    const timeDifference = new Date().getTime() - time;
    const hoursDifference = Math.floor(timeDifference / (1000 * 60 * 60));
    if (hoursDifference < 24) {
        if (hoursDifference === 0) {
            return "刚刚"
        }
        return hoursDifference + "小时前"
    } else if (hoursDifference < 240) {
        return Math.floor(hoursDifference / 24) + "天前"
    }
    return new Date(time).toLocaleString()
}