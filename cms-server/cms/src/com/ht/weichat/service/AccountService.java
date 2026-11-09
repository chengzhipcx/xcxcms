package com.ht.weichat.service;


import com.ht.weichat.pojo.TbAccount;

import java.util.List;

/**
 * Created by  on 16/12/13.
 */
public interface AccountService {
    public TbAccount login(String username, String password);

    void save(String username, String password) throws Exception;

    /**
     * 修改账号密码
     *
     * @param id       账号 id
     * @param password 明文新密码（内部会做 md5）
     */
    void updatePassword(Integer id, String password) throws Exception;

    /**
     * 账号列表
     */
    List<TbAccount> list();

    /**
     * 用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 删除账号
     */
    void deleteById(Integer id) throws Exception;
}
