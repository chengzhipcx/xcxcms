package com.ht.weichat.service;

import com.ht.weichat.pojo.TbArticle;

import java.util.List;

/**
 * Created by  on 17/1/7.
 */
public interface ArticleService {

    public List<TbArticle> listById(int id);

    public List<TbArticle> listById(int id, int now);

    void save(TbArticle tbArticle) throws Exception;

    void deleteById(Integer id) throws Exception;

    TbArticle findById(int id);

    void update(TbArticle tbArticle);

    List<TbArticle> listBanner();

    List<TbArticle> listByIdApi(int id, int now);
}
