<%--
  Created by IntelliJ IDEA.
  User: huangtao
  Date: 17/1/7
  Time: 16:50
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="head.jsp">
    <jsp:param name="pageTitle" value="${isAddBanner == 1 ? '轮播图编辑' : '文章编辑'}"/>
</jsp:include>

<div class="row">
    <div class="col-md-8 col-md-offset-2">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${fn:escapeXml(error)}</div>
        </c:if>

        <div class="box box-primary">
            <div class="box-header with-border">
                <h3 class="box-title">${isAddBanner == 1 ? '轮播图信息' : '文章信息'}</h3>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/item/save" enctype="multipart/form-data" role="form">
                <div class="box-body">
                    <div class="form-group">
                        <label>图片（jpg / jpeg / png / gif / webp，最大 5MB）</label>
                        <input type="file" name="logo" accept="image/*" />
                        <c:if test="${not empty article.imgurl}">
                            <div style="margin-top: 8px;">
                                <img class="thumb-banner" src="${pageContext.request.contextPath}${article.imgurl}" onerror="this.style.display='none'" />
                                <span class="help-inline">当前图片，不重新上传则保留</span>
                            </div>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label>标题 <span class="text-red">*</span></label>
                        <input type="text" class="form-control" placeholder="请输入标题" name="title" value="${fn:escapeXml(article.title)}" required />
                    </div>
                    <div class="form-group">
                        <label>权重</label>
                        <input type="number" class="form-control" placeholder="留空则按时间倒序" name="weight" value="${article.weight}" style="width: 200px;" />
                        <span class="help-inline">数字越小排序越靠前</span>
                    </div>
                    <div class="form-group">
                        <label>内容</label>
                        <textarea class="form-control" name="content" rows="10" placeholder="文章正文">${article.content}</textarea>
                    </div>

                    <c:if test="${isAddBanner != 1}">
                        <div class="form-group">
                            <label>分类 <span class="text-red">*</span></label>
                            <select class="form-control" name="pid" style="width: auto;">
                                <c:forEach var="titleBean" items="${titleList}" varStatus="status">
                                    <option value="${titleBean.id}" ${article.pid == titleBean.id ? 'selected' : ''}>${fn:escapeXml(titleBean.title)}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>是否是轮播图</label>
                            <select class="form-control" name="isbanner" style="width: auto;">
                                <option value="0" ${article.isbanner ? '' : 'selected'}>否</option>
                                <option value="1" ${article.isbanner ? 'selected' : ''}>是</option>
                            </select>
                        </div>
                    </c:if>

                    <c:if test="${isAddBanner == 1}">
                        <input type="hidden" name="isbanner" value="1"/>
                    </c:if>
                    <input type="hidden" name="imgurl" value="${article.imgurl}"/>
                    <input type="hidden" name="id" value="${article.id}"/>
                </div>

                <div class="box-footer">
                    <button type="submit" class="btn btn-primary">提交</button>
                    <a class="btn btn-default" href="javascript:history.back()">返回</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="foot.jsp"/>
