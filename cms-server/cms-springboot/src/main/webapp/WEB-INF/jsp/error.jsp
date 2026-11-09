<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>出错了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
</head>
<body style="background-color: #f5f5f5;">
    <div class="error-page-wrap">
        <div class="code">:-(</div>
        <h2>页面开小差了</h2>
        <p>
            <%
                Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
                if (statusCode != null && statusCode == 404) {
                    out.print("您访问的页面不存在（404）");
                } else {
                    out.print("服务器开小差了，请稍后重试（500）");
                }
            %>
        </p>
        <p style="margin-top: 20px;">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin">返回后台登录</a>
        </p>
    </div>
</body>
</html>
