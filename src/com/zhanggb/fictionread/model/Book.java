package com.zhanggb.fictionread.model;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
public class Book {
    private int id;
    private String name;
    private String file;
    private String author;
    private long lastTime;
    private String percent;
    public long beginRead;

    public long getBeginRead() {
        return beginRead;
    }

    public void setBeginRead(long beginRead) {
        this.beginRead = beginRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
