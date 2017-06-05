/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.table;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import org.dy.fc.FileItem;
import org.dy.fc.comparators.ComparatorDateTime;
import org.dy.fc.comparators.ComparatorFileName;
import org.dy.fc.comparators.ComparatorSize;
import org.dy.fc.comparators.ComparatorType;
import org.dy.fc.table.cells.CellIcon;
import org.dy.fc.table.cells.CellName;
import org.dy.fc.table.cells.CellSize;
import org.dy.fc.table.cells.CellTime;
import org.dy.fc.table.cells.CellType;

/**
 *
 * @author dubin
 */
public class TableViewFileItem extends AbstractTableViewFileItem{

    public TableViewFileItem(ObservableList<FileItem> list) {
        super(list);
    }

    
    @Override
    protected void initTable() {
        addColumn(" ", new CellIcon(), null, 32);
        addColumn("Name", true, true, TableColumn.SortType.ASCENDING, new CellName(), new ComparatorFileName(), 300);
        addColumn("Type", true, false, TableColumn.SortType.ASCENDING, new CellType(), new ComparatorType(), 50);
        addColumn("Size", true, false, TableColumn.SortType.ASCENDING, new CellSize(), new ComparatorSize(), 100);
        addColumn("Date&Time", true, false, TableColumn.SortType.ASCENDING, new CellTime(), new ComparatorDateTime(), 170);
        
        double width = getColumns().get(0).widthProperty().get(); //icon
        width += getColumns().get(2).widthProperty().get(); //type
        width += getColumns().get(3).widthProperty().get(); //size
        width += getColumns().get(4).widthProperty().get(); //date&time
        getColumns().get(1).prefWidthProperty().bind(widthProperty().subtract(width).subtract(10)); //name 
    }
}
