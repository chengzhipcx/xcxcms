<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 17/1/7
  Time: 16:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
    <style type="text/css">
        td{
            text-align: center;
        }
    </style>
</head>
<body>
    <jsp:include page="head.jsp"/><br/><br/>
    <form method="post" action="${pageContext.request.contextPath}/item/save" enctype="multipart/form-data">
        图片：<input type="file" name="logo" /><br/>
        标题：<input type="text" placeholder="标题" name="title" value="${article.title}" /><br/>
        <%--权重：<input type="number" placeholder="权重" name="weight" value="${article.weight}" /><br/>--%>
        内容：<textarea name="content" cols="200" rows="10" >${article.content}</textarea><br/>

        <c:if test="${isAddBanner != 1}">
            分类：<select name="pid">
            <c:forEach var="titleBean" items="${titleList}" varStatus="status">
                <c:if test="${article.pid == titleBean.id}">
                    <option value="${titleBean.id }" selected>${titleBean.title}</option>
                </c:if>
                <c:if test="${article.pid != titleBean.id}">
                    <option value="${titleBean.id }">${titleBean.title}</option>
                </c:if>
            </c:forEach>
            </select><br/>
            是否是轮播图：<select name="isbanner">
            <c:if test="${!article.isbanner}">
                <option value="0" selected>否</option>
            </c:if>
            <c:if test="${!article.isbanner}">
                <option value="1">是</option>
            </c:if>

            <c:if test="${article.isbanner}">
                <option value="0">否</option>
            </c:if>
            <c:if test="${article.isbanner}">
                <option value="1" selected>是</option>
            </c:if>
            </select><br/>
        </c:if>

        <c:if test="${isAddBanner == 1}">
            <input type="hidden" name="isbanner" value="1"/>
        </c:if>
        <input type="hidden" name="imgurl" value="${article.imgurl}"/>
        <input type="hidden" name="id" value="${article.id}"/>

        <input type="submit" value="提交" />
    </form>

    <div style="color: red">${error}</div>
</body>
</html>
