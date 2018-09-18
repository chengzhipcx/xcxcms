<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 16/12/13
  Time: 14:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>登录页面</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminLTE.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/blue.css">
</head>
<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <a href="#">后台管理</a>
    </div>

    <div class="login-box-body">
        <p class="login-box-msg" style="color: red;">${msg}</p>
        <form action="${pageContext.request.contextPath}/user/login" method="post">
            <input type="hidden" name="msg" id="msg" class="form-control" value="">
            <div class="form-group has-feedback">
                <input type="text" name="username" class="form-control" placeholder="用户名">
            </div>
            <div class="form-group has-feedback">
                <input type="password" name="password" class="form-control" placeholder="密码">
            </div>
            <div class="row">
                <button type="submit" class="btn btn-primary btn-block btn-flat">登录</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>