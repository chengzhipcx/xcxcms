package com.ht.weichat.service.impl;

import com.ht.weichat.mapper.TbAccountMapper;
import com.ht.weichat.pojo.TbAccount;
import com.ht.weichat.pojo.TbAccountExample;
import com.ht.weichat.service.AccountService;
import com.ht.weichat.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * Created by  on 16/12/13.
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private TbAccountMapper tbAccountMapper;

    @Override
    public TbAccount login(String username, String password) {
        TbAccountExample example = new TbAccountExample();
        TbAccountExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username).andPasswordEqualTo(Md5Utils.encode(password));
        List<TbAccount> list = tbAccountMapper.selectByExample(example);
        if (list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void save(String username, String password) throws Exception{
        TbAccount tbAccount = new TbAccount();
        tbAccount.setUsername(username);
        tbAccount.setPassword(Md5Utils.encode(password));

        Date currentDate = new Date();
        tbAccount.setCreattime(currentDate);
        tbAccount.setUpdattime(currentDate);

        tbAccountMapper.insert(tbAccount);
    }
}
