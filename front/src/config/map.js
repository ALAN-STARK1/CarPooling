// 百度地图配置
export const BAIDU_MAP_CONFIG = {
  // Web 端 JS API AK（浏览器端）
  JS_API_AK: 'lDYIR1arsYS3qxSqMAYFFRWUxWDIxjhq',
  JS_API_URL: 'https://api.map.baidu.com/api?v=3.0',
  COORD_TYPE: 'bd09ll',
  DEFAULT_CITY: '上海',
}

/**
 * 格式化驾车距离
 * @param {number} meters 米
 */
export function formatDistance(meters) {
  if (meters >= 1000) {
    return `${(meters / 1000).toFixed(1)} 公里`
  }
  return `${meters} 米`
}

/**
 * 格式化驾车时间
 * @param {number} seconds 秒
 */
export function formatDuration(seconds) {
  const minutes = Math.round(seconds / 60)
  if (minutes >= 60) {
    const h = Math.floor(minutes / 60)
    const m = minutes % 60
    return `${h} 小时 ${m} 分钟`
  }
  return `${minutes} 分钟`
}
