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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

/**
 * @author cz
 */
@Controller
@RequestMapping("/item/")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TypeService typeService;



    @RequestMapping("showArticleSave")
    public String show(
            Model model,
            @RequestParam(defaultValue = "0") int id,
            @RequestParam(defaultValue = "0") int isAddBanner){
        List<TbType> titleList = typeService.list();

        if (titleList.size() == 0){
            return "redirect:/type/showSavaPage";
        }

        model.addAttribute("titleList", titleList);


        if ( id != 0 ){
            TbArticle tbArticle = articleService.findById(id);
            model.addAttribute("article", tbArticle);
        }

        model.addAttribute("isAddBanner", isAddBanner);

        return "articleEdit";
    }

    @RequestMapping("save")
    public String save(
            @ModelAttribute TbArticle tbArticle,
            Model model,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            HttpServletRequest request){
        try {
            if (logo != null && !logo.isEmpty()){
                String rootPath = request.getSession().getServletContext().getRealPath("/img");
                String oldName = logo.getOriginalFilename();
                String imageName = UUID.randomUUID().toString()+oldName.substring(oldName.lastIndexOf("."));
                String imgUrl = rootPath + "/" + imageName;
                File file = new File(imgUrl);
                if (!file.getParentFile().exists()){
                    file.mkdirs();
                }
                logo.transferTo(file);
                tbArticle.setImgurl("/img/"+imageName);
            }


            if (tbArticle.getId() != null && tbArticle.getId() != 0){
                articleService.update(tbArticle);
            } else {
                articleService.save(tbArticle);
            }

            if (tbArticle.getPid() == null){
                return "redirect:/banner/home";
            }

            return "redirect:/home?id="+tbArticle.getPid();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("article", tbArticle);
            model.addAttribute("error", "插入或更新失败 "+e.getMessage()+"");
            return "articleEdit";
        }
    }

    @RequestMapping("deleteById")
    public String deleteById(Integer id, Integer pid){
        try {
            articleService.deleteById(id);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (pid == null){
            pid = 0;
        }

        return "redirect:/home?id="+pid;
    }

}
