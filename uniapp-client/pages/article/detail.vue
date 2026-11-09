<template>
  <view class="detail">
    <view class="detail-box">
      <text class="title">{{ title }}</text>
      <view class="meta">
        <text class="time">{{ date }}</text>
      </view>
    </view>
    <view class="content">
      <image v-if="imgUrl" class="cover" :src="imgUrl" mode="widthFix" />
      <rich-text class="rich" :nodes="htmlContent"></rich-text>
    </view>
  </view>
</template>

<script>
import { formatDate } from '@/common/util.js'

export default {
  data() {
    return {
      id: '',
      title: '',
      date: '',
      imgUrl: '',
      htmlContent: ''
    }
  },
  onLoad(options) {
    this.id = options.id
    this.loadDetail()
  },
  methods: {
    loadDetail() {
      uniCloud
        .importObject('api')
        .getArticle({ id: this.id })
        .then(res => {
          const a = res.data
          uni.setNavigationBarTitle({ title: a.title || '文章详情' })
          this.title = a.title
          this.date = formatDate(a.updatetime)
          this.imgUrl = a.imgUrl || ''
          this.htmlContent = this.toRichHtml(a.content)
        })
        .catch(() => {
          uni.showModal({
            title: '提示',
            content: '很抱歉，文章不存在或网络繁忙！',
            showCancel: false
          })
        })
    },
    /**
     * 纯文本自动转义 + 换行转 <br/>；含 HTML 标签的内容交给 rich-text 直接渲染
     */
    toRichHtml(content) {
      const raw = content || ''
      if (/<[a-z][\s\S]*>/i.test(raw)) {
        return raw
      }
      return raw
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br/>')
    },
    onShareAppMessage() {
      return {
        title: this.title || 'cms',
        path: '/pages/article/detail?id=' + this.id
      }
    }
  }
}
</script>

<style>
.detail-box {
  margin-top: 30rpx;
  padding: 0 30rpx;
}
.title {
  display: block;
  font-size: 44rpx;
  color: #000;
  margin-bottom: 12rpx;
  font-weight: 600;
}
.meta {
  font-size: 28rpx;
  color: #888;
}
.content {
  padding: 0 30rpx;
  margin-top: 24rpx;
  padding-bottom: 60rpx;
}
.cover {
  display: block;
  width: 100%;
  border-radius: 6rpx;
  background: #eee;
}
.rich {
  display: block;
  font-size: 32rpx;
  color: #111;
  line-height: 50rpx;
  margin-top: 24rpx;
  word-break: break-all;
}
</style>
