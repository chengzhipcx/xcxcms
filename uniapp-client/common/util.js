/**
 * 通用工具
 */

/**
 * 按字符（Unicode 码点）截断，避免把 emoji 等
 * 增补平面字符（代理对）从中间切开产生乱码。
 */
export function truncate(content, maxChars) {
  if (content == null) return ''
  if (content.codePointCount(0, content.length) <= maxChars) {
    return content
  }
  return content.substring(0, content.offsetByCodePoints(0, maxChars))
}

/**
 * 时间戳（毫秒）-> yyyy-MM-dd HH:mm
 */
export function formatDateTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  const p = n => (n < 10 ? '0' + n : '' + n)
  return (
    d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) +
    ' ' + p(d.getHours()) + ':' + p(d.getMinutes())
  )
}

/**
 * 时间戳（毫秒）-> yyyy-MM-dd
 */
export function formatDate(ts) {
  if (!ts) return ''
  return formatDateTime(ts).split(' ')[0]
}
