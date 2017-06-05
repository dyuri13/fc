/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.table.cells;

import org.dy.fc.FileItem;
import java.nio.file.Path;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

/**
 *
 * @author dubin
 */
//.CellDataFeatures
public class CellType implements Callback<TableColumn<FileItem, FileItem>, TableCell<FileItem, FileItem>>{

    @Override
    public TableCell<FileItem, FileItem> call(TableColumn<FileItem, FileItem> param) {
        
        TableCell<FileItem, FileItem> cell = new TableCell<FileItem, FileItem>() {

            String s = "";
            FileItem fi;
            TableRow<FileItem> tr;
            
            @Override
            protected void updateItem(FileItem item, boolean empty) {
                super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                
                if (!empty) {
                    tr = getTableRow();
                    if (tr != null) {
                        fi = tr.getItem();
                           if (fi != null) {
                               s = fi.getType();
                           }
                    }
//                    Label l = new Label(s);
//                    setGraphic(l);
                    setText(s);
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        };

        return cell;
    }

    
}
