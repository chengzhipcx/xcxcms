<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 17/1/7
  Time: 16:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
    <jsp:include page="head.jsp"/><br/><br/>
    <form method="post" action="${pageContext.request.contextPath}/type/save">
        标题：<input type="text" placeholder="标题" name="title" value="${type.title}" /><br/>
        权重：<input type="number" placeholder="权重" name="weight" value="${type.weight}" /><br/>
        <input type="hidden" name="id" value="${type.id}" />
        <input type="submit" value="提交" />
    </form>

    <div style="color: red">${error}</div>
</body>
</html>
