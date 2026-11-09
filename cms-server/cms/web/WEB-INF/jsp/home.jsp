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
    <jsp:param name="active" value="home"/>
    <jsp:param name="pageTitle" value="文章管理"/>
    <jsp:param name="pageSub" value="按分类维护文章列表"/>
</jsp:include>

<div class="box">
    <div class="box-header with-border">
        <form class="form-inline" onsubmit="return false;">
            <label>分类：</label>
            <select id="titleSelect" class="form-control" style="width: auto;" onchange="changeTitle()">
                <c:forEach var="titleBean" items="${titleList}" varStatus="status">
                    <option value="${titleBean.id}" ${topTitle.id == titleBean.id ? 'selected' : ''}>${fn:escapeXml(titleBean.title)}</option>
                </c:forEach>
            </select>
            <a class="btn btn-primary" style="margin-left: 12px;" href="${pageContext.request.contextPath}/item/showArticleSave">＋ 增加文章</a>
        </form>
    </div>

    <div class="box-body table-responsive no-padding">
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th style="width: 90px;">图片</th>
                    <th style="width: 30%;">标题</th>
                    <th>简介</th>
                    <th style="width: 160px;">操作</th>
                </tr>
            </thead>
            <tbody id="containner">
                <c:if test="${empty itemList}">
                    <tr class="empty-row"><td colspan="4">该分类下暂无文章，点击右上角「增加文章」创建</td></tr>
                </c:if>
                <c:forEach var="itemBean" items="${itemList}" varStatus="status">
                    <tr>
                        <td><img class="thumb" src="${pageContext.request.contextPath}${itemBean.imgurl}" onerror="this.style.visibility='hidden'"></td>
                        <td>${fn:escapeXml(itemBean.title)}</td>
                        <td class="content-preview">${fn:escapeXml(itemBean.content)}</td>
                        <td>
                            <a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}/item/showArticleSave?id=${itemBean.id}">编辑</a>
                            <button class="btn btn-danger btn-xs" onclick="deleteById(${itemBean.id}, '${fn:escapeXml(itemBean.title)}')">删除</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="box-footer clearfix">
        <span class="pull-left" style="line-height: 30px;">当前第 <font id="nowPage" class="text-red">1</font> 页</span>
        <ul id="pageCntainner" class="pagination pagination-sm no-margin pull-right">
            <c:forEach begin="1" end="${totalCount}" var="index">
                <li><a onclick="page(${index})">${index}</a></li>
            </c:forEach>
        </ul>
    </div>
</div>

<script type="text/javascript">
    // 防 XSS：动态拼接 HTML 前先转义
    function esc(s){
        if (s == null) return "";
        return String(s)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function findByPid(pid, now){
        $.get(
                "${pageContext.request.contextPath}/api/listById?id="+pid+"&now="+now,
                function(data){
                    var tbodyElement = $("#containner");
                    tbodyElement.html("");

                    if (data.list.length == 0){
                        tbodyElement.html("<tr class='empty-row'><td colspan='4'>该分类下暂无文章，点击右上角「增加文章」创建</td></tr>");
                    }

                    for (var i = 0; i < data.list.length; i++) {
                        var article = data.list[i];
                        var trElement = "<tr>"
                        trElement+="<td><img class='thumb' src='${pageContext.request.contextPath}"+esc(article.imgurl)+"' onerror=\"this.style.visibility='hidden'\"/></td>";
                        trElement+="<td>"+esc(article.title)+"</td>";
                        trElement+="<td class='content-preview'>"+esc(article.content)+"</td>";
                        trElement+="<td><a class='btn btn-default btn-xs' href='${pageContext.request.contextPath}/item/showArticleSave?id="+article.id+"'>编辑</a> ";
                        var deleteBtn = "deleteById("+article.id+", '"+esc(article.title)+"')";
                        trElement+= "<button class='btn btn-danger btn-xs' onclick=\""+deleteBtn+"\">删除</button></td>";
                        tbodyElement.append(trElement);
                    }

                    var pageElement = $("#pageCntainner");
                    pageElement.html("");
                    for (var i=1; i<= data.pageCount;i++){
                        pageElement.append("<li class='"+(i==now?"active":"")+"'><a onclick='page("+i+")'>"+i+"</a></li>");
                    }
                    document.getElementById("nowPage").innerHTML = now;
                }
        );
    }

    function changeTitle(){
        findByPid(document.getElementById("titleSelect").value, 1);
    }

    function deleteById(id, title){
        var isDelete = window.confirm("确定删除「"+title+"」吗？");
        if (isDelete){
            window.location.href = "${pageContext.request.contextPath}/item/deleteById?id="+id+"&pid="+document.getElementById("titleSelect").value;
        }
    }

    findByPid(document.getElementById("titleSelect").value, 1);

    function page(now){
        findByPid(document.getElementById("titleSelect").value, now);
        document.getElementById("nowPage").innerHTML = now;
    }
</script>

<jsp:include page="foot.jsp"/>
