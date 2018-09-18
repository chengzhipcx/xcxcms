<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 16/12/13
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title></title>
    <style type="text/css">
        td{
            text-align: center;
        }
    </style>
    <script src="${pageContext.request.contextPath}/js/jquery-2.2.3.min.js"></script>
</head>
<body>
    <jsp:include page="head.jsp"/><br/><br/>
    <div style="text-align: center;">

        <select id="titleSelect" onchange="changeTitle()">
            <c:forEach var="titleBean" items="${titleList}" varStatus="status">

                <c:if test="${topTitle.id == titleBean.id}">
                    <option value="${titleBean.id }" selected>${titleBean.title}</option>
                </c:if>
                <c:if test="${topTitle.id != titleBean.id}">
                    <option value="${titleBean.id }" >${titleBean.title}</option>
                </c:if>
            </c:forEach>
        </select>

        <a href="${pageContext.request.contextPath}/item/showArticleSave">增加文章</a>
    </div>
    <table cellpadding="0" cellspacing="0" border="1" width="500px" align="center" style="margin-top: 20px;">
        <caption align="top">文章管理系统</caption>
        <tr>
            <th>logo</th>
            <th>名称</th>
            <th>简介</th>
            <th>编辑</th>
            <th>删除</th>
        </tr>
        <tbody id="containner">
            <c:forEach var="itemBean" items="${itemList}" varStatus="status">
                <tr>
                    <td>
                        <img style="width: 100px; height: 100px;" src="${itemBean.imgurl}">
                    </td>
                    <td>${itemBean.title}</td>
                    <td>${itemBean.content}</td>
                    <td><a href="${pageContext.request.contextPath}/item/showArticleSave?id=${itemBean.id}">编辑</a></td>
                    <td><button onclick="deleteById(${itemBean.id}, '${itemBean.title}')">删除</button></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div id="pageCntainner" style="text-align: center; margin-top: 20px;">
        <c:forEach begin="1" end="${totalCount}" var="index">
            <a onclick="page(${index})">${index}&nbsp;&nbsp;&nbsp;&nbsp;</a>
        </c:forEach>

        当前页面第<font id="nowPage" style="color: red;">1</font>页面
    </div>
</body>

<script type="text/javascript">
    function findByPid(pid, now){
        $.get(
                "${pageContext.request.contextPath}/api/listById?id="+pid+"&now="+now,
                function(data){
//                    var articleList = eval("("+data+")");

                    var tbodyElement = $("#containner");
                    tbodyElement.html("");

                    for (var i = 0; i < data.list.length; i++) {
                        var article = data.list[i];
                        var trElement = "<tr>"
                        trElement+="<td><img style='width: 100px; height: 100px;' src='${pageContext.request.contextPath}"+article.imgurl+"'/></td>";
                        trElement+="<td>"+article.title+"</td>";
                        trElement+="<td>"+article.content+"</td>";
                        trElement+="<td><a href='${pageContext.request.contextPath}/item/showArticleSave?id="+article.id+"'>编辑</a></td>";
                        var deleteBtn = "deleteById("+article.id+", '"+article.title+"')";
                        trElement+= "<td><button onclick=\""+deleteBtn+"\">删除</button></td>";
                        tbodyElement.append(trElement);
                    }



                    var pageInfo = "";
                    for (var i=1; i<= data.pageCount;i++){
                        pageInfo += "<a onclick='page("+i+")'>"+i+"&nbsp;&nbsp;&nbsp;&nbsp;</a>";
                    }

                    document.getElementById("pageCntainner").innerHTML=pageInfo+"当前页面第<font id=\"nowPage\" style=\"color: red;\">"+now+"</font>页面";
                }
        );
    }

    function changeTitle(){
        findByPid(document.getElementById("titleSelect").value, 1);
    }

    function deleteById(id, title){
        var isDelete = window.confirm("确定删除"+title+"吗");
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
</html>
