/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

/**
 *
 * @author dubin
 */
public class FileItemModel {
    private final ObservableList<FileItem> list = FXCollections.observableArrayList();
    private final FilteredList<FileItem> fList = new FilteredList<>(list);
    private final SortedList<FileItem> sList = new SortedList<>(fList);

//    private final ObjectProperty<FileItem> currentFileItem = new SimpleObjectProperty<>();

//    public FileItem getCurrentFileItem() {
//        return currentFileItem.get();
//    }

//    public void setCurrentFileItem(FileItem value) {
//        currentFileItem.set(value);
//    }
//
//    public ObjectProperty currentFileItemProperty() {
//        return currentFileItem;
//    }
    
    public ObservableList<FileItem> getList() {
        return list;
    }
    
    public FilteredList<FileItem> getFilteredList() {
        return fList;
    }
    
    public SortedList<FileItem> getSortedList() {
        return sList;
    }
    
    public void clearSelected(List<FileItem> list) {
        
    }
    
    

//    public void load(Path p) throws IOException{
//        
//    }
//    
//    public void load(FileItem fi) throws IOException {
//        load(fi.getPath());
//    }
}
