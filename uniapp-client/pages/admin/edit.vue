<template>
  <view class="page">
    <view class="form">
      <view class="field">
        <text class="label">图片（jpg/png/gif/webp）</text>
        <view class="upload-row">
          <image
            v-if="form.imgUrl"
            class="preview"
            :src="form.imgUrl"
            mode="aspectFill"
            @tap="chooseImage"
          />
          <view v-else class="preview placeholder" @tap="chooseImage">＋ 选择图片</view>
          <text v-if="form.imgUrl" class="hint" @tap="form.imgUrl = ''">移除图片</text>
        </view>
      </view>

      <view class="field">
        <text class="label">标题 *</text>
        <input v-model="form.title" class="input" placeholder="请输入标题" />
      </view>

      <view class="field">
        <text class="label">权重</text>
        <input
          v-model="form.weight"
          class="input"
          type="number"
          placeholder="数字越小越靠前，留空按时间排序"
        />
      </view>

      <view class="field">
        <text class="label">内容</text>
        <textarea
          v-model="form.content"
          class="textarea"
          :maxlength="-1"
          placeholder="文章正文"
        />
      </view>

      <view v-if="!isBanner" class="field">
        <text class="label">分类 *</text>
        <picker :range="typeNames" @change="onTypeChange">
          <view class="input picker-view">{{ currentTypeName || '请选择分类' }} ▾</view>
        </picker>
      </view>

      <view v-if="!isBanner" class="field">
        <text class="label">是否轮播图</text>
        <switch
          :checked="form.isBanner"
          color="#3c8dbc"
          @change="e => (form.isBanner = e.detail.value)"
        />
      </view>

      <button class="btn" :loading="saving" @tap="doSave">提 交</button>
      <view class="err">{{ error }}</view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      id: '',
      isBanner: false,
      typeList: [],
      form: {
        imgUrl: '',
        title: '',
        weight: '',
        content: '',
        pid: '',
        isBanner: false
      },
      saving: false,
      error: ''
    }
  },
  computed: {
    typeNames() {
      return this.typeList.map(t => t.title)
    },
    currentTypeName() {
      const t = this.typeList.find(t => t._id === this.form.pid)
      return t ? t.title : ''
    }
  },
  onLoad(options) {
    this.id = options.id || ''
    this.isBanner = options.isBanner === '1'
    this.form.isBanner = this.isBanner
    this.form.pid = options.pid || ''
    this.loadTypes()
    if (this.id) this.loadArticle()
  },
  methods: {
    getAdmin() {
      return uniCloud.importObject('admin')
    },
    token() {
      return uni.getStorageSync('admin_token')
    },
    loadTypes() {
      this.getAdmin()
        .listTypes({ token: this.token() })
        .then(res => (this.typeList = res.data))
        .catch(e => this.fail(e))
    },
    loadArticle() {
      this.getAdmin()
        .getArticle({ token: this.token(), id: this.id })
        .then(res => {
          const a = res.data
          this.form.title = a.title
          this.form.content = a.content
          this.form.imgUrl = a.imgUrl || ''
          this.form.pid = a.pid || ''
          this.form.isBanner = !!a.isBanner
          this.form.weight = a.weight == null ? '' : String(a.weight)
        })
        .catch(e => this.fail(e))
    },
    onTypeChange(e) {
      const idx = Number(e.detail.value)
      this.form.pid = this.typeList[idx]._id
    },
    chooseImage() {
      uni.chooseImage({
        count: 1,
        success: res => {
          uni.showLoading({ title: '上传中' })
          uniCloud
            .uploadFile({
              filePath: res.tempFilePaths[0],
              cloudPath: 'article/' + Date.now() + '-' + res.tempFilePaths[0].split('/').pop()
            })
            .then(up => {
              uni.hideLoading()
              this.form.imgUrl = up.fileID
            })
            .catch(() => {
              uni.hideLoading()
              uni.showToast({ title: '图片上传失败', icon: 'none' })
            })
        }
      })
    },
    doSave() {
      if (this.saving) return
      if (!this.form.title.trim()) {
        this.error = '请输入标题'
        return
      }
      if (!this.isBanner && !this.form.pid) {
        this.error = '请选择分类'
        return
      }
      this.saving = true
      this.error = ''
      const payload = {
        token: this.token(),
        data: {
          id: this.id || undefined,
          title: this.form.title.trim(),
          content: this.form.content,
          imgUrl: this.form.imgUrl,
          pid: this.isBanner ? null : this.form.pid,
          isBanner: this.isBanner || this.form.isBanner,
          weight: this.form.weight === '' ? null : Number(this.form.weight)
        }
      }
      const call = this.id
        ? this.getAdmin().updateArticle(payload)
        : this.getAdmin().addArticle(payload)
      call
        .then(() => {
          this.saving = false
          uni.showToast({ title: '保存成功', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 600)
        })
        .catch(e => {
          this.saving = false
          this.fail(e)
        })
    },
    fail(e) {
      const msg = (e && e.message) || '操作失败，请稍后重试'
      if (msg.indexOf('token') >= 0 || msg.indexOf('登录') >= 0) {
        uni.redirectTo({ url: '/pages/admin/login' })
      } else {
        this.error = msg
      }
    }
  }
}
</script>

<style>
.page {
  padding: 20rpx;
}
.form {
  background: #fff;
  border-radius: 10rpx;
  padding: 24rpx;
}
.field {
  margin-bottom: 28rpx;
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
.picker-view {
  line-height: 76rpx;
  color: #333;
}
.textarea {
  border: 1px solid #ddd;
  border-radius: 8rpx;
  width: 100%;
  height: 300rpx;
  padding: 16rpx 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}
.upload-row {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.preview {
  width: 200rpx;
  height: 140rpx;
  border-radius: 8rpx;
  background: #eee;
}
.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 26rpx;
  border: 1px dashed #ccc;
}
.hint {
  color: #e64340;
  font-size: 26rpx;
  margin-left: 24rpx;
}
.btn {
  background: #3c8dbc;
  color: #fff;
  font-size: 32rpx;
  margin-top: 10rpx;
}
.err {
  color: #e64340;
  font-size: 26rpx;
  min-height: 40rpx;
  padding-top: 14rpx;
  text-align: center;
}
</style>
