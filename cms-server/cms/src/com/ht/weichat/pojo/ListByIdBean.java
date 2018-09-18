package com.ht.weichat.pojo;

import java.util.List;

/**
 * Created by  on 17/1/10.
 */
public class ListByIdBean {
    private List<TbArticle> list;

    private int pageCount;

    public List<TbArticle> getList() {
        return list;
    }

    public void setList(List<TbArticle> list) {
        this.list = list;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
