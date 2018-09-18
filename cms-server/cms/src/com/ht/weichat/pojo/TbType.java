package com.ht.weichat.pojo;

import java.util.Date;

public class TbType {
    private Integer id;

    private String title;

    private String weight;

    private Date updattime;

    private Date creattime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight == null ? null : weight.trim();
    }

    public Date getUpdattime() {
        return updattime;
    }

    public void setUpdattime(Date updattime) {
        this.updattime = updattime;
    }

    public Date getCreattime() {
        return creattime;
    }

    public void setCreattime(Date creattime) {
        this.creattime = creattime;
    }
}