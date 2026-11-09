//index.js
//获取应用实例
var app = getApp()
var ROOT_URL = getApp().ROOT_URL
var API_PAGE_SIZE = 15  // 与服务端 ConstantPool.API_PAGE_SIZE 保持一致
var currentTypeId = 0
var loadTime = 0
Page({
  data: {
  currentTypeId:0,
  navTab: [],
  currentNavtab: "0",
  modalHidden:true,
  btnHidden:false,
  indicatorDots: true,
  autoplay: true,
  interval: 5000,
  duration: 1000,
  notify: [],
  imgUrls:[],
  typeList:[],
  bannerList:[],
  hasMore:true,
  loading:false
  },
  onLoad: function () {
      var that = this;
      that.loadBanners();
      that.loadTypes();
  },
  onPullDownRefresh: function () {
      // 下拉刷新：重置分页并重新拉取全部数据
      loadTime = 0;
      var that = this;
      that.setData({ hasMore: true, loading: false });
      wx.showNavigationBarLoading();
      that.loadBanners();
      that.loadTypes();
  },
  loadBanners: function(){
      var that = this;
      //获取焦点图
      wx.request({
        url: ROOT_URL+'/api/list',
        method:'GET',
        header: {
            'content-type': 'application/json'
        },
        success: function(res) {
          var imgList = [];
          for (var i=0; i<res.data.length; i++){
               imgList.push(res.data[i].imgurl)
          }
          that.setData({
             imgUrls:imgList,
             bannerList:res.data
          })
        },
        fail:function(res){
          that.showError();
        }
      });
  },
  loadTypes: function(){
      var that = this;
      //获取标题分类
      wx.request({
        url: ROOT_URL+'/api/listApiType',
        method:'GET',
        header: {
            'content-type': 'application/json'
        },
        success: function(res) {
          var titleList = [];
          for (var i=0; i<res.data.length; i++){
               titleList.push(res.data[i].title)
          }

          that.setData({
             navTab:titleList,
             typeList:res.data
          })

          currentTypeId = res.data[0].id;
          //获取默认列表数据
          loadTime = 0;
          that.setData({ hasMore:true, loading:false });
          that.getListDate(currentTypeId, 0, true);
        },
        fail:function(res){
          that.showError();
        },
        complete:function(){
          wx.hideNavigationBarLoading();
          wx.stopPullDownRefresh();
        }
      });
  },
  showError:function(){
      wx.showModal({
        title: '提示',
        content: '很抱歉，网络繁忙，请稍后重试！',
        showCancel: false
      })
  },
  lower: function () {
      // 上拉加载下一页；没有更多数据或正在加载时忽略
      if (!this.data.hasMore || this.data.loading){
        return;
      }
      this.setData({ loading: true });
      wx.showNavigationBarLoading()
      this.getListDate(currentTypeId, loadTime + 1, false);
  },
  getListDate:function(typeId,page,isclear){
      var that = this;
      //获取列表数据
      wx.request({
        url: ROOT_URL+'/api/listApiArticle?id='+typeId+'&loadTime='+page,
        method:'GET',
        header: {
            'content-type': 'application/json'
        },
        success: function(res) {
          var list = res.data || [];
          that.setData({
            loading:false,
            hasMore: list.length >= API_PAGE_SIZE,
            notify: isclear ? list : that.data.notify.concat(list)
          })
          if (list.length > 0){
            loadTime = page;
          }
        },
        fail:function(res){
          that.setData({ loading:false });
          that.showError();
        },
        complete:function(){
          wx.hideNavigationBarLoading();
          wx.stopPullDownRefresh();
        }
      });
  },
  switchTab: function(e){
    currentTypeId =this.data.typeList[e.currentTarget.dataset.idx].id;
    if (currentTypeId == 0){
      this.modalTap();
      return;
    }

    this.setData({
      currentNavtab: e.currentTarget.dataset.idx,
      hasMore:true,
      loading:false
    });
    loadTime = 0;
    this.getListDate(currentTypeId, 0, true);
  },
  itemClick:function(e){
    wx.navigateTo({
      url: '../article/detail?id='+e.currentTarget.dataset.id
    })
  },
  modalTap: function (e) {
    this.setData({
        modalHidden: false
    })
  },
  modalHide: function(e) {
    this.setData({
        modalHidden: true
    })
  },
  touchHide:function(e){
     this.setData({
        btnHidden: true
    })
  },
  touchShow:function(e){
    this.setData({
        btnHidden: false
    })
  },
  swipclick: function(e){
    wx.navigateTo({
      url: '../article/detail?id='+this.data.bannerList[e.currentTarget.dataset.idx].id
    })
  },
  copyWebsite: function (event) {
    wx.setClipboardData({ data: 'https://codercheng.xyz' })
  },
  onShareAppMessage: function () {
    return {
      title: 'cms资讯',
      desc: 'cms系统小程序端',
      path: 'pages/index/index'
    }
  }
})
