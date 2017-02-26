package com.zhanggb.fictionread.manager.impl;

import com.zhanggb.fictionread.manager.FileManager;
import com.zhanggb.fictionread.model.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public class FileManagerImpl implements FileManager {

    public Comparator<Directory> comparator = new Comparator<Directory>() {
        @Override
        public int compare(Directory directory, Directory directory1) {
            String s1 = directory.getName().toLowerCase();
            String s2 = directory1.getName().toLowerCase();
            if (s1.charAt(0) > s2.charAt(0)) {
                return 1;
            } else if (s1.charAt(0) < s2.charAt(0)) {
                return -1;
            } else {
                String alphabet1 = s1.replaceAll("\\d+$", "");
                String alphabet2 = s2.replaceAll("\\d+$", "");
                int cmpAlphabet = alphabet1.compareToIgnoreCase(alphabet2);
                if (cmpAlphabet != 0) {
                    return cmpAlphabet;
                }
                String numeric1 = s1.replaceAll("^[a-zA-Z]+", "");
                String numeric2 = s2.replaceAll("^[a-zA-Z]+", "");
                if ("".equals(numeric1)) {
                    return -1;
                }
                if ("".equals(numeric2)) {
                    return 1;
                }
                if (!isNum(numeric1)) {
                    return -1;
                }
                if (!isNum(numeric2)) {
                    return 1;
                }
                int result = Integer.parseInt(numeric1) - Integer.parseInt(numeric2);
                return result;
            }
        }
    };

    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    @Override
    public List<Directory> findDirectoryByPath(File path) {

        List<Directory> directoryList = new ArrayList<Directory>();
        List<Directory> fileList = new ArrayList<Directory>();
        File[] files = path.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Directory directory = new Directory();
                File file = files[i];
                directory.setId(i);
                directory.setName(file.getName());
                directory.setFile(file.getAbsoluteFile());
                if (file.isDirectory()) {
                    directory.setType(Directory.Type.DIRECTORY);
                    directoryList.add(directory);
                } else {
                    directory.setType(Directory.Type.FILE);
                    fileList.add(directory);
                }
            }
        }
        Collections.sort(directoryList, comparator);
        Collections.sort(fileList, comparator);
        directoryList.addAll(fileList);
        return directoryList;
    }
}
