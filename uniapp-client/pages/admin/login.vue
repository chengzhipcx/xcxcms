<template>
  <view class="login-page">
    <view class="login-box">
      <view class="login-title">CMS 后台管理</view>
      <input
        v-model="username"
        class="input"
        placeholder="用户名"
        placeholder-class="ph"
      />
      <input
        v-model="password"
        class="input"
        password
        placeholder="密码"
        placeholder-class="ph"
      />
      <button class="btn" :loading="loading" @tap="doLogin">登 录</button>
      <view class="tip">{{ msg }}</view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      msg: ''
    }
  },
  methods: {
    doLogin() {
      if (this.loading) return
      if (!this.username || !this.password) {
        this.msg = '请输入用户名和密码'
        return
      }
      this.loading = true
      uniCloud
        .importObject('admin')
        .login({ username: this.username, password: this.password })
        .then(res => {
          this.loading = false
          uni.setStorageSync('admin_token', res.data.token)
          uni.setStorageSync('admin_username', res.data.username)
          uni.redirectTo({ url: '/pages/admin/articles' })
        })
        .catch(e => {
          this.loading = false
          this.msg = (e && e.message) || '登录失败，请稍后重试'
        })
    }
  }
}
</script>

<style>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #3c8dbc 0%, #1f4568 60%, #152735 100%);
}
.login-box {
  width: 600rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 50rpx 40rpx;
}
.login-title {
  text-align: center;
  font-size: 36rpx;
  font-weight: 600;
  margin-bottom: 40rpx;
  color: #333;
}
.input {
  border: 1px solid #ddd;
  border-radius: 8rpx;
  height: 80rpx;
  padding: 0 24rpx;
  margin-bottom: 24rpx;
  font-size: 30rpx;
}
.ph {
  color: #bbb;
}
.btn {
  background: #3c8dbc;
  color: #fff;
  font-size: 32rpx;
  margin-top: 10rpx;
}
.tip {
  text-align: center;
  color: #e64340;
  font-size: 26rpx;
  min-height: 40rpx;
  padding-top: 16rpx;
}
</style>
