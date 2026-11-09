<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 16/12/13
  Time: 15:09
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="head.jsp">
    <jsp:param name="active" value="banner"/>
    <jsp:param name="pageTitle" value="轮播图管理"/>
    <jsp:param name="pageSub" value="小程序首页焦点图"/>
</jsp:include>

<div class="box">
    <div class="box-header with-border">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/item/showArticleSave?isAddBanner=1">＋ 增加轮播图</a>
    </div>

    <div class="box-body table-responsive no-padding">
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th style="width: 150px;">图片</th>
                    <th style="width: 25%;">名称</th>
                    <th>简介</th>
                    <th style="width: 160px;">操作</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty bannerList}">
                    <tr class="empty-row"><td colspan="4">暂无轮播图，点击上方「增加轮播图」创建</td></tr>
                </c:if>
                <c:forEach var="itemBean" items="${bannerList}" varStatus="status">
                    <tr>
                        <td><img class="thumb-banner" src="${pageContext.request.contextPath}${itemBean.imgurl}" onerror="this.style.visibility='hidden'"></td>
                        <td>${fn:escapeXml(itemBean.title)}</td>
                        <td class="content-preview">${fn:escapeXml(itemBean.content)}</td>
                        <td>
                            <a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}/item/showArticleSave?isAddBanner=1&id=${itemBean.id}">编辑</a>
                            <button class="btn btn-danger btn-xs" onclick="deleteById(${itemBean.id}, '${fn:escapeXml(itemBean.title)}')">删除</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript">
    function deleteById(id, title){
        var isDelete = window.confirm("确定删除「"+title+"」吗？");
        if (isDelete){
            window.location.href = "${pageContext.request.contextPath}/item/deleteById?id="+id;
        }
    }
</script>

<jsp:include page="foot.jsp"/>
