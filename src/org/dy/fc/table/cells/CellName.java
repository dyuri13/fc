/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.table.cells;

import org.dy.fc.FileItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

/**
 *
 * @author dubin
 */
//.CellDataFeatures
public class CellName implements Callback<TableColumn<FileItem, FileItem>, TableCell<FileItem, FileItem>>{


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
                               //s = fi.getPath().getFileName().toString();
                               s = fi.getName();
                           }
                    }
//                    Label l = new Label(s);
                    
                    //l.setPrefWidth(350);
                    //l.setWrapText(true);
                    
//                    setGraphic(l);
                    setText(s);
                    //setTooltip(new Tooltip(s));
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        };
        
        return cell;
    }

    
}
