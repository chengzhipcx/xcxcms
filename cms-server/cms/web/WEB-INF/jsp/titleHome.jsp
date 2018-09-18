<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 17/1/7
  Time: 16:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
    <div style="text-align: center;"><a href="${pageContext.request.contextPath}/type/showSavaPage">增加分类</a></div><br/><br/>
    <table cellpadding="0" cellspacing="0" border="1" width="500px" align="center">
        <caption align="top">分类管理系统</caption>
        <tr>
            <th>名称</th>
            <th>权重</th>
            <th>编辑</th>
            <th>删除</th>
        </tr>
        <tbody id="containner">
            <c:forEach var="titleBean" items="${titleList}" varStatus="status">
                <tr>
                    <td>${titleBean.title}</td>
                    <td>${titleBean.weight}</td>
                    <td><a href="${pageContext.request.contextPath}/type/showSavaPage?id=${titleBean.id}">编辑</a></td>
                    <td><button onclick="deleteById(${titleBean.id}, '${titleBean.title}')">删除</button></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>

<script>
    function deleteById(id, title){
        var isDelete = window.confirm("确定删除"+title+"吗");
        if (isDelete){
            window.location.href = "${pageContext.request.contextPath}/type/deleteById?id="+id;
        }
    }
</script>
</html>
