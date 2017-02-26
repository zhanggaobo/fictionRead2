package com.zhanggb.fictionread.manager;


import com.zhanggb.fictionread.model.Directory;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public interface FileManager {
    List<Directory> findDirectoryByPath(File file);
}
