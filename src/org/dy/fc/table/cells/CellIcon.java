/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.table.cells;

import org.dy.fc.FileItem;
import java.nio.file.Path;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 *
 * @author dubin
 */
//.CellDataFeatures
public class CellIcon implements Callback<TableColumn<FileItem, FileItem>, TableCell<FileItem, FileItem>>{
    
/*    private String getExt(String s) {
        return null;
    }
*/    
    
    @Override
    public TableCell<FileItem, FileItem> call(TableColumn<FileItem, FileItem> param) {
        
        //ImageView imageView;
        
        TableCell<FileItem, FileItem> cell = new TableCell<FileItem, FileItem>() {

        String s = "";
        Path f;
        TableRow<FileItem> tr;
        FileItem fi;
        ImageView imageView;// = new ImageView();

            
        @Override
        protected void updateItem(FileItem item, boolean empty) {
            
            super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
            //final ImageView imageView = null;
            if (!empty) {
                tr = getTableRow();
                if (tr != null) {
                    fi = tr.getItem();
                       if (fi != null) {
                           f = fi.getPath();
                           //imageView = getImageView(f);
                           imageView = fi.getIcon();
                           
                           //imageView.setImage(fi.getIcon());
                           //Label l = new Label("", imageView);
                           setGraphic(imageView);
                           
                           //setGraphic(l);
                           //System.out.println(fi);
                    }
                }
            } else {
                setGraphic(null);
                setText(null);
//                System.out.println("else " +fi);
            }
        }
        };
        return cell;
    }
    
    

}
