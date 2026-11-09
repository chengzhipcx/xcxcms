<div align="center">

# xcxcms

**微信小程序 CMS 系统** —— Java 后台（管理端 + 小程序接口）+ 微信小程序客户端

一个轻量的资讯/内容管理方案：后台维护分类、文章与轮播图，小程序端展示列表与详情。

[![build](https://img.shields.io/badge/build-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)](#构建与测试)
[![java](https://img.shields.io/badge/Java-8%2B-007396?logo=openjdk&logoColor=white)](#环境要求)
[![spring](https://img.shields.io/badge/Spring%20MVC-4.3-6DB33F?logo=spring&logoColor=white)](#技术栈)
[![mybatis](https://img.shields.io/badge/MyBatis-3.2-red)](#技术栈)
[![mysql](https://img.shields.io/badge/MySQL-5.5%2B-4479A1?logo=mysql&logoColor=white)](#环境要求)

**已通过端到端验证**：登录鉴权 · CRUD · 图片上传 · 安全加固 · 单元测试 · 界面截图核验

> **本分支（`fastapi`）新增 Python FastAPI 版本后端**（`cms-server/cms-fastapi/`）：
> FastAPI + SQLAlchemy + PyMySQL + Jinja2，复用同一个 MySQL 库（`cms.sql` 直接初始化）、
> 同一套 AdminLTE 管理页面，小程序接口路径与响应形态逐字节一致。
> master 分支保留传统 Spring MVC 版本；`springboot` 分支为 Spring Boot 版；`uniapp` 分支为 uni-app + uniCloud Serverless 版。
> 详见下方 [Python FastAPI 版本](#python-fastapi-版本)。

</div>

---

## 目录

- [界面预览](#界面预览)
- [项目结构](#项目结构)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [后台功能](#后台功能)
- [小程序数据接口](#小程序数据接口)
- [小程序端配置](#小程序端配置)
- [生产部署](#生产部署)
- [构建与测试](#构建与测试)
- [常见问题](#常见问题)
- [Python FastAPI 版本](#python-fastapi-版本)
- [完善说明](#完善说明)

## 界面预览

| 登录 | 文章管理 |
| --- | --- |
| ![登录页](docs/screenshots/login.png) | ![文章管理](docs/screenshots/articles.png) |
| **账号管理** | **轮播图管理** |
| ![账号管理](docs/screenshots/users.png) | ![轮播图管理](docs/screenshots/banners.png) |

> 后台基于 AdminLTE + Bootstrap 3，全部页面已实测；截图见 [docs/screenshots](docs/screenshots)。

## 项目结构

```
xcxcms
├── cms-client/                 微信小程序客户端（微信开发者工具直接导入）
│   ├── pages/index/            首页：轮播图 + 分类 Tab + 文章列表（上拉/下拉刷新）
│   └── pages/article/          文章详情（rich-text 渲染）
├── cms-server/cms/             Java 后台（Spring MVC + MyBatis，可 Maven 构建）
│   ├── src/                    Java 源码与 Spring/MyBatis 配置
│   ├── test/                   JUnit 单元测试
│   ├── web/                    JSP 页面、静态资源、上传目录 img/
│   ├── web/WEB-INF/lib/        离线 jar（Eclipse 老工程方式使用；Maven 构建不打包）
│   └── pom.xml                 Maven 构建配置（推荐）
├── cms.sql                     MySQL 初始化脚本（建库 + 表 + 演示数据 + 索引）
├── cms-server/cms-fastapi/     Python FastAPI 版本后端（本分支新增）
│   ├── app/                    FastAPI 应用（路由 / Jinja2 模板 / 静态资源）
│   └── tests/                  pytest 单元测试
├── docs/
│   ├── CHANGELOG.md            完善记录（修复/加固/功能/工程化明细）
│   └── screenshots/            后台界面截图
└── .github/workflows/ci.yml    CI（JDK 8/17 矩阵构建）
```

## 技术栈

| 层 | 选型 |
| --- | --- |
| Web 框架 | Spring MVC 4.3.3（JSP + JSTL） |
| 持久层 | MyBatis 3.2.8 + mybatis-spring 1.2.4 |
| 连接池 | Druid 1.0.9 |
| 数据库 | MySQL 5.5+（兼容 MariaDB） |
| 序列化 | fastjson 1.2.83 |
| 后台 UI | AdminLTE 2.3.6 + Bootstrap 3.3.6 |
| 构建 | Maven（war，`--release 8`）/ Eclipse 老工程两种方式 |
| 测试 | JUnit 4.13.2 |

## 快速开始

### 环境要求

- JDK 8+（已在 JDK 8 / 17 / 24 上验证编译运行）
- Maven 3.6+（推荐；也可用 Eclipse/IDEA 导入老工程）
- Tomcat **8 或 9**（Tomcat 10+ 为 jakarta 命名空间，不兼容 Spring 4）
- MySQL 5.5+ / 微信开发者工具

### ① 初始化数据库

```bash
mysql -uroot -p < cms.sql
```

脚本自动创建 `weichatarticle` 库、三张表（账号 / 分类 / 文章）及演示数据。

### ② 修改三处配置

| 文件 | 配置 | 说明 |
| --- | --- | --- |
| `cms-server/cms/src/config/resource/db.properties` | `jdbc.url` / `username` / `password` | 本机 MySQL 连接信息 |
| `cms-server/cms/src/com/ht/weichat/utils/ConstantPool.java` | `IP` | 服务端对外地址（含上下文路径），用于拼接图片完整 URL。本地调试 `http://localhost:8080/cms`，线上改 `https://域名/cms` |
| `cms-client/app.js` | `ROOT_URL` | 小程序请求的后台地址，与上一致（末尾带 `/`） |

### ③ 构建并启动

```bash
cd cms-server/cms
mvn clean package
cp target/cms.war $TOMCAT_HOME/webapps/   # Windows: copy 到 webapps\
$TOMCAT_HOME/bin/startup.sh               # Windows: startup.bat
```

访问 **http://localhost:8080/cms/** 进入后台。

> **默认账号**：`13695316318` / `123456`（登录后右上角可修改密码、管理账号）

### ④ 运行小程序

1. 微信开发者工具导入 `cms-client/` 目录
2. 详情 → 本地设置 → 勾选**「不校验合法域名」**（本地 http 调试必需）
3. `project.config.json` 中的 `appid` 请替换为自己的测试号 appid

## 后台功能

| 模块 | 说明 |
| --- | --- |
| 文章管理 | 按分类分页、图片上传（白名单校验）、权重排序、简介预览 |
| 轮播图管理 | 独立维护小程序首页焦点图 |
| 分类管理 | 增删改、权重排序（小程序首页 Tab 顺序） |
| 账号管理 | 账号列表 / 新增 / 删除 / 修改密码（原密码校验，改后强制重登） |
| 安全 | 登录拦截、连续失败 5 次锁定 10 分钟、XSS 转义、上传白名单、友好错误页 |

## 小程序数据接口

所有接口 GET，返回 JSON（UTF-8）。

| 接口 | 说明 |
| --- | --- |
| `GET /api/list` | 轮播图列表 |
| `GET /api/listApiType` | 分类列表（按权重排序） |
| `GET /api/listApiArticle?id={分类id}&loadTime={页码-1}` | 分类文章分页，每页 15 条，`loadTime` 从 0 递增 |
| `GET /api/findByIdApi?id={文章id}` | 文章详情（含 `date` 字段；不存在时 HTTP 404） |

<details>
<summary>响应示例</summary>

```jsonc
// GET /api/findByIdApi?id=2
{
  "id": 2,
  "pid": 1,
  "title": "cms小程序上线啦",
  "content": "微信小程序版的cms系统 上线啦",
  "imgurl": "http://localhost:8080/cms/img/fe32f07a-....png",
  "isbanner": false,
  "updattime": "2018-09-17 18:51:59",
  "date": "2018-09-17"
}

// 文章不存在时
HTTP 404  {"error": "article not found"}

// GET /api/listApiArticle?id=1&loadTime=0
[
  { "id": 9, "title": "...", "content": "最多50字预览…", "imgurl": "http://…", "updattime": "2026-11-10 01:02:03", … }
]
```

图片地址由服务端拼接为完整 URL（`ConstantPool.IP` + `/img/xxx`）。

</details>

## 生产部署

1. `mvn clean package` 得到 `target/cms.war`，放入 Tomcat `webapps/`
2. `ConstantPool.IP` 与 `cms-client/app.js` 的 `ROOT_URL` 改为线上 https 域名（小程序正式环境强制 https）
3. Tomcat SSL 配置（`conf/server.xml`）：

```xml
<Connector port="443" protocol="org.apache.coyote.http11.Http11Protocol"
    maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
    clientAuth="false" sslProtocol="TLS"
    keystoreFile="你的keystore路径" keystorePass="生成证书时的口令" />
<!-- http 自动跳转 https：port 改 80，redirectPort 改 443 -->
<Connector connectionTimeout="20000" port="80" protocol="HTTP/1.1" redirectPort="443"/>
```

4. 如需 http 强制跳转 https，在 `conf/web.xml` 的 `<web-app>` 下加入：

```xml
<security-constraint>
    <web-resource-collection>
        <web-resource-name>SSL</web-resource-name>
        <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <user-data-constraint>
        <transport-guarantee>CONFIDENTIAL</transport-guarantee>
    </user-data-constraint>
</security-constraint>
```

> ⚠️ 上传图片保存在 `webapps/cms/img/`，重新部署 war 会被覆盖。生产环境建议将上传目录改到应用外部（修改 `ArticleController.save` 中的 `getRealPath` 逻辑并配套静态资源映射）。

## 构建与测试

```bash
cd cms-server/cms
mvn clean package     # 编译 + 单元测试（7 个用例）+ 打 war
mvn test              # 仅跑测试：Md5UtilsTest / TextUtilsTest
```

CI：GitHub Actions 在 JDK 8 / 17 矩阵下执行 `mvn -B clean package`（推送后生效，见 `.github/workflows/ci.yml`）。

## 常见问题

<details>
<summary><b>MySQL 8 连接报错？</b></summary>

`db.properties` 中驱动类改为 `com.mysql.cj.jdbc.Driver`，pom 中 `mysql-connector-java` 升到 `8.0.x`（当前 5.1.49 亦可连接 8.x）。
</details>

<details>
<summary><b>登录提示“用户名密码不存在”？</b></summary>

确认已执行 `cms.sql`；默认密码 `123456` 对应标准 MD5 `e10adc3949ba59abbe56e057f20f883e`。连续输错 5 次会锁定 10 分钟。
</details>

<details>
<summary><b>小程序图片不显示？</b></summary>

检查 `ConstantPool.IP` 是否为小程序端可访问的地址；本地调试需在开发者工具勾选「不校验合法域名」。
</details>

<details>
<summary><b>Tomcat 10 启动失败？</b></summary>

Spring 4.x 只支持 `javax.*` 命名空间，请使用 Tomcat 8/9。
</details>

<details>
<summary><b>列表里的演示文章没有图片？</b></summary>

`cms.sql` 演示数据引用的图片文件仅存在于原作者服务器。后台上传新配图即可正常显示。
</details>

<details>
<summary><b>如何用 Eclipse/IDEA 老工程方式运行？</b></summary>

Eclipse：导入 `cms-server/cms` 已有工程（`.project`/`.classpath` 保留，依赖在 `web/WEB-INF/lib/`）。IDEA：直接打开目录识别 `pom.xml` 即可。
</details>

## Python FastAPI 版本

`cms-server/cms-fastapi/` 是本分支新增的 **Python 后端实现**：FastAPI + SQLAlchemy 2.0 + PyMySQL + Jinja2，
直接复用经典版的 MySQL 库（先执行根目录 `cms.sql` 初始化即可）与全部 AdminLTE 管理页面（JSP → Jinja2 模板），
小程序接口的路径与响应形态保持一致，客户端零改动。

### 与经典版对照

| | 经典版（master） | FastAPI 版（本分支） |
| --- | --- | --- |
| 语言/框架 | Java 8 · Spring MVC 4.3 | Python 3.9+ · FastAPI |
| ORM | MyBatis | SQLAlchemy 2.0 |
| 视图 | JSP + JSTL | Jinja2（同款 AdminLTE 界面） |
| 会话 | Tomcat Session | 签名 Cookie（itsdangerous，30 分钟） |
| 运行 | war 部署到 Tomcat | `uvicorn` 直接启动 |
| 测试 | JUnit（7 用例） | pytest（9 用例） |

安全特性全部移植：图片上传扩展名白名单、登录失败限流（5 次锁 10 分钟）、XSS 转义（Jinja2 自动转义 + AJAX 行 JS 转义）、账号管理守卫（不能删自己/最后一个账号）、修改密码后强制重登、404/500 友好错误页。

### 运行步骤

```bash
cd cms-server/cms-fastapi
python -m venv .venv
.venv/Scripts/pip install -r requirements.txt     # macOS/Linux: .venv/bin/pip
# 确认 MySQL 已执行 cms.sql 初始化（默认连接 root:123456@localhost:3306/weichatarticle）
.venv/Scripts/python -m uvicorn app.main:app --host 0.0.0.0 --port 8000
# 访问 http://localhost:8000/cms/
```

### 配置（环境变量）

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `CMS_DB_URL` | `mysql+pymysql://root:123456@localhost:3306/weichatarticle?charset=utf8mb4` | 数据库连接 |
| `CMS_IP` | `http://localhost:8000/cms` | 对外地址（拼接图片完整 URL），线上改 https 域名 |
| `CMS_PREFIX` | `/cms` | 上下文路径（与经典版一致，小程序端无需改动） |
| `CMS_UPLOAD_DIR` | `./uploads` | 图片上传目录（应用外存储，`/cms/img/**` 自动映射） |
| `CMS_SESSION_SECRET` | 开发默认值 | 会话签名密钥，**生产必须设置随机值** |

### 验证情况

- pytest 单元测试 9 项全过（MD5 标准向量 + 前导零回归、中文/emoji 截断、登录限流）
- uvicorn 启动后端到端回归全过：登录/鉴权拦截（带 `/cms` 前缀）/ 四个小程序接口（字段名与 fastjson 形态逐字节一致、404 JSON）/ 后台 AJAX 分页 / 图片上传白名单拒绝 `.jsp` + 正常上传落盘并可经 `/img/**` 访问 / 修改密码全场景 / 账号增删与守卫 / 登录限流锁定 / 404 错误页 / UTF-8 字节级正确
- 浏览器截图核验：AdminLTE 界面与经典版完全一致

## 完善说明

原始项目为 2017-2018 年间的学习型项目。本版本做了系统性完善：**修复 9 处 Bug（含 MD5 前导零导致密码无法登录、缺失 JSP 页面等）、6 项安全加固（上传 RCE、XSS、登录限流、fastjson 升级等）、新增账号管理/权重排序等功能、后台全面升级 AdminLTE 界面、小程序体验优化、补齐 Maven 构建 + 单元测试 + CI**。

逐项明细见 [docs/CHANGELOG.md](docs/CHANGELOG.md)。

> 本改进版本仅供学习交流使用

## 关于作者

- 个人网站：[codercheng.xyz](https://codercheng.xyz)
- 微信公众号：**coder程**（微信搜一搜，或扫码关注）

<p align="center">
  <img src="docs/wechat-codercheng.png" width="560" alt="微信公众号：coder程">
</p>
