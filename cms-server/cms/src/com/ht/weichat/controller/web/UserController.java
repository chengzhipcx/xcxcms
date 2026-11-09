package com.ht.weichat.controller.web;

import com.ht.weichat.pojo.TbAccount;
import com.ht.weichat.service.AccountService;
import com.ht.weichat.utils.LoginRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        // 简单限流：同一 用户名+IP 连续失败 5 次锁定 10 分钟
        String limiterKey = username + "|" + request.getRemoteAddr();
        if (LoginRateLimiter.isLocked(limiterKey)){
            model.addAttribute("msg", "登录失败次数过多，请 10 分钟后再试");
            return "login";
        }

        TbAccount tbAccount = accountService.login(username, password);
        if (tbAccount != null){
            LoginRateLimiter.onSuccess(limiterKey);
            request.getSession().setAttribute("global.account", tbAccount);
            return "redirect:/home";
        } else {
            LoginRateLimiter.onFailure(limiterKey);
            model.addAttribute("msg", "用户名密码不存在,请重新登陆");
            return "login";
        }
    }

    @RequestMapping("home")
    public String home(Model model, HttpServletRequest request){
        TbAccount account = currentAccount(request);
        if (account == null){
            return "redirect:/admin";
        }
        model.addAttribute("account", account);
        model.addAttribute("accountList", accountService.list());
        return "userHome";
    }

    /**
     * 修改当前登录账号密码
     */
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword,
            String confirmPassword, Model model, HttpServletRequest request){
        TbAccount account = currentAccount(request);
        if (account == null){
            return "redirect:/admin";
        }

        if (newPassword == null || newPassword.trim().length() < 6){
            model.addAttribute("msg", "新密码长度不能少于 6 位");
            model.addAttribute("account", account);
            model.addAttribute("accountList", accountService.list());
            return "userHome";
        }

        if (!newPassword.equals(confirmPassword)){
            model.addAttribute("msg", "两次输入的新密码不一致");
            model.addAttribute("account", account);
            model.addAttribute("accountList", accountService.list());
            return "userHome";
        }

        if (accountService.login(account.getUsername(), oldPassword) == null){
            model.addAttribute("msg", "原密码不正确");
            model.addAttribute("account", account);
            model.addAttribute("accountList", accountService.list());
            return "userHome";
        }

        try {
            accountService.updatePassword(account.getId(), newPassword);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "密码修改失败，请稍后重试");
            model.addAttribute("account", account);
            model.addAttribute("accountList", accountService.list());
            return "userHome";
        }

        // 密码已变更，强制重新登录
        request.getSession().removeAttribute("global.account");
        model.addAttribute("msg", "密码修改成功，请使用新密码重新登录");
        return "login";
    }

    /**
     * 新增后台账号
     */
    @RequestMapping(value = "addAccount", method = RequestMethod.POST)
    public String addAccount(String username, String password, String confirmPassword,
            Model model, HttpServletRequest request){
        TbAccount account = currentAccount(request);
        if (account == null){
            return "redirect:/admin";
        }

        String failMsg = validateNewAccount(username, password, confirmPassword);
        if (failMsg == null && accountService.existsByUsername(username)){
            failMsg = "用户名已存在";
        }

        if (failMsg != null){
            model.addAttribute("account", account);
            model.addAttribute("accountList", accountService.list());
            model.addAttribute("msg", failMsg);
            return "userHome";
        }

        try {
            accountService.save(username.trim(), password);
            model.addAttribute("msg", "账号 " + username.trim() + " 创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "账号创建失败，请稍后重试");
        }

        model.addAttribute("account", account);
        model.addAttribute("accountList", accountService.list());
        return "userHome";
    }

    /**
     * 删除账号；不能删除自己，也不能删除最后一个账号
     */
    @RequestMapping("deleteAccount")
    public String deleteAccount(Integer id, Model model, HttpServletRequest request){
        TbAccount account = currentAccount(request);
        if (account == null){
            return "redirect:/admin";
        }

        List<TbAccount> accountList = accountService.list();
        if (accountList.size() <= 1){
            model.addAttribute("msg", "至少保留一个账号，无法删除");
        } else if (account.getId().equals(id)){
            model.addAttribute("msg", "不能删除当前登录的账号");
        } else {
            try {
                accountService.deleteById(id);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("msg", "删除失败，请稍后重试");
            }
        }

        model.addAttribute("account", account);
        model.addAttribute("accountList", accountService.list());
        return "userHome";
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("global.account");
        return "redirect:/admin";
    }

    private TbAccount currentAccount(HttpServletRequest request){
        return (TbAccount) request.getSession().getAttribute("global.account");
    }

    /**
     * @return 校验失败返回错误信息，通过返回 null
     */
    private String validateNewAccount(String username, String password, String confirmPassword){
        if (username == null || username.trim().length() < 4 || username.trim().length() > 20){
            return "用户名长度需在 4 ~ 20 位之间";
        }
        if (password == null || password.trim().length() < 6){
            return "密码长度不能少于 6 位";
        }
        if (!password.equals(confirmPassword)){
            return "两次输入的密码不一致";
        }
        return null;
    }
}
