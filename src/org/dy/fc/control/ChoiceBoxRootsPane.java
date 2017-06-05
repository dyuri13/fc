/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.control;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author dubin
 */
public class ChoiceBoxRootsPane extends ChoiceBox<Path>{
    
//    private ChoiceBox<Path> cb;
    private ArrayList<Path> list;
    private FileSystem fs;
    private final IntegerProperty index = new SimpleIntegerProperty();
    private final StringProperty root = new SimpleStringProperty("");
    private boolean isUpdate = false;
    private int prev;

    public ChoiceBoxRootsPane() {
        this(FileSystems.getDefault());
    }
    
    public ChoiceBoxRootsPane(FileSystem fs) {
        this.fs = fs;
        
        list = new ArrayList<Path>();
        setItems(FXCollections.observableArrayList(list));
        setFocusTraversable(false);
        
        setOnShowing(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
//                System.out.println("showing");
            }
        });
        
        setOnHiding(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
//                System.out.println("hiding");
            }
        });
        
        setOnMousePressed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
//                System.out.println("mouse pressed");
                updateRoots();
//                System.out.println(event);
//                event.consume();
            }
        });
        
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                System.out.println("on action");
                updateRoots();
//                event.consume();
            }
        });
        
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
//                System.out.println("on keypressed");
                updateRoots();
//                event.consume();
            }
        });
        
        getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                prev = oldValue.intValue();
                if (!isUpdate) {
                    index.setValue(newValue);
                }
            }
        });
        updateRoots();
        getSelectionModel().selectFirst();
//        getChildren().add(cb);
    }
    
    final public void updateRoots() {
        Path p = null;
        Path oldP = null;
        int i = getSelectionModel().getSelectedIndex();
        
        if (i >= 0) {
            oldP = list.get(i);
        }
        
        Iterator<Path> it = fs.getRootDirectories().iterator();
//        isUpdate = true;
        list.clear();
//        System.out.println("before clear : ");
        getItems().clear();
//        System.out.println("after clear : ");
        while(it.hasNext()) {
            p = it.next();
            list.add(p);
        }
        getItems().addAll(list);
        if (i < list.size()) {
//            System.out.println("before select : ");
            getSelectionModel().select(i);
//            System.out.println("after select : ");
        }
//        isUpdate = false;

        if (oldP != null) {
//            System.out.println("old : " + oldP);
//            System.out.println("now : " + list.get(cb.getSelectionModel().getSelectedIndex()));
            if (!oldP.toString().equals( list.get(getSelectionModel().getSelectedIndex()).toString()) ) {
                getSelectionModel().selectFirst();
            }
        }

//        System.out.println("update roots : " + isUpdate);
    }
    
    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }
    
    public Path getItem() {
        return getSelectionModel().getSelectedItem();
    }

    public Path getItem(int i) {
        return getItems().get(i);
    }
    
    public void activate() {
//        cb.requestFocus();

//        KeyEvent ke = new KeyEvent(null, cb, KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false);
//        cb.fireEvent(ke);
//        ke.consume();
          show();
        
//        MouseEvent me = new MouseEvent(null, cb, MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false, false, null);
//        cb.fireEvent(me);
    }
    
    public void back() {
        getSelectionModel().select(prev);
    }
    
    public void back(Path p) {
        getSelectionModel().select(p);
    }
    
    
    
}
