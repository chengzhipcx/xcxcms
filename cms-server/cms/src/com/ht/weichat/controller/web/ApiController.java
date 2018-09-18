package com.ht.weichat.controller.web;

import com.alibaba.fastjson.JSON;
import com.ht.weichat.pojo.ListByIdBean;
import com.ht.weichat.pojo.TbArticle;
import com.ht.weichat.pojo.TbType;
import com.ht.weichat.service.ArticleService;
import com.ht.weichat.service.TypeService;
import com.ht.weichat.utils.ConstantPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author cz
 */
@Controller
@RequestMapping("/api/")
public class ApiController {

	@Autowired
	private ArticleService articleService;

	@RequestMapping("findByIdApi")
	public void findByIdApi(HttpServletResponse response, int id) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		TbArticle tbArticle = articleService.findById(id);

		SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
		tbArticle.setDate(simpleFormatter.format(tbArticle.getUpdattime()));

		tbArticle.setImgurl(ConstantPool.IP + tbArticle.getImgurl());

		try {
			response.getWriter().print(JSON.toJSONString(tbArticle));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("list")
	public void showIndex(HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		List<TbArticle> tbArticles = articleService.listBanner();

		for (int i = 0; i < tbArticles.size(); i++) {
			TbArticle tbArticle = tbArticles.get(i);
			tbArticle.setImgurl(ConstantPool.IP + tbArticle.getImgurl());
		}

		try {
			response.getWriter().print(JSON.toJSONString(tbArticles));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	private TypeService tbListTitleService;

	@RequestMapping("listApiType")
	public void listApi(HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		List<TbType> list = tbListTitleService.list();

		try {
			response.getWriter().print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "listById")
	public void listById(int id, HttpServletResponse response,
			@RequestParam(defaultValue = "1") int now) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		try {
			List<TbArticle> tbArticleList = articleService.listById(id, now);

			for (int i = 0; i < tbArticleList.size(); i++) {
				TbArticle tbArticle = tbArticleList.get(i);
				if (tbArticle.getContent() != null
						&& tbArticle.getContent().length() > 50) {
					tbArticle.setContent(tbArticle.getContent()
							.substring(0, 50));
				}
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

	@RequestMapping(value = "listApiArticle")
	public void listApiArticle(int id, HttpServletResponse response,
			@RequestParam(defaultValue = "0") int loadTime) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		try {
			List<TbArticle> tbArticleList = articleService.listByIdApi(id,
					loadTime + 1);

			for (int i = 0; i < tbArticleList.size(); i++) {
				TbArticle tbArticle = tbArticleList.get(i);
				if (tbArticle.getContent() != null
						&& tbArticle.getContent().length() > 50) {
					tbArticle.setContent(tbArticle.getContent()
							.substring(0, 50));
				}

				tbArticle.setImgurl(ConstantPool.IP + tbArticle.getImgurl());
			}

			response.getWriter().print(JSON.toJSONString(tbArticleList));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
