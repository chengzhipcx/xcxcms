package com.ht.weichat.service;

import com.ht.weichat.pojo.TbType;

import java.util.List;

/**
 * Created by  on 17/1/7.
 */
public interface TypeService {

    public List<TbType> list();

    public void saveTitle(TbType tbListTitle) throws Exception;

    public TbType findById(int id);

    void updateTitle(TbType tbListTitle);

    void deleteById(Integer id) throws Exception;
}
