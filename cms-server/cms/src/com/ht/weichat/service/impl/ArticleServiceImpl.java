package com.ht.weichat.service.impl;

import com.ht.weichat.mapper.TbArticleMapper;
import com.ht.weichat.pojo.TbArticle;
import com.ht.weichat.pojo.TbArticleExample;
import com.ht.weichat.service.ArticleService;
import com.ht.weichat.utils.ConstantPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by  on 17/1/7.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private TbArticleMapper tbArticleMapper;

    @Override
    public List<TbArticle> listById(int id) {


        TbArticleExample example = new TbArticleExample();
        TbArticleExample.Criteria criteria = example.createCriteria();
        criteria.andPidEqualTo(id).andIsbannerEqualTo(false);
        return tbArticleMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<TbArticle> listById(int id, int now) {
        return tbArticleMapper.listApi(id, (now - 1) * ConstantPool.PAGESIZE, ConstantPool.PAGESIZE);
    }

    @Override
    public void save(TbArticle tbArticle) throws Exception {

        Date currentDate = new Date();
        tbArticle.setCreattime(currentDate);
        tbArticle.setUpdattime(currentDate);

        tbArticleMapper.insert(tbArticle);
    }

    @Override
    public void deleteById(Integer id) throws Exception {
        tbArticleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public TbArticle findById(int id) {
        return tbArticleMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbArticle tbArticle) {
        Date currentDate = new Date();
        tbArticle.setUpdattime(currentDate);
        tbArticleMapper.updateByPrimaryKeyWithBLOBs(tbArticle);
    }

    @Override
    public List<TbArticle> listBanner() {
        TbArticleExample example = new TbArticleExample();
        TbArticleExample.Criteria criteria = example.createCriteria();
        criteria.andIsbannerEqualTo(true);
        return tbArticleMapper.selectByExample(example);
    }

    @Override
    public List<TbArticle> listByIdApi(int id, int now) {

        TbArticleExample example = new TbArticleExample();
        TbArticleExample.Criteria criteria = example.createCriteria();
        criteria.andPidEqualTo(id).andIsbannerEqualTo(false);

        return tbArticleMapper.listApi(id, (now - 1) * 15, 15);
    }
}
