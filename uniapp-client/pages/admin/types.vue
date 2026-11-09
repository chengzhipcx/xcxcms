<template>
  <view class="page">
    <view class="toolbar">
      <text class="add" @tap="startAdd">＋ 增加分类</text>
    </view>

    <!-- 编辑/新增表单 -->
    <view v-if="editing" class="form">
      <view class="field">
        <text class="label">标题 *</text>
        <input v-model="form.title" class="input" placeholder="分类标题" />
      </view>
      <view class="field">
        <text class="label">权重</text>
        <input v-model="form.weight" class="input" type="number" placeholder="数字越小越靠前" />
      </view>
      <view class="form-ops">
        <button class="btn" :loading="saving" @tap="doSave">保 存</button>
        <button class="btn ghost" @tap="editing = false">取 消</button>
      </view>
      <view class="err">{{ error }}</view>
    </view>

    <!-- 列表 -->
    <view v-for="t in list" :key="t._id" class="item">
      <text class="item-title">{{ t.title }}</text>
      <text class="item-weight">权重 {{ t.weight == null ? '-' : t.weight }}</text>
      <text class="op" @tap="startEdit(t)">编辑</text>
      <text class="op danger" @tap="doDelete(t)">删除</text>
    </view>

    <view v-if="list.length === 0 && !editing" class="empty">暂无分类，点上方「增加分类」</view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      list: [],
      editing: false,
      saving: false,
      error: '',
      form: { id: '', title: '', weight: '' }
    }
  },
  onShow() {
    this.load()
  },
  methods: {
    getAdmin() {
      return uniCloud.importObject('admin')
    },
    token() {
      return uni.getStorageSync('admin_token')
    },
    load() {
      this.getAdmin()
        .listTypes({ token: this.token() })
        .then(res => (this.list = res.data))
        .catch(e => this.handleAuthError(e))
    },
    startAdd() {
      this.form = { id: '', title: '', weight: '' }
      this.error = ''
      this.editing = true
    },
    startEdit(t) {
      this.form = {
        id: t._id,
        title: t.title,
        weight: t.weight == null ? '' : String(t.weight)
      }
      this.error = ''
      this.editing = true
    },
    doSave() {
      if (this.saving) return
      if (!this.form.title.trim()) {
        this.error = '请输入分类标题'
        return
      }
      this.saving = true
      this.getAdmin()
        .saveType({
          token: this.token(),
          data: {
            id: this.form.id || undefined,
            title: this.form.title.trim(),
            weight: this.form.weight === '' ? null : Number(this.form.weight)
          }
        })
        .then(() => {
          this.saving = false
          this.editing = false
          this.load()
        })
        .catch(e => {
          this.saving = false
          this.handleAuthError(e)
        })
    },
    doDelete(t) {
      uni.showModal({
        title: '确认删除',
        content: '确定删除「' + t.title + '」吗？',
        success: r => {
          if (r.confirm) {
            this.getAdmin()
              .deleteType({ token: this.token(), id: t._id })
              .then(() => this.load())
              .catch(e => this.handleAuthError(e))
          }
        }
      })
    },
    handleAuthError(e) {
      const msg = (e && e.message) || ''
      if (msg.indexOf('token') >= 0 || msg.indexOf('登录') >= 0) {
        uni.redirectTo({ url: '/pages/admin/login' })
      } else {
        this.error = msg || '操作失败'
      }
    }
  }
}
</script>

<style>
.page {
  padding: 20rpx;
}
.toolbar {
  margin-bottom: 16rpx;
}
.add {
  color: #3c8dbc;
  font-size: 30rpx;
}
.form {
  background: #fff;
  border-radius: 10rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}
.field {
  margin-bottom: 24rpx;
}
.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 12rpx;
}
.input {
  border: 1px solid #ddd;
  border-radius: 8rpx;
  height: 76rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}
.form-ops {
  display: flex;
  flex-direction: row;
}
.btn {
  flex: 1;
  background: #3c8dbc;
  color: #fff;
  font-size: 30rpx;
}
.btn.ghost {
  background: #f0f0f0;
  color: #666;
  margin-left: 20rpx;
}
.err {
  color: #e64340;
  font-size: 26rpx;
  min-height: 36rpx;
  padding-top: 12rpx;
  text-align: center;
}
.item {
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #fff;
  border-radius: 10rpx;
  padding: 24rpx;
  margin-bottom: 14rpx;
}
.item-title {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}
.item-weight {
  font-size: 24rpx;
  color: #999;
  margin-right: 24rpx;
}
.op {
  font-size: 26rpx;
  color: #3c8dbc;
  margin-left: 20rpx;
}
.op.danger {
  color: #e64340;
}
.empty {
  text-align: center;
  color: #999;
  font-size: 26rpx;
  padding: 40rpx 0;
}
</style>
