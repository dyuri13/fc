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
public class CellTime implements Callback<TableColumn<FileItem, FileItem>, TableCell<FileItem, FileItem>>{

    private String formatDateTime(String s) {
        //System.out.println(s);
        s = s.replace("T", " ");
        int i = s.indexOf(".");
        if ( (i >= 0) && (i < s.length())) {
            s = s.substring(0, i-1);
        }

        return s;
        //return s.replace("T", " ").substring(0, (s.contains(".") ? s.indexOf(".") : s.length()-1));
    }
    
    @Override
    public TableCell<FileItem, FileItem> call(TableColumn<FileItem, FileItem> param) {
        TableCell<FileItem, FileItem> cell = new TableCell<FileItem, FileItem>() {

            String s = "";
            //Path p;
            FileItem fi;
            
            @Override
            protected void updateItem(FileItem item, boolean empty) {
                super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                
                if (!empty) {
                    TableRow<FileItem> tr = getTableRow();
                    if (tr != null) {
                        fi = tr.getItem();
                        if (fi != null) {
                            if (fi.getDateTime() != null) {
                                s = fi.getDateTime().toString();
                            } else {
                                s = "";
                            }
                        }
//                        Label l = new Label(formatDateTime(s));
//                        setGraphic(l);
                        setText(s);
                    } 
                } else {
                    setGraphic(null);
                    setText(null);
                }   
            };
        };
        return cell;
    }
}
