package com.zhanggb.fictionread.manager;

import com.zhanggb.fictionread.model.Book;
import com.zhanggb.fictionread.model.Section;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-22
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public interface BookManager {

    List<Book> findBooks();

    Book findBookById(int id);

    void updateBook(int id, Book book);

    long insertBook(Book book);

    void deleteBookById(int id);

    void deleteBookFileByFile(String file);


    List<Section> findSectionDirectory(String path);

    void insertSections(List<Section> sections, String file);

    List<Section> findSections(String file);
}
