/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.table;

import org.dy.fc.FileItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author yuri
 */
public abstract class AbstractTableViewFileItem extends TableView<FileItem>{
    
    private ObservableList<FileItem> list;// = FXCollections.observableArrayList(new ArrayList<FileItem>());
    private final FilteredList<FileItem> fList;// = new FilteredList<>(list);
    private final SortedList<FileItem> sList;// = new SortedList<>(fList);
    
    private FileItem savedSelectionFileItem;
    private int savedSelectionRowIndex;
    private boolean isSaveSelectionFileItem = false;
    
    private final BooleanProperty showHidden = new SimpleBooleanProperty(true);

    public AbstractTableViewFileItem(ObservableList<FileItem> l) {
        list = l;
        fList = new FilteredList<>(list);
        sList = new SortedList<>(fList);
        preInitTable();
        initTable();
        postInitTable();
    }
    
    
    
    
    public void addColumn(String header, boolean isSortable,  boolean isSort,
            TableColumn.SortType sortType,
            Callback<TableColumn<FileItem, FileItem>, TableCell<FileItem, FileItem>> cellFactory,
            Comparator<FileItem> comparator, int prefWidth) {
        
        TableColumn<FileItem, FileItem> tc = new TableColumn<>(header);
        tc.setCellValueFactory(new PropertyValueFactory("this"));
        tc.setSortable(isSortable);
        tc.setCellFactory(cellFactory);
        tc.setComparator(comparator);
        tc.setPrefWidth(prefWidth);
        if (isSortable && isSort) {
            getSortOrder().add(tc);
            tc.setSortType(sortType);
        }
        getColumns().add(tc);

    }
    
    public void addColumn(String header, 
            Callback<TableColumn<FileItem, FileItem>, 
            TableCell<FileItem, FileItem>> cellFactory,
            Comparator<FileItem> comparator, int prefWidth) {    
        addColumn(header, false, false, TableColumn.SortType.ASCENDING, cellFactory, comparator, prefWidth);
    }
    

    protected void preInitTable() {
        setPlaceholder(new Label());
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sList.comparatorProperty().bind(comparatorProperty());
        getSelectionModel().selectFirst();
        
        showHidden.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("showHide change listener");
                
                saveSelectionRow();
                fList.setPredicate(new Predicate<FileItem>() {
                    @Override
                    public boolean test(FileItem t) {
                        if (newValue) {
                                return true;
                        } else {
                            return ("..".equals(t.getName()) || !t.isHidden());
                        }
                    }
                });
                restoreSelectionRow();
            }
        });
    }
    
    protected void postInitTable() {
        getSortOrder().add(getColumns().get(0));
//        showHidden.set(false);
    }
    
    abstract protected void initTable();

    public void updateData(ObservableList<FileItem> l) {
        
        if (l != null) {
            saveSelectionRow();
//            list.clear();
//            list.addAll(l);
//            setItems(list);
//            list = l;
            long time = System.currentTimeMillis();
            System.out.println("start setItems(sList); ");
            setItems(sList);
            System.out.println("stop setItems(sList); millis : " + (System.currentTimeMillis() - time));
            restoreSelectionRow();
        }
    }
    
    public void updateData() {
        saveSelectionRow();
        setItems(sList);
        restoreSelectionRow();
    }

    
    public void addData(FileItem fi) {
        if (fi != null) {
            list.add(fi);
        }
        setItems(list);
    }

    public void addData(ArrayList<FileItem> l) {
        if (l != null) {
            list.addAll(l);
        }
        setItems(list);
    }
    
    public void delData(FileItem fi) {
        if (fi != null) {
            if (list.contains(fi)) {
                list.remove(fi);
            }
        }
        setItems(list);
    }
    
    public void clearData() {
        if (list != null) {
            list.clear();
        }
        setItems(list);
    }
    
    public void refreshTable() {
        getColumns().get(0).setVisible(false);
        getColumns().get(0).setVisible(true);
    }
    
    public BooleanProperty showHiddenProperty() {
        return showHidden;
    }
    
    public void setShowHidden(boolean b) {
        showHidden.set(b);
    }
    
    public boolean getShowHidden() {
        return showHidden.get();
    }
    
    public void saveSelectionRow() {
        savedSelectionFileItem = getSelectionModel().getSelectedItem();
        savedSelectionRowIndex = getSelectionModel().getSelectedIndex();
        isSaveSelectionFileItem = savedSelectionFileItem != null;
    }
    
    public void restoreSelectionRow() {
        if (isSaveSelectionFileItem) {
            getSelectionModel().clearSelection();
            getSelectionModel().select(savedSelectionFileItem);
            isSaveSelectionFileItem = false;
            if (getSelectionModel().getSelectedIndex() < 0) {
                getSelectionModel().selectFirst();
                scrollTo(0);
            } else {
                scrollTo(savedSelectionFileItem);
            }
        }
    }
    
    public FilteredList<FileItem> getFilteredList() {
        return fList;
    }
    
    
    
}
