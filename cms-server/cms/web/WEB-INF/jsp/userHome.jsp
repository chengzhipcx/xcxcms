<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="head.jsp">
    <jsp:param name="active" value="user"/>
    <jsp:param name="pageTitle" value="账号管理"/>
    <jsp:param name="pageSub" value="后台账号与密码"/>
</jsp:include>

<c:if test="${not empty msg}">
    <c:choose>
        <c:when test="${fn:contains(msg, '成功')}">
            <div class="alert alert-success">${fn:escapeXml(msg)}</div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger">${fn:escapeXml(msg)}</div>
        </c:otherwise>
    </c:choose>
</c:if>

<div class="row">
    <div class="col-md-7">
        <div class="box">
            <div class="box-header with-border"><h3 class="box-title">账号列表</h3></div>
            <div class="box-body table-responsive no-padding">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th style="width: 35%;">用户名</th>
                            <th>创建时间</th>
                            <th style="width: 100px;">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${accountList}">
                            <tr>
                                <td>
                                    ${fn:escapeXml(item.username)}
                                    <c:if test="${account.id == item.id}"><span class="label label-primary">当前登录</span></c:if>
                                </td>
                                <td><fmt:formatDate value="${item.creattime}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td>
                                    <c:if test="${account.id != item.id}">
                                        <button class="btn btn-danger btn-xs" onclick="deleteAccount(${item.id}, '${fn:escapeXml(item.username)}')">删除</button>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="box box-primary">
            <div class="box-header with-border"><h3 class="box-title">新增账号</h3></div>
            <form method="post" action="${pageContext.request.contextPath}/user/addAccount" role="form">
                <div class="box-body">
                    <div class="form-group">
                        <label>用户名（4 ~ 20 位）</label>
                        <input type="text" class="form-control" name="username" required minlength="4" maxlength="20" />
                    </div>
                    <div class="form-group">
                        <label>密码（不少于 6 位）</label>
                        <input type="password" class="form-control" name="password" required minlength="6" />
                    </div>
                    <div class="form-group">
                        <label>确认密码</label>
                        <input type="password" class="form-control" name="confirmPassword" required minlength="6" />
                    </div>
                </div>
                <div class="box-footer">
                    <button type="submit" class="btn btn-primary">创建账号</button>
                </div>
            </form>
        </div>
    </div>

    <div class="col-md-5">
        <div class="box box-warning">
            <div class="box-header with-border"><h3 class="box-title">修改当前账号（${fn:escapeXml(account.username)}）密码</h3></div>
            <form method="post" action="${pageContext.request.contextPath}/user/changePassword" role="form">
                <div class="box-body">
                    <div class="form-group">
                        <label>原密码</label>
                        <input type="password" class="form-control" name="oldPassword" required />
                    </div>
                    <div class="form-group">
                        <label>新密码（不少于 6 位）</label>
                        <input type="password" class="form-control" name="newPassword" required minlength="6" />
                    </div>
                    <div class="form-group">
                        <label>确认新密码</label>
                        <input type="password" class="form-control" name="confirmPassword" required minlength="6" />
                    </div>
                    <p class="text-muted">修改成功后将强制退出，请使用新密码重新登录。</p>
                </div>
                <div class="box-footer">
                    <button type="submit" class="btn btn-warning">提交修改</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function deleteAccount(id, username){
        if (window.confirm("确定删除账号「"+username+"」吗？")){
            window.location.href = "${pageContext.request.contextPath}/user/deleteAccount?id="+id;
        }
    }
</script>

<jsp:include page="foot.jsp"/>
