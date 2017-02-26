package com.zhanggb.fictionread.model;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
public class Directory {
    public static enum Type {
        UNKNOWN, DIRECTORY, FILE;
    }

    private String name;
    private Type type;
    private int id;
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
