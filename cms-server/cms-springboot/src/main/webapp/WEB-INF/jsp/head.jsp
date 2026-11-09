<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>${empty param.pageTitle ? 'CMS 后台' : param.pageTitle} - CMS 后台管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminLTE.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/blue.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <script src="${pageContext.request.contextPath}/js/jquery-2.2.3.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <header class="main-header">
        <a href="${pageContext.request.contextPath}/home" class="logo">
            <span class="logo-mini"><b>C</b>MS</span>
            <span class="logo-lg"><b>CMS</b> 后台管理</span>
        </a>
        <nav class="navbar navbar-static-top">
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <i class="ico">👤</i>
                            <span class="hidden-xs">${fn:escapeXml(sessionScope['global.account'].username)}</span>
                            <i class="caret"></i>
                        </a>
                        <ul class="dropdown-menu">
                            <li class="header" style="text-align:center;">${fn:escapeXml(sessionScope['global.account'].username)}</li>
                            <li><a href="${pageContext.request.contextPath}/user/home"><i class="ico">🔑</i> 账号管理 / 修改密码</a></li>
                            <li class="divider"></li>
                            <li><a href="${pageContext.request.contextPath}/user/logout"><i class="ico">🚪</i> 退出登录</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
    </header>

    <aside class="main-sidebar">
        <section class="sidebar">
            <ul class="sidebar-menu">
                <li class="header">内容管理</li>
                <li class="${param.active == 'home' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/home"><i class="ico">📝</i><span>文章管理</span></a>
                </li>
                <li class="${param.active == 'banner' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/banner/home"><i class="ico">🖼️</i><span>轮播图管理</span></a>
                </li>
                <li class="${param.active == 'type' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/type/home"><i class="ico">🗂️</i><span>分类管理</span></a>
                </li>
                <li class="header">系统设置</li>
                <li class="${param.active == 'user' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/user/home"><i class="ico">👤</i><span>账号管理</span></a>
                </li>
            </ul>
        </section>
    </aside>

    <div class="content-wrapper">
        <section class="content-header">
            <h1>${empty param.pageTitle ? 'CMS 后台' : param.pageTitle}
                <small>${empty param.pageSub ? '' : param.pageSub}</small>
            </h1>
        </section>
        <section class="content">
