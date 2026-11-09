<template>
  <view class="page">
    <!-- 分类筛选 + 操作 -->
    <view class="toolbar">
      <picker :range="typeNames" @change="onTypeChange">
        <view class="picker">分类：{{ currentTypeName || '全部' }} ▾</view>
      </picker>
      <view class="spacer" />
      <text class="link" @tap="goTypes">分类管理</text>
      <text class="link" @tap="goAdd">＋ 增加文章</text>
      <text class="link" @tap="goBanners">轮播图</text>
      <text class="link" @tap="logout">退出</text>
    </view>

    <!-- 列表 -->
    <view v-for="item in list" :key="item._id" class="item">
      <image
        v-if="item.imgUrl"
        class="thumb"
        :src="item.imgUrl"
        mode="aspectFill"
      />
      <view class="item-body">
        <text class="item-title">{{ item.title }}</text>
        <text class="item-meta">{{ item.updatetimeText }}</text>
      </view>
      <view class="item-ops">
        <text class="op" @tap="goEdit(item._id)">编辑</text>
        <text class="op danger" @tap="doDelete(item)">删除</text>
      </view>
    </view>

    <view v-if="list.length === 0" class="empty">暂无文章，点右上角「增加文章」</view>
    <view v-else-if="!hasMore" class="empty">— 没有更多了 —</view>
  </view>
</template>

<script>
import { formatDateTime } from '@/common/util.js'

export default {
  data() {
    return {
      typeList: [],
      currentTypeId: '',
      list: [],
      page: 0,
      hasMore: true,
      showBanner: false
    }
  },
  computed: {
    typeNames() {
      return ['全部'].concat(this.typeList.map(t => t.title))
    },
    currentTypeName() {
      const t = this.typeList.find(t => t._id === this.currentTypeId)
      return t ? t.title : '全部'
    }
  },
  onShow() {
    this.loadTypes()
  },
  onPullDownRefresh() {
    this.page = 0
    this.hasMore = true
    this.loadList()
    uni.stopPullDownRefresh()
  },
  onReachBottom() {
    if (!this.hasMore) return
    this.loadList(this.page + 1)
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
        .then(res => {
          this.typeList = res.data
          this.loadList(0)
        })
        .catch(e => this.handleAuthError(e))
    },
    loadList(page = 0) {
      this.getAdmin()
        .listArticles({
          token: this.token(),
          typeId: this.currentTypeId,
          page
        })
        .then(res => {
          const list = res.data || []
          list.forEach(a => {
            a.updatetimeText = formatDateTime(a.updatetime)
          })
          this.list = page === 0 ? list : this.list.concat(list)
          this.hasMore = list.length >= 15
          if (list.length > 0) this.page = page
        })
        .catch(e => this.handleAuthError(e))
    },
    onTypeChange(e) {
      const idx = Number(e.detail.value)
      this.currentTypeId = idx === 0 ? '' : this.typeList[idx - 1]._id
      this.page = 0
      this.hasMore = true
      this.loadList(0)
    },
    doDelete(item) {
      uni.showModal({
        title: '确认删除',
        content: '确定删除「' + item.title + '」吗？',
        success: r => {
          if (r.confirm) {
            this.getAdmin()
              .deleteArticle({ token: this.token(), id: item._id })
              .then(() => this.loadList(0))
              .catch(e => this.handleAuthError(e))
          }
        }
      })
    },
    goAdd() {
      uni.navigateTo({
        url: '/pages/admin/edit?pid=' + (this.currentTypeId || '') + '&isBanner=' + (this.showBanner ? 1 : 0)
      })
    },
    goEdit(id) {
      uni.navigateTo({ url: '/pages/admin/edit?id=' + id })
    },
    goBanners() {
      uni.navigateTo({ url: '/pages/admin/edit?isBanner=1' })
    },
    goTypes() {
      uni.navigateTo({ url: '/pages/admin/types' })
    },
    logout() {
      uni.removeStorageSync('admin_token')
      uni.redirectTo({ url: '/pages/admin/login' })
    },
    handleAuthError(e) {
      const msg = (e && e.message) || ''
      if (msg.indexOf('token') >= 0 || msg.indexOf('登录') >= 0) {
        uni.redirectTo({ url: '/pages/admin/login' })
      } else {
        uni.showToast({ title: msg || '操作失败', icon: 'none' })
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
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  flex-wrap: wrap;
}
.picker {
  font-size: 28rpx;
  color: #333;
}
.spacer {
  flex: 1;
}
.link {
  color: #3c8dbc;
  font-size: 26rpx;
  margin-left: 24rpx;
}
.item {
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}
.thumb {
  width: 100rpx;
  height: 76rpx;
  border-radius: 6rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
  background: #eee;
}
.item-body {
  flex: 1;
  overflow: hidden;
}
.item-title {
  display: block;
  font-size: 30rpx;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-meta {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
}
.item-ops {
  margin-left: 16rpx;
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
