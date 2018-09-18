package com.ht.weichat.controller.web;

import com.ht.weichat.pojo.TbAccount;
import com.ht.weichat.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cz
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String username, String password, Model model, HttpServletRequest request){
        TbAccount tbAccount = accountService.login(username, password);
        if (tbAccount != null){
            request.getSession().setAttribute("global.account", tbAccount);
            return "redirect:/home";
        } else {
            model.addAttribute("msg", "用户名密码不存在,请重新登陆");
            return "login";
        }
    }

    @RequestMapping("home")
    public String home(){
        return "userHome";
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("global.account");
        return "redirect:/admin";
    }
}
