package com.ht.weichat.controller.web;

import com.ht.weichat.pojo.TbArticle;
import com.ht.weichat.pojo.TbType;
import com.ht.weichat.service.ArticleService;
import com.ht.weichat.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author cz
 */
@Controller
@RequestMapping("/item/")
public class ArticleController {

    /**
     * 允许上传的图片扩展名（一律转小写比较）。
     * 必须做白名单校验：web/img 目录在应用内，若不限制扩展名，
     * 上传的 .jsp 会被 Tomcat 直接执行，造成远程代码执行漏洞。
     */
    private static final List<String> ALLOWED_IMG_EXT =
            Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");

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
                String oldName = logo.getOriginalFilename();
                String ext = "";
                if (oldName != null && oldName.lastIndexOf(".") >= 0){
                    ext = oldName.substring(oldName.lastIndexOf(".")).toLowerCase(Locale.ENGLISH);
                }
                if (!ALLOWED_IMG_EXT.contains(ext)){
                    throw new IllegalArgumentException("只支持上传图片文件（jpg/jpeg/png/gif/webp）");
                }

                String rootPath = request.getSession().getServletContext().getRealPath("/img");
                String imageName = UUID.randomUUID().toString() + ext;
                String imgUrl = rootPath + "/" + imageName;
                File file = new File(imgUrl);
                if (!file.getParentFile().exists()){
                    file.mkdirs();
                }
                logo.transferTo(file);
                tbArticle.setImgurl("/img/" + imageName);
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
            // 回填编辑页所需数据，避免错误页下拉框为空
            model.addAttribute("titleList", typeService.list());
            model.addAttribute("article", tbArticle);
            model.addAttribute("isAddBanner",
                    request.getParameter("isAddBanner") == null ? 0
                            : request.getParameter("isAddBanner"));
            model.addAttribute("error", "保存失败：" + e.getMessage());
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
