package com.ht.weichat.service;


import com.ht.weichat.pojo.TbAccount;

/**
 * Created by  on 16/12/13.
 */
public interface AccountService {
    public TbAccount login(String username, String password);

    void save(String username, String password) throws Exception;
}
