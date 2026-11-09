<template>
  <view class="container">
    <!-- 轮播图 -->
    <swiper
      class="activity"
      indicator-dots
      autoplay
      :interval="5000"
      :duration="1000"
      circular
    >
      <swiper-item v-for="(img, idx) in imgUrls" :key="idx">
        <image
          class="slide-image"
          :src="img"
          mode="aspectFill"
          @tap="onBannerTap(idx)"
        />
      </swiper-item>
    </swiper>

    <!-- 分类 Tab -->
    <scroll-view class="top-tab" scroll-x>
      <view class="tab-inner">
        <view
          v-for="(t, idx) in typeList"
          :key="t._id"
          class="tab-item"
          :class="{ active: currentTypeId === t._id }"
          @tap="switchTab(t)"
        >
          {{ t.title }}
        </view>
      </view>
    </scroll-view>

    <!-- 文章列表 -->
    <view class="list">
      <view
        v-for="item in notify"
        :key="item._id"
        class="notify-item"
        @tap="goDetail(item._id)"
      >
        <image
          v-if="item.imgUrl"
          class="avatar"
          :src="item.imgUrl"
          mode="aspectFill"
        />
        <view class="notify-content">
          <text class="notify-title">{{ item.title }}</text>
          <text class="notify-detail">{{ item.content }}</text>
        </view>
      </view>

      <view v-if="notify.length === 0" class="empty-tip">
        该分类下暂无内容
      </view>
      <view v-else-if="!hasMore" class="empty-tip">— 没有更多了 —</view>
    </view>

    <!-- 顶部右侧入口：后台管理 + 关于 -->
    <view class="float-btns">
      <text class="float-btn" @tap="goAdmin">管理</text>
      <text class="float-btn" @tap="aboutShow = true">关于</text>
    </view>

    <!-- 关于弹窗：公众号名片 -->
    <view v-if="aboutShow" class="mask" @tap="aboutShow = false">
      <view class="about-box" @tap.stop>
        <image
          class="gzh-card"
          src="/static/images/wechat-codercheng.png"
          mode="widthFix"
        />
        <view class="about-site" @tap="copyWebsite">
          官网 codercheng.xyz（点击复制）
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { truncate } from '@/common/util.js'

const API_PAGE_SIZE = 15 // 与云对象 api 内保持一致

export default {
  data() {
    return {
      imgUrls: [],
      bannerList: [],
      typeList: [],
      currentTypeId: '',
      notify: [],
      page: 0,
      hasMore: true,
      loading: false,
      aboutShow: false
    }
  },
  onLoad() {
    this.loadBanners()
    this.loadTypes()
  },
  onPullDownRefresh() {
    this.page = 0
    this.hasMore = true
    this.loadBanners()
    this.loadTypes()
  },
  onReachBottom() {
    if (!this.hasMore || this.loading) return
    this.loading = true
    uni.showNavigationBarLoading()
    this.loadArticles(this.currentTypeId, this.page + 1, false)
  },
  methods: {
    getApi() {
      return uniCloud.importObject('api')
    },
    loadBanners() {
      this.getApi()
        .getBanners()
        .then(res => {
          this.bannerList = res.data
          this.imgUrls = res.data.map(a => a.imgUrl).filter(Boolean)
        })
        .catch(() => this.showError())
    },
    loadTypes() {
      this.getApi()
        .getTypes()
        .then(res => {
          this.typeList = res.data
          if (res.data.length > 0) {
            this.currentTypeId = res.data[0]._id
            this.loadArticles(this.currentTypeId, 0, true)
          }
          uni.stopPullDownRefresh()
          uni.hideNavigationBarLoading()
        })
        .catch(() => {
          uni.stopPullDownRefresh()
          this.showError()
        })
    },
    loadArticles(typeId, page, clear) {
      this.getApi()
        .getArticles({ typeId, page })
        .then(res => {
          const list = res.data || []
          // 预览截断（与经典版一致：最多 50 字，emoji 不截半）
          list.forEach(a => {
            a.content = truncate(a.content, 50)
          })
          this.notify = clear ? list : this.notify.concat(list)
          this.hasMore = list.length >= API_PAGE_SIZE
          if (list.length > 0) this.page = page
          this.loading = false
          uni.stopPullDownRefresh()
          uni.hideNavigationBarLoading()
        })
        .catch(() => {
          this.loading = false
          uni.stopPullDownRefresh()
          uni.hideNavigationBarLoading()
          this.showError()
        })
    },
    switchTab(t) {
      this.currentTypeId = t._id
      this.page = 0
      this.hasMore = true
      this.loadArticles(t._id, 0, true)
    },
    onBannerTap(idx) {
      const banner = this.bannerList[idx]
      if (banner) this.goDetail(banner._id)
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/article/detail?id=' + id })
    },
    goAdmin() {
      uni.navigateTo({ url: '/pages/admin/login' })
    },
    copyWebsite() {
      uni.setClipboardData({ data: 'https://codercheng.xyz' })
    },
    showError() {
      uni.showModal({
        title: '提示',
        content: '很抱歉，网络繁忙，请稍后重试！',
        showCancel: false
      })
    },
    onShareAppMessage() {
      return {
        title: 'cms资讯',
        path: '/pages/index/index'
      }
    }
  }
}
</script>

<style>
.activity {
  width: 750rpx;
  height: 360rpx;
}
.slide-image {
  width: 750rpx;
  height: 360rpx;
}
.top-tab {
  width: 750rpx;
  height: 88rpx;
  background: #f2f2f2;
  white-space: nowrap;
}
.tab-inner {
  display: flex;
  flex-direction: row;
}
.tab-item {
  display: inline-block;
  padding: 0 30rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  color: #333;
}
.tab-item.active {
  color: #21b100;
  font-weight: 600;
}
.notify-item {
  display: flex;
  flex-direction: row;
  width: 700rpx;
  padding: 30rpx 0 20rpx 0;
  margin: 0 25rpx;
  border-bottom: 1px solid #e2e2e2;
}
.avatar {
  width: 160rpx;
  height: 120rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
  background: #eee;
}
.notify-content {
  flex: 1;
}
.notify-title {
  display: block;
  color: #000;
  font-size: 32rpx;
  line-height: 50rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.notify-detail {
  display: block;
  color: #999;
  font-size: 26rpx;
  line-height: 34rpx;
  margin-top: 6rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
.empty-tip {
  text-align: center;
  color: #999;
  font-size: 26rpx;
  padding: 40rpx 0;
}
.float-btns {
  position: fixed;
  right: 20rpx;
  bottom: 60rpx;
  display: flex;
  flex-direction: column;
}
.float-btn {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 24rpx;
  padding: 12rpx 22rpx;
  border-radius: 30rpx;
  margin-top: 14rpx;
  text-align: center;
}
.mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99;
}
.about-box {
  background: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
}
.gzh-card {
  width: 600rpx;
  border-radius: 8rpx;
}
.about-site {
  text-align: center;
  color: #007aff;
  font-size: 26rpx;
  padding-top: 20rpx;
}
</style>
