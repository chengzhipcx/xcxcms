package com.ht.weichat.controller.web;

import com.alibaba.fastjson.JSON;
import com.ht.weichat.pojo.ListByIdBean;
import com.ht.weichat.pojo.TbArticle;
import com.ht.weichat.pojo.TbType;
import com.ht.weichat.service.ArticleService;
import com.ht.weichat.service.TypeService;
import com.ht.weichat.utils.ConstantPool;
import com.ht.weichat.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 微信小程序数据接口
 *
 * @author cz
 */
@Controller
@RequestMapping("/api/")
public class ApiController {

	@Autowired
	private ArticleService articleService;

	/**
	 * 文章详情
	 */
	@RequestMapping("findByIdApi")
	public void findByIdApi(HttpServletResponse response, int id) {
		try {
			doFindByIdApi(response, id);
		} catch (Exception e) {
			writeServerError(response, e);
		}
	}

	private void doFindByIdApi(HttpServletResponse response, int id) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		TbArticle tbArticle = articleService.findById(id);

		try {
			if (tbArticle == null) {
				// 文章不存在或已被删除，返回 404，避免空指针导致 500
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().print("{\"error\":\"article not found\"}");
				return;
			}

			SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (tbArticle.getUpdattime() != null) {
				tbArticle.setDate(simpleFormatter.format(tbArticle.getUpdattime()));
			}
			tbArticle.setImgurl(absoluteImgUrl(tbArticle.getImgurl()));

			response.getWriter().print(JSON.toJSONString(tbArticle));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 轮播图列表
	 */
	@RequestMapping("list")
	public void showIndex(HttpServletResponse response) {
		try {
			doListBanner(response);
		} catch (Exception e) {
			writeServerError(response, e);
		}
	}

	private void doListBanner(HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		List<TbArticle> tbArticles = articleService.listBanner();

		for (int i = 0; i < tbArticles.size(); i++) {
			TbArticle tbArticle = tbArticles.get(i);
			tbArticle.setImgurl(absoluteImgUrl(tbArticle.getImgurl()));
		}

		try {
			response.getWriter().print(JSON.toJSONString(tbArticles));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	private TypeService tbListTitleService;

	/**
	 * 文章分类列表
	 */
	@RequestMapping("listApiType")
	public void listApi(HttpServletResponse response) {
		try {
			doListType(response);
		} catch (Exception e) {
			writeServerError(response, e);
		}
	}

	private void doListType(HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		List<TbType> list = tbListTitleService.list();

		try {
			response.getWriter().print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 后台管理页按分类分页查询文章列表
	 */
	@RequestMapping(value = "listById")
	public void listById(int id, HttpServletResponse response,
			@RequestParam(defaultValue = "1") int now) {
		try {
			doListById(id, response, now);
		} catch (Exception e) {
			writeServerError(response, e);
		}
	}

	private void doListById(int id, HttpServletResponse response, int now) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		try {
			List<TbArticle> tbArticleList = articleService.listById(id, now);

			for (int i = 0; i < tbArticleList.size(); i++) {
				TbArticle tbArticle = tbArticleList.get(i);
				tbArticle.setContent(TextUtils.truncate(tbArticle.getContent(), 50));
			}

			ListByIdBean listByIdBean = new ListByIdBean();
			listByIdBean.setList(tbArticleList);

			List<TbArticle> countList = articleService.listById(id);
			int pageSize = countList.size() / ConstantPool.PAGESIZE;
			listByIdBean
					.setPageCount(countList.size() % ConstantPool.PAGESIZE == 0 ? pageSize
							: pageSize + 1);

			response.getWriter().print(JSON.toJSONString(listByIdBean));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 小程序按分类分页查询文章列表（loadTime 从 0 开始，每次上拉加载 +1）
	 */
	@RequestMapping(value = "listApiArticle")
	public void listApiArticle(int id, HttpServletResponse response,
			@RequestParam(defaultValue = "0") int loadTime) {
		try {
			doListApiArticle(id, response, loadTime);
		} catch (Exception e) {
			writeServerError(response, e);
		}
	}

	private void doListApiArticle(int id, HttpServletResponse response, int loadTime) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		try {
			List<TbArticle> tbArticleList = articleService.listByIdApi(id,
					loadTime + 1);

			for (int i = 0; i < tbArticleList.size(); i++) {
				TbArticle tbArticle = tbArticleList.get(i);
				tbArticle.setContent(TextUtils.truncate(tbArticle.getContent(), 50));
				tbArticle.setImgurl(absoluteImgUrl(tbArticle.getImgurl()));
			}

			response.getWriter().print(JSON.toJSONString(tbArticleList));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 统一的服务端异常输出：JSON 格式 500，避免向前端暴露堆栈页
	 */
	private void writeServerError(HttpServletResponse response, Exception e) {
		e.printStackTrace();
		try {
			if (!response.isCommitted()) {
				response.reset();
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json;charset=UTF-8");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().print("{\"error\":\"server error\"}");
			}
		} catch (IOException ignore) {
			// 响应已提交或客户端断开，无法再输出
		}
	}

	/**
	 * 把图片相对路径拼成完整 URL；空值或已是完整 URL 时原样返回
	 */
	private String absoluteImgUrl(String imgurl) {
		if (imgurl == null || imgurl.startsWith("http://")
				|| imgurl.startsWith("https://")) {
			return imgurl;
		}
		return ConstantPool.IP + imgurl;
	}
}
