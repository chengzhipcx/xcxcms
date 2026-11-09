package com.ht.weichat.interceptor;

import com.ht.weichat.pojo.TbAccount;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object handler, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object handler, ModelAndView arg3)
			throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TbAccount account = (TbAccount) request.getSession().getAttribute("global.account");
		//取不到用户信息
		if (null == account) {
			//跳转到登录页面，把用户请求的url作为参数传递给登录页面。
			response.sendRedirect("/admin");
			//返回false
			return false;
		}
		return true;
	}
	
}
