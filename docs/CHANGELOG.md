# 变更记录（完善说明）

本项目基于 [chengzhipcx/xcxcms](https://github.com/chengzhipcx/xcxcms) 做了系统性完善，按主题归类如下。

## 一、Bug 修复

| 问题 | 修复 |
| --- | --- |
| `Md5Utils` 摘要以 `0` 开头时丢失前导零（输出 31 位），对应密码**永远无法登录** | 改为固定 32 位小写输出，附单元测试回归（含暴力搜索首字节 < 0x10 的用例） |
| `UserController.home` 引用的 `userHome.jsp` 不存在，访问即 500 | 补全页面并升级为完整账号管理页 |
| `/api/findByIdApi` 查询不存在文章时空指针 500 | 返回 404 + JSON 错误信息 |
| `db.properties` 指向原作者远程数据库（含真实密码），库名与 `cms.sql` 不一致 | 改为本机默认配置，库名对齐 `weichatarticle` |
| `log4j.properties` 残留另一项目配置（含内网 Oracle 地址与账号） | 重写为干净配置，日志级别 DEBUG → INFO |
| `bannerHome.jsp` jQuery 相对路径 404 | 改为 contextPath 绝对路径 |
| `/admin` 登录态判断被注释，登录后访问后台入口回到登录页 | 恢复判断：已登录重定向到 `/home` |
| 文章列表内容按 char 截断可能切断 emoji（代理对） | 新增 `TextUtils.truncate` 按码点截断，附单测 |
| Maven 构建下 `war` 会同时打入老工程离线 jar 与 Maven 依赖（mysql 驱动双版本冲突） | `warSourceExcludes` 排除 `WEB-INF/lib/*.jar`，运行时依赖统一由 Maven 管理 |

## 二、安全加固

- **上传文件扩展名白名单**（jpg/jpeg/png/gif/webp）：原先可上传任意文件，`.jsp` 落在应用目录会被 Tomcat 直接执行，构成远程代码执行漏洞；非法类型在编辑页给出明确报错并保留表单数据
- **存储型 XSS 防护**：全部 JSP 动态输出 `fn:escapeXml` 转义；后台 AJAX 拼接的表格行增加 JS `esc()` 转义（标题含 `<script>` 的文章不再可执行）
- **登录失败限流**：`LoginRateLimiter` 对同一 用户名+IP 连续失败 5 次锁定 10 分钟
- **拦截器补齐**：`/user/**` 纳入登录拦截（放行 `/user/login`），后台不再有未授权入口
- **fastjson 1.2.19 → 1.2.83**：旧版存在 autoType 反序列化 RCE；Eclipse 离线 lib 同步替换
- **API 统一异常兜底**：出错返回 JSON 500，不再向前端暴露堆栈页
- mysql-connector 5.0.8 → 5.1.49（Maven 与 lib 均已更新）

## 三、功能完善

- **账号管理**：账号列表 / 新增账号（重名、长度、两次密码一致性校验）/ 删除账号（不能删自己、不能删最后一个）/ 修改密码（校验原密码，成功后强制重新登录）
- **文章权重排序**：数字越小越靠前，未设置按时间倒序；编辑页恢复权重输入
- **友好错误页**：`error.jsp` 处理 404 / 500 / Throwable；后台会话 30 分钟超时
- **API 日期格式化**：`yyyy-MM-dd HH:mm:ss`（原为毫秒时间戳）
- 图片 URL 拼接空值 / 完整 URL 保护

## 四、界面（AdminLTE）

- 后台整体升级为 AdminLTE 布局：左侧边栏导航（`jsp:param` 驱动菜单高亮）、顶栏用户下拉菜单、内容盒子（box）、空数据提示、标准分页组件
- 登录页渐变背景重设计；错误页对齐风格
- 后台页脚加官网链接；小程序「联系」弹窗替换为公众号名片（coder程）+ 官网复制
- 自定义样式集中在 `web/css/index.css`（并清除了原文件中污染全局按钮的残留样式）

## 五、小程序端

- `ROOT_URL` 改为本地调试地址；删除未注册的 `logs` 模板残留页面
- 首页：下拉刷新、上拉加载守卫（无更多数据不再重复请求）、空状态与「没有更多了」提示、分享文案优化
- 详情页：`rich-text` 渲染（纯文本自动转义 + 换行转 `<br/>`，含 HTML 时直接渲染）、导航栏显示文章标题、分享标题使用文章标题
- 新增 `sitemap.json` 并注册到 `app.json`

## 六、工程化

- 新增 `pom.xml`：`mvn clean package` 一键出 war（`--release 8` 编译，兼容 Eclipse 目录结构 `src/` + `test/` + `web/`）
- **单元测试**（JUnit 4.13.2）：`Md5UtilsTest`、`TextUtilsTest`，共 7 个用例
- GitHub Actions CI（JDK 8 / 17 矩阵构建）
- 删除误提交的 `build/` 编译产物，新增 `.gitignore`
- `cms.sql` 增加索引：`tb_article(pid, isBanner)`、`tb_account(username)`
- 重写 README 与本文档；后台界面截图见 `docs/screenshots/`

## 验证情况

- `mvn clean package`：编译 + 7 个单测全部通过
- Tomcat 9.0.123 + MariaDB 10.6.28（兼容 MySQL 5.5+ 语法）端到端回归：
  登录 / 鉴权拦截 / 四个小程序 API / 文章 CRUD 与图片上传 / 上传白名单拒绝 `.jsp` /
  XSS 转义（静态 + AJAX 双路径）/ 权重排序 / 账号增删改与修改密码 7 场景 /
  登录限流锁定 / 404 错误页 / 退出登录 —— 全部通过
- 后台界面已在浏览器逐页截图核验（`docs/screenshots/`）
