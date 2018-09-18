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
    <script src="js/jquery-2.2.3.min.js"></script>
</head>
<body>
    <jsp:include page="head.jsp"/><br/><br/>
    <div style="text-align: center;">
        <a href="${pageContext.request.contextPath}/item/showArticleSave?isAddBanner=1">增加轮播图</a>
    </div>

    <table cellpadding="0" cellspacing="0" border="1" width="500px" align="center">
        <caption align="top">轮播图管理系统</caption>
        <tr>
            <th>logo</th>
            <th>名称</th>
            <th>简介</th>
            <th>编辑</th>
            <th>删除</th>
        </tr>
        <tbody id="containner">
            <c:forEach var="itemBean" items="${bannerList}" varStatus="status">
                <tr>
                    <td>
                        <img style="width: 100px; height: 100px;" src="${pageContext.request.contextPath}${itemBean.imgurl}">
                    </td>
                    <td>${itemBean.title}</td>
                    <td>${itemBean.content}</td>
                    <td><a href="${pageContext.request.contextPath}/item/showArticleSave?isAddBanner=1&id=${itemBean.id}">编辑</a></td>
                    <td><button onclick="deleteById(${itemBean.id}, '${itemBean.title}')">删除</button></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>

<script type="text/javascript">

//    function findByPid(pid){
//        $.get(
//                "/item/listById?id="+pid,
//                function(data){
////                    var articleList = eval("("+data+")");
//
//                    var tbodyElement = $("#containner");
//                    tbodyElement.html("");
//
//                    for (var i = 0; i < data.length; i++) {
//                        var article = data[i];
//                        var trElement = "<tr>"
//                        trElement+="<td><img style='width: 150px; height: 150px;' src='"+article.imgurl+"'/></td>";
//                        trElement+="<td>"+article.title+"</td>";
//                        trElement+="<td>"+article.content+"</td>";
//                        trElement+="<td><a href='/item/showArticleSave?id="+article.id+"'>编辑</a></td>";
//                        var deleteBtn = "deleteById("+article.id+", '"+article.title+"')";
//                        trElement+= "<td><button onclick=\""+deleteBtn+"\">删除</button></td>";
//                        tbodyElement.append(trElement);
//                    }
//                }
//        );
//    }
//
//    function changeTitle(){
//        findByPid(document.getElementById("titleSelect").value);
//    }

    function deleteById(id, title){
        var isDelete = window.confirm("确定删除"+title+"吗");
        if (isDelete){
            window.location.href = "${pageContext.request.contextPath}/item/deleteById?id="+id;
        }
    }

//    findByPid(document.getElementById("titleSelect").value);
</script>
</html>
