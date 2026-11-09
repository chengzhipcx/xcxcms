# cms-client · 微信小程序端

xcxcms 的小程序客户端，读取 Java 后台接口展示分类 Tab、文章列表、轮播图与文章详情。

## 目录

```
cms-client
├── app.js / app.json / app.wxss   全局配置（ROOT_URL 在 app.js）
├── pages/index/                   首页：轮播图 swiper + 分类 Tab + 文章列表
└── pages/article/                 文章详情（rich-text 渲染 + 分享）
```

## 使用步骤

1. **配置后台地址**：修改 `app.js` 中的 `ROOT_URL`（末尾带 `/`）：
   - 本地调试：`http://localhost:8080/cms/`
   - 线上环境：`https://你的域名/cms/`
2. **微信开发者工具**导入本目录
3. 本地调试需在「详情 → 本地设置」勾选 **不校验合法域名**
4. `project.config.json` 的 `appid` 替换为自己的测试号

## 页面说明

| 页面 | 功能 |
| --- | --- |
| 首页 | 轮播图自动轮播（点击进详情）、分类 Tab 切换、文章列表（下拉刷新 / 上拉加载更多，每页 15 条）、空数据与「没有更多了」提示 |
| 详情 | rich-text 渲染正文、导航栏显示标题、分享给好友（带文章 id） |

## 依赖的后台接口

见项目根 README「小程序数据接口」一节：`/api/list`、`/api/listApiType`、`/api/listApiArticle`、`/api/findByIdApi`。

> 每页条数与后台 `ConstantPool.API_PAGE_SIZE = 15` 保持一致，如需调整请两端同步修改。

## 关于作者

- 个人网站：[codercheng.xyz](https://codercheng.xyz)
- 微信公众号：**coder程**（微信搜一搜，或扫码关注）

<p align="center">
  <img src="../docs/wechat-codercheng.png" width="480" alt="微信公众号：coder程">
</p>
