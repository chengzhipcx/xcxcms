package com.ht.weichat.service.impl;

import com.ht.weichat.mapper.TbTypeMapper;
import com.ht.weichat.pojo.TbType;
import com.ht.weichat.pojo.TbTypeExample;
import com.ht.weichat.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by  on 17/1/7.
 */
@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TbTypeMapper tbTypeMapper;

    @Override
    public List<TbType> list() {
        TbTypeExample example = new TbTypeExample();
        example.setOrderByClause("weight");
        return tbTypeMapper.selectByExample(example);
    }

    @Override
    public void saveTitle(TbType tbListTitle) throws Exception {
        Date currentDate = new Date();
        tbListTitle.setCreattime(currentDate);
        tbListTitle.setUpdattime(currentDate);
        tbTypeMapper.insert(tbListTitle);
    }

    @Override
    public TbType findById(int id) {
        return tbTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateTitle(TbType tbListTitle) {
        tbTypeMapper.updateByPrimaryKey(tbListTitle);
    }

    @Override
    public void deleteById(Integer id) throws Exception{
        tbTypeMapper.deleteByPrimaryKey(id);
    }


}
