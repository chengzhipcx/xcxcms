/**
 * 后台管理云对象（对应经典版 UserController/ArticleController/TypeController）
 *
 * 登录返回 token（7 天有效，存 xcxcms-token 集合），
 * 其余方法均须携带 token 校验。
 *
 * 客户端调用：uniCloud.importObject('admin').login({...}) 等
 */
const crypto = require('crypto')

const db = uniCloud.database()
const dbCmd = db.command
const accountCol = db.collection('xcxcms-account')
const tokenCol = db.collection('xcxcms-token')
const articleCol = db.collection('xcxcms-article')
const typeCol = db.collection('xcxcms-type')

/** 管理列表每页条数（与经典版 ConstantPool.PAGESIZE 一致） */
const PAGESIZE = 5
/** token 有效期：7 天 */
const TOKEN_TTL = 7 * 24 * 60 * 60 * 1000

module.exports = {
  _before() {},

  /**
   * 登录：校验账号密码（md5），签发 token
   */
  async login({ username, password } = {}) {
    if (!username || !password) {
      throw new Error('请输入用户名和密码')
    }
    const res = await accountCol.where({ username }).get()
    const account = res.data[0]
    if (!account || account.password !== md5(password)) {
      throw new Error('用户名或密码不正确')
    }
    const token = crypto.randomBytes(16).toString('hex')
    await tokenCol.add({
      token,
      accountId: account._id,
      username: account.username,
      creattime: Date.now(),
      expireAt: Date.now() + TOKEN_TTL
    })
    return ok({ token, username: account.username })
  },

  /**
   * 分类列表
   */
  async listTypes({ token } = {}) {
    await this._checkToken(token)
    const res = await typeCol.orderBy('weight', 'asc').get()
    return ok(res.data)
  },

  /**
   * 文章分页列表（typeId 为空查全部）
   */
  async listArticles({ token, typeId = '', page = 0 } = {}) {
    await this._checkToken(token)
    const where = typeId ? { pid: typeId } : {}
    const res = await articleCol
      .aggregate()
      .match(where)
      .addFields({ _w: { $ifNull: ['$weight', 999999] } })
      .sort({ _w: 1, updatetime: -1 })
      .skip(page * PAGESIZE)
      .limit(PAGESIZE)
      .end()
    return ok(res.data)
  },

  /**
   * 文章详情（编辑回填）
   */
  async getArticle({ token, id } = {}) {
    await this._checkToken(token)
    if (!id) {
      throw new Error('参数错误：缺少文章 id')
    }
    const res = await articleCol.doc(id).get()
    if (!res.data || res.data.length === 0) {
      throw new Error('文章不存在')
    }
    return ok(res.data[0])
  },

  /**
   * 新增文章；data = {title, content, imgUrl, pid, isBanner, weight}
   */
  async addArticle({ token, data } = {}) {
    await this._checkToken(token)
    const doc = validateArticle(data, false)

    const now = Date.now()
    doc.creattime = now
    doc.updatetime = now
    const res = await articleCol.add(doc)
    return ok({ id: res.id })
  },

  /**
   * 更新文章
   */
  async updateArticle({ token, data } = {}) {
    await this._checkToken(token)
    if (!data || !data.id) {
      throw new Error('参数错误：缺少文章 id')
    }
    const doc = validateArticle(data, true)
    doc.updatetime = Date.now()
    await articleCol.doc(data.id).update(doc)
    return ok({ id: data.id })
  },

  /**
   * 删除文章
   */
  async deleteArticle({ token, id } = {}) {
    await this._checkToken(token)
    if (!id) {
      throw new Error('参数错误：缺少文章 id')
    }
    await articleCol.doc(id).remove()
    return ok(true)
  },

  /**
   * 新增/更新分类；data = {id?, title, weight}
   */
  async saveType({ token, data } = {}) {
    await this._checkToken(token)
    if (!data || !data.title || !String(data.title).trim()) {
      throw new Error('请输入分类标题')
    }
    const now = Date.now()
    const doc = {
      title: String(data.title).trim(),
      weight: data.weight == null ? null : Number(data.weight)
    }
    if (data.id) {
      doc.updatetime = now
      await typeCol.doc(data.id).update(doc)
      return ok({ id: data.id })
    }
    doc.creattime = now
    doc.updatetime = now
    const res = await typeCol.add(doc)
    return ok({ id: res.id })
  },

  /**
   * 删除分类
   */
  async deleteType({ token, id } = {}) {
    await this._checkToken(token)
    if (!id) {
      throw new Error('参数错误：缺少分类 id')
    }
    await typeCol.doc(id).remove()
    return ok(true)
  },

  /**
   * 修改当前账号密码：校验原密码，成功后吊销该账号全部 token（强制重新登录）
   */
  async changePassword({ token, oldPassword, newPassword } = {}) {
    const tokenDoc = await this._checkToken(token)
    if (!newPassword || String(newPassword).length < 6) {
      throw new Error('新密码长度不能少于 6 位')
    }
    const accountRes = await accountCol.doc(tokenDoc.accountId).get()
    const account = accountRes.data[0]
    if (!account || account.password !== md5(oldPassword || '')) {
      throw new Error('原密码不正确')
    }
    await accountCol.doc(tokenDoc.accountId).update({
      password: md5(newPassword),
      updatetime: Date.now()
    })
    // 吊销该账号全部 token，强制使用新密码重新登录
    await tokenCol.where({ accountId: tokenDoc.accountId }).remove()
    return ok(true)
  },

  // ================= 私有方法（下划线开头不会暴露为云对象接口） =================

  /**
   * 校验 token；过期或不存在时抛错（客户端据此跳转登录页）
   */
  async _checkToken(token) {
    if (!token) {
      throw new Error('请先登录（token 缺失）')
    }
    const res = await tokenCol.where({ token }).get()
    const tokenDoc = res.data[0]
    if (!tokenDoc) {
      throw new Error('登录状态无效（token 不存在），请重新登录')
    }
    if (tokenDoc.expireAt < Date.now()) {
      await tokenCol.doc(tokenDoc._id).remove()
      throw new Error('登录已过期（token 过期），请重新登录')
    }
    return tokenDoc
  }
}

function ok(data) {
  return { errCode: 0, errMsg: 'ok', data }
}

function md5(str) {
  return crypto
    .createHash('md5')
    .update(str, 'utf8')
    .digest('hex')
}

/**
 * 校验并规整文章数据
 * @param {object} data 客户端提交的数据
 * @param {boolean} isUpdate 更新时字段可缺省（不更新未提交字段）
 */
function validateArticle(data, isUpdate) {
  if (!data) {
    throw new Error('参数错误：缺少文章数据')
  }
  const doc = {}
  const set = (key, value) => {
    if (isUpdate && value === undefined) return
    doc[key] = value
  }

  if (!isUpdate || data.title !== undefined) {
    if (!data.title || !String(data.title).trim()) {
      throw new Error('请输入标题')
    }
    set('title', String(data.title).trim())
  }
  set('content', data.content == null ? '' : String(data.content))
  set('imgUrl', data.imgUrl == null ? '' : String(data.imgUrl))

  // isBanner / pid：更新时未提交则保持原值不动（避免部分更新覆盖为默认值）
  if (!isUpdate || data.isBanner !== undefined) {
    set('isBanner', !!data.isBanner)
  }
  if (doc.isBanner) {
    // 轮播图无分类（pid 为 null，与经典版语义一致）
    set('pid', null)
  } else if (!isUpdate) {
    if (!data.pid) {
      throw new Error('请选择分类')
    }
    set('pid', data.pid)
  } else if (data.pid !== undefined) {
    set('pid', data.pid || null)
  }
  if (data.weight === null || data.weight === undefined || data.weight === '') {
    set('weight', null)
  } else {
    const w = Number(data.weight)
    if (Number.isNaN(w)) {
      throw new Error('权重必须是数字')
    }
    set('weight', w)
  }
  return doc
}
