<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 16/12/13
  Time: 14:03
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>CMS 后台管理 - 登录</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminLTE.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/blue.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
</head>
<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <a href="#"><b>CMS</b> 后台管理</a>
    </div>

    <div class="login-box-body">
        <c:if test="${not empty msg}">
            <p class="login-box-msg" style="color: red;">${fn:escapeXml(msg)}</p>
        </c:if>
        <c:if test="${empty msg}">
            <p class="login-box-msg">请登录后开始管理</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/login" method="post">
            <div class="form-group has-feedback">
                <input type="text" name="username" class="form-control" placeholder="用户名" required autofocus>
            </div>
            <div class="form-group has-feedback">
                <input type="password" name="password" class="form-control" placeholder="密码" required>
            </div>
            <div class="row">
                <button type="submit" class="btn btn-primary btn-block btn-flat">登 录</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
