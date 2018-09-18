//index.js
//获取应用实例
var app = getApp()
var ROOT_URL = getApp().ROOT_URL
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
  bannerList:[]
  },
  onLoad: function () {      
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

          // titleList.push("联系我们");
          // res.data.push({"id":0,"title":"联系我们"});    
          that.setData({
             navTab:titleList,
             typeList:res.data
          })  

          currentTypeId = res.data[0].id;
          //获取默认列表数据         
          that.getListDate(currentTypeId,0, true);
        },
        fail:function(res){
          that.showError();
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
      wx.showNavigationBarLoading()
      loadTime++;
      this.getListDate(currentTypeId,loadTime, false);
      setTimeout(function(){wx.hideNavigationBarLoading();   
      wx.stopPullDownRefresh();}, 2000);
  },
  getListDate:function(typeId,loadTime,isclear){
      var that = this;
      //获取更多列表数据
      wx.request({
        url: ROOT_URL+'/api/listApiArticle?id='+typeId+'&loadTime='+loadTime, 
        method:'GET',
        header: {
            'content-type': 'application/json'
        },
        success: function(res) {
          if(res.data.length == 0){
            if (isclear){
              that.setData({
                notify:[]
              })
            }
             return;
          }
          var dataList;
          if(isclear){
             dataList = []
          } else{
            dataList = that.data.notify;
          }
          dataList=dataList.concat(res.data);
          //console.log(dataList);
          that.setData({
            notify:dataList
          })
    
        },
        fail:function(res){
          that.showError();
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
      currentNavtab: e.currentTarget.dataset.idx
    });
    loadTime = 0;
    this.getListDate(currentTypeId,loadTime, true);
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
  contactMe: function(event) {
    wx.makePhoneCall({phoneNumber: '4000668566'})
  },
  onShareAppMessage: function () {
    return {
      title: 'cms',
      desc: 'cms',
      path: 'pages/index/index'
    }
  }
})
