package com.ht.weichat.controller.web;

import com.alibaba.fastjson.JSON;
import com.ht.weichat.pojo.TbArticle;
import com.ht.weichat.service.ArticleService;
import com.ht.weichat.utils.ConstantPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author cz
 */
@Controller
@RequestMapping("/banner/")
public class BannerController {

    @Autowired
    private ArticleService articleService;


    @RequestMapping("list")
    public void showIndex(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        List<TbArticle> tbArticles = articleService.listBanner();

        for (int i = 0; i < tbArticles.size(); i++) {
            TbArticle tbArticle = tbArticles.get(i);
            tbArticle.setImgurl(ConstantPool.IP+tbArticle.getImgurl());
        }

        try {
            response.getWriter().print(JSON.toJSONString(tbArticles));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("home")
    public String bannerHome(Model model) {
        List<TbArticle> tbArticles = articleService.listBanner();
        model.addAttribute("bannerList", tbArticles);
        return "bannerHome";
    }
}
