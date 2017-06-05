/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.dy.fc.copy.TaskCopy;
import org.dy.fc.delete.TaskDelete;
import org.dy.fc.move.TaskMove;
import org.dy.fc.pane.FilePane;
import org.dy.fc.pane.FilePaneMain;

/**
 *
 * @author yuri
 */
public class MainPanel extends BorderPane implements MainPane{

    private SplitPane sp;
    private FilePane pLeft, pRight;
    
    
    private final BooleanProperty showHiddenGlobal = new SimpleBooleanProperty(true);
    private final ObjectProperty<FilePane> activePanel = new SimpleObjectProperty<>(null);
   
    
    public MainPanel() {
        initCenter();   //must be first ???   TODO
        
        initTop();
        initBottom();

        initHandle();
        
        showHiddenGlobal.bindBidirectional(pLeft.showHiddenProperty());
        showHiddenGlobal.bindBidirectional(pRight.showHiddenProperty());
        
        pLeft.activeProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    activePanel.set(pLeft);
                }
            }
        });
        
        pRight.activeProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    activePanel.set(pRight);
                }
            }
        });
    }

    private void initTop() {
        VBox vBox = new VBox(5);
        
        MainMenu mainMenu = new MainMenu(this);
        vBox.getChildren().add(mainMenu);
        
        MainTopBar mTop = new MainTopBar(this);
        vBox.getChildren().add(mTop);
        
        setTop(vBox);
    }

    private void initBottom() {
        MainBottomBar b = new MainBottomBar(this);
        setBottom(b);
    }

    private void initHandle() {
//        KeyCombination kcAltF1 = new KeyCodeCombination(KeyCode.F1, KeyCombination.ALT_DOWN);
//        KeyCombination kcAltF2 = new KeyCodeCombination(KeyCode.F2, KeyCombination.ALT_DOWN);
        KeyCombination kcF3 = new KeyCodeCombination(KeyCode.F3);
        KeyCombination kcF4 = new KeyCodeCombination(KeyCode.F4);
        KeyCombination kcF5 = new KeyCodeCombination(KeyCode.F5);
        KeyCombination kcF6 = new KeyCodeCombination(KeyCode.F6);
        KeyCombination kcShiftF6 = new KeyCodeCombination(KeyCode.F6, KeyCombination.SHIFT_DOWN);
        KeyCombination kcF7 = new KeyCodeCombination(KeyCode.F7);
        KeyCombination kcF8 = new KeyCodeCombination(KeyCode.F8);
        
        //select current file
        KeyCombination kcInsert = new KeyCodeCombination(KeyCode.INSERT);
        KeyCombination kcSpace = new KeyCodeCombination(KeyCode.SPACE);

        //invert select
        KeyCombination kcMultiply = new KeyCodeCombination(KeyCode.MULTIPLY);
        
        
        sp.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
        
                Window window = ((Node)event.getTarget()).getScene().getWindow();
                
                // select current item
                if (kcInsert.match(event) || kcSpace.match(event)) {
                    getActivePanel().selectCurrentFileItem();
                    event.consume();
                }
                
                // invert selection ?? need folders or not ?TODO
                if (kcMultiply.match(event)) {
                    getActivePanel().invertSelectionAll(true);
                    event.consume();
                }
                
                if (kcF7.match(event)) {
                    doMakeDir(window);
                    event.consume();
                }

                if (kcF8.match(event)) {
                    doDelete(window);
                    event.consume();
                }

                if (kcF5.match(event)) {
                    doCopy(window);
                    event.consume();
                }

                if (kcF6.match(event)) {
                    doMove(window);
                    event.consume();
                }
                
                if (kcShiftF6.match(event)) {
                    doRename(window);
                    event.consume();
                }
                
                
//                if (event.isAltDown() && (event.getCode().isLetterKey() || event.getCode().isDigitKey()) ) {
//                    System.out.println("to do search");
//                    mTop.tfSearch.setText(event.getCode().toString());
//                    mTop.tfSearch.requestFocus();
//                    event.consume();
//                }
                
            }
            
        });
        
    }
    
    public void doMakeDir(Window window) {
        FilePane fpm = getActivePanel();
        String s = FXDialogs.showTextInput(window, "Make directory", null, "Folder name :", "");
        if (s != null) {
            System.out.println("Make directory : " + s);
            try {
                Path p = getActivePanel().getCurrentLocationFileItem().getPath();
                p = p.resolve(s);
                System.out.println("Make directory after resolve : " + p);
                Files.createDirectory(p);
                fpm.refresh(new FileItem(p));
            } catch (IOException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                FXDialogs.showException(window, "IOException", null, "Error create directory : " + s, ex);
            }
        }
    }
    
    public void doDelete(Window window) {
        List<FileItem> list =  getActivePanel().getSelectedFileItems();
        FileItem fi = getActivePanel().getSelectedFileItem();
        String message = "";
        
        
        if (list.size() == 0) {
            if ((fi != null) && (!"..".equals(fi.getName()))) {
                list.add(fi);
            }
        }

        if (list.size() > 0) {
        
            if (list.size() == 1) {
                message = "Delete " + list.get(0).getName() + " ?";
            } else {
                message = "Delete " + list.size() + " items ?";
            }
            if (FXDialogs.showConfirm(window, "Delete", null, message)) {
                    TaskDelete td = new TaskDelete(list, window, 0);
                    WindowTask tw = new WindowTask(window, td, "Delete...", list, true, getActivePanel(), null, true, false, false);
                    tw.show();
            }
        }
        
    }

    public void doCopy(Window window) {
        List<FileItem> list =  getActivePanel().getSelectedFileItems();
        FileItem fi = getActivePanel().getSelectedFileItem();
        FileItem fiDest = getInactivePanel().getCurrentLocationFileItem();
        
        String message = "";
        
        
        if (list.size() == 0) {
            if ((fi != null) && (!"..".equals(fi.getName()))) {
                list.add(fi);
            }
        }

        if (list.size() > 0) {
        
            if (list.size() == 1) {
                message = "Copy " + list.get(0).getName();
            } else {
                message = "Copy " + list.size() + " items";
            }
            
            message = message + "\nto " + fiDest.getPath().toString() + " ?";
            
            if (FXDialogs.showConfirm(window, "Copy", null, message)) {
                    TaskCopy tc = new TaskCopy(list, window, 0, fiDest.getPath());
                    WindowTask tw = new WindowTask(window, tc, "Copy...", list, true, 
                            getActivePanel(), getInactivePanel(), false, true, true);
                    tw.show();
            }
            
        }
        
        
        
    }
    
    public void doMove(Window window) {
        List<FileItem> list =  getActivePanel().getSelectedFileItems();
        FileItem fi = getActivePanel().getSelectedFileItem();
        FileItem fiDest = getInactivePanel().getCurrentLocationFileItem();
        
        String message = "";
        
        
        if (list.size() == 0) {
            if ((fi != null) && (!"..".equals(fi.getName()))) {
                list.add(fi);
            }
        }

        if (list.size() > 0) {
        
            if (list.size() == 1) {
                message = "Move " + list.get(0).getName();
            } else {
                message = "Move " + list.size() + " items";
            }
            
            message = message + " to " + fiDest.getPath().toString() + " ?";
            
            if (FXDialogs.showConfirm(window, "Move", null, message)) {
                    TaskMove tm = new TaskMove(list, window, 0, fiDest.getPath());
                    WindowTask tw = new WindowTask(window, tm, "Move...", list, true, 
                            getActivePanel(), getInactivePanel(), true, true, true);
                    tw.show();
            }
            
        }
        
        
        
    }
    

    public void doRename(Window window) {
        FileItem fi = getActivePanel().getSelectedFileItem();
        Path p = fi.getPath().getParent();
        System.out.println("In Rename : ");
        if ((fi != null) && (!"..".equals(fi.getName()))) {
            String s = FXDialogs.showTextInput(window, "Rename item", null, "Enter new name :", fi.getName());
            if ((s != null) && (!s.equals(fi.getName()))) {
                System.out.println("Rename : " + fi.getName() + " to " + s);
                try {
                    p = p.resolve(s);
                   
                    System.out.println("rename : " + fi.getPath() + " to :" + p);
                    Files.move(fi.getPath(), p);
                    getActivePanel().refresh();
                    getActivePanel().cursorTo(new FileItem(p));
                } catch (IOException ex) {
                    Logger.getLogger(MainPane.class.getName()).log(Level.SEVERE, null, ex);
                    FXDialogs.showException(window, "Error", null, "Error rename : " + fi.getPath() + "\nto : " + p, ex);
                    System.out.println(ex.getLocalizedMessage());
//                    FXDialogs.showError(window, "Error", null, ex.toString());
                }
            }
            
        }        
//        if (FXDialogs.showConfirm(window, "Move", null, message)) {
//                TaskMove tm = new TaskMove(list, window, 0, fiDest.getPath());
//                WindowTask tw = new WindowTask(window, tm, "Move...", list, true, 
//                        getActivePanel(), getInactivePanel(), true, true, true);
//                tw.show();
//        }
            
//        }
        
        
        
    }
    
//    public void showCBRLeft() {
//        pLeft.getCBR().activate();
//    }
//    
//    public void showCBRRight() {
//        pRight.getCBR().activate();
//    }
    
    
    void quit() {
//        if (FXDialogs.showConfirm(null, "", null, "Exit application?")) {
            Platform.exit();
//        }
    }
    
    private void initCenter() {
        
        pLeft = new FilePaneMain();
        
//        URI uri = URI.create("jar:file:/POST;dubin:Vertex86gb$@WAIN/DATA");
        
        Path p = Paths.get("c:/tmp/fc.zip");
        URI uri = URI.create("jar:file:" + p.toUri().getPath());
        
//        pRight = new FilePaneMain(Paths.get("C:\\Windows\\winsxs\\"));
        pRight = new FilePaneMain();
        
        
        
        
        sp = new SplitPane((Node)pLeft, (Node)pRight);
        
        setCenter(sp);
    }
    
    
    public FilePane getLeftPane() {
        return pLeft;
    }

    public FilePane getRightPane() {
        return pRight;
    }

    @Override
    public FilePane getActivePanel(){
//        if (pLeft.isActive()) {
//            return pLeft;
//        } else {
//            if (pRight.isActive()) {
//                return pRight;
//            }
//        }
        return activePanel.get();
    } 
    
    @Override
    public FilePane getInactivePanel(){
        if (getActivePanel() == pLeft) {
            return pRight;
        } else {
            return pLeft;
        }
    }
    
    @Override
    public void setActivePanel(FilePane pane) {
        ((Pane)pane).requestFocus();
    }
    
    @Override
    public void refreshPanel(FilePane p) {
        p.refresh();
    }

    @Override
    public boolean getShowHiddenGlobal() {
        return showHiddenGlobal.get();
    }

    @Override
    public void setShowHiddenGlobal(boolean b) {
        showHiddenGlobal.set(b);
    }

    @Override
    public BooleanProperty showHiddenGlobalProperty() {
        return showHiddenGlobal;
    }

    @Override
    public void swapPanels() {
        FilePane tmpLeft, tmpRight;
        tmpLeft = pLeft;
        tmpRight = pRight;
        sp.getItems().clear();
        pLeft = tmpRight;
        pRight = tmpLeft;
        sp.getItems().addAll((Node)pLeft, (Node)pRight);
    }

    @Override
    public void goHome() {
        getActivePanel().goHome();
    }


    
}
