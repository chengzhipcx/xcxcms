// pages/article/detail.js
var app = getApp()
var ROOT_URL = getApp().ROOT_URL
Page({
  data:{
    title:'',
    date:'',
    htmlContent:'',
    imgUrl:'',
    modalHidden:true,
    btnHidden:false,
    mid:0
  },
  onLoad:function(options){
    this.data.mid = options.id;
    var that = this;
      //获取文章详情
      wx.request({
        url: ROOT_URL+'/api/findByIdApi?id='+parseInt(options.id),
        method:'GET',
        header: {
            'content-type': 'application/json'
        },
        success: function(res) {
          wx.setNavigationBarTitle({ title: res.data.title || '文章详情' });
          that.setData({
             title:res.data.title,
             htmlContent:toRichHtml(res.data.content),
             imgUrl:res.data.imgurl,
             date:res.data.date
          })
        },
        fail:function(res){
          that.showError();
        }
      });
  },
  showError:function(){
      wx.showModal({
        title: '提示',
        content: '很抱歉，文章不存在或网络繁忙！',
        showCancel: false
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
  contactMe: function(event) {
    wx.makePhoneCall({phoneNumber: '4000668566'})
  },
  onShareAppMessage: function () {
    return {
      title: this.data.title || 'cms',
      desc: 'cms系统小程序端',
      path: 'pages/article/detail?id='+this.data.mid
    }
  }
})

/**
 * 把文章内容转成 rich-text 可用的 HTML：
 * - 内容本身已是 HTML（后台直接写了标签）则原样交给 rich-text 渲染
 * - 纯文本则先转义再换行转 <br/>，防止意外标签注入
 */
function toRichHtml(content) {
  var raw = content || '';
  if (/<[a-z][\s\S]*>/i.test(raw)) {
    return raw;
  }
  return raw
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br/>');
}
