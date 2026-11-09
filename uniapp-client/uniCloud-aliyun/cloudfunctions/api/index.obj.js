/**
 * 公开数据接口云对象（对应经典版 ApiController 的四个接口）
 *
 * 客户端调用：uniCloud.importObject('api').getBanners() 等
 *
 * 返回统一信封：{ errCode: 0, data: ... }
 */
const db = uniCloud.database()
const dbCmd = db.command
const articleCol = db.collection('xcxcms-article')
const typeCol = db.collection('xcxcms-type')

/** 小程序接口每页条数（与经典版 ConstantPool.API_PAGE_SIZE 一致） */
const API_PAGE_SIZE = 15

module.exports = {
  _before() {},

  /**
   * 轮播图列表
   */
  async getBanners() {
    const res = await articleCol
      .where({ isBanner: true })
      .orderBy('updatetime', 'desc')
      .get()
    return ok(res.data)
  },

  /**
   * 分类列表（按权重升序）
   */
  async getTypes() {
    const res = await typeCol.orderBy('weight', 'asc').get()
    return ok(res.data)
  },

  /**
   * 分类文章分页列表
   * @param {object} params
   * @param {string} params.typeId 分类 _id
   * @param {number} [params.page=0] 页码，从 0 开始
   */
  async getArticles({ typeId, page = 0 } = {}) {
    if (!typeId) {
      return ok([])
    }
    // 未设权重的文章排到最后（等价于经典版 IFNULL(weight, 999999)）
    const res = await articleCol
      .aggregate()
      .match({ pid: typeId, isBanner: dbCmd.neq(true) })
      .addFields({ _w: { $ifNull: ['$weight', 999999] } })
      .sort({ _w: 1, updatetime: -1 })
      .skip(page * API_PAGE_SIZE)
      .limit(API_PAGE_SIZE)
      .end()
    return ok(res.data)
  },

  /**
   * 文章详情；不存在时返回 errCode 404（客户端据此提示）
   */
  async getArticle({ id } = {}) {
    if (!id) {
      throw new Error('参数错误：缺少文章 id')
    }
    const res = await articleCol.doc(id).get()
    if (!res.data || res.data.length === 0) {
      return { errCode: 404, errMsg: 'article not found', data: null }
    }
    return ok(res.data[0])
  }
}

function ok(data) {
  return { errCode: 0, errMsg: 'ok', data }
}

/**
 * 按字符（码点）截断（如需在云端做简介预览时使用，与经典版 TextUtils 行为一致）
 * @param {string} content
 * @param {number} maxChars
 */
function truncate(content, maxChars) {
  if (content == null) return ''
  if (content.codePointCount(0, content.length) <= maxChars) {
    return content
  }
  return content.substring(0, content.offsetByCodePoints(0, maxChars))
}
