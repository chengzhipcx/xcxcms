<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 17/1/7
  Time: 16:50
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="head.jsp">
    <jsp:param name="active" value="type"/>
    <jsp:param name="pageTitle" value="分类编辑"/>
</jsp:include>

<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${fn:escapeXml(error)}</div>
        </c:if>

        <div class="box box-primary">
            <div class="box-header with-border">
                <h3 class="box-title">${empty type ? '增加分类' : '编辑分类'}</h3>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/type/save" role="form">
                <div class="box-body">
                    <div class="form-group">
                        <label>标题 <span class="text-red">*</span></label>
                        <input type="text" class="form-control" placeholder="请输入分类标题" name="title" value="${fn:escapeXml(type.title)}" required />
                    </div>
                    <div class="form-group">
                        <label>权重</label>
                        <input type="number" class="form-control" placeholder="数字越小排序越靠前" name="weight" value="${type.weight}" style="width: 200px;" />
                    </div>
                    <input type="hidden" name="id" value="${type.id}" />
                </div>
                <div class="box-footer">
                    <button type="submit" class="btn btn-primary">提交</button>
                    <a class="btn btn-default" href="${pageContext.request.contextPath}/type/home">返回</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="foot.jsp"/>
