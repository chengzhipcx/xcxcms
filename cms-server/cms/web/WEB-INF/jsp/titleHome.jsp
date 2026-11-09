<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 17/1/7
  Time: 16:22
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="head.jsp">
    <jsp:param name="active" value="type"/>
    <jsp:param name="pageTitle" value="分类管理"/>
    <jsp:param name="pageSub" value="小程序首页顶部导航标签"/>
</jsp:include>

<div class="box">
    <div class="box-header with-border">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/type/showSavaPage">＋ 增加分类</a>
    </div>

    <div class="box-body table-responsive no-padding">
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th style="width: 35%;">名称</th>
                    <th style="width: 20%;">权重</th>
                    <th style="width: 160px;">操作</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty titleList}">
                    <tr class="empty-row"><td colspan="3">暂无分类，点击上方「增加分类」创建</td></tr>
                </c:if>
                <c:forEach var="titleBean" items="${titleList}" varStatus="status">
                    <tr>
                        <td>${fn:escapeXml(titleBean.title)}</td>
                        <td>${titleBean.weight}</td>
                        <td>
                            <a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}/type/showSavaPage?id=${titleBean.id}">编辑</a>
                            <button class="btn btn-danger btn-xs" onclick="deleteById(${titleBean.id}, '${fn:escapeXml(titleBean.title)}')">删除</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script>
    function deleteById(id, title){
        var isDelete = window.confirm("确定删除「"+title+"」吗？删除后该分类下的文章仍会保留，但将无法在后台首页找到。");
        if (isDelete){
            window.location.href = "${pageContext.request.contextPath}/type/deleteById?id="+id;
        }
    }
</script>

<jsp:include page="foot.jsp"/>
