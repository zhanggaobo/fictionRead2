package com.zhanggb.fictionread.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-1-22
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public class Section implements Serializable {

    private String name;
    private Long current;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }
}
