/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.pane;


import org.dy.fc.FileItem;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.StatusBar;
import org.dy.fc.FXDialogs;
import org.dy.fc.IconManager;
import org.dy.fc.control.ChoiceBoxRootsPane;
import org.dy.fc.table.AbstractTableViewFileItem;
import org.dy.fc.table.TableViewFileItem;

/**
 *
 * @author yuri
 */
public class FilePaneMain extends BorderPane implements FilePane{
    private final ObservableList<FileItem> list = FXCollections.observableArrayList();
    
    protected final AbstractTableViewFileItem tv = new TableViewFileItem(list);

    private final LongProperty totalItems = new SimpleLongProperty(0);
    private final LongProperty totalItemsSize = new SimpleLongProperty(0);
    
    private final LongProperty selectedTotalItems = new SimpleLongProperty(0);
    private final LongProperty selectedTotalItemsSize = new SimpleLongProperty(0);
    
    private final ChoiceBoxRootsPane cbr = new ChoiceBoxRootsPane();
    
    private final ObjectProperty<FileItem> currentLocationFileItem = new SimpleObjectProperty<>();
    private final ObjectProperty<FileItem> currentFileItem = new SimpleObjectProperty<>();
    
    private final StackPane stack = new StackPane();
    private final MaskerPane masker = new MaskerPane();

    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private int searchIndex = 0;
    
    
    public FilePaneMain(Path p) {
        initTopStatusBar();
        initBottomStatusBar();
        initHandle();
        initOver();

        
        showHiddenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                calcNumAndSizeAndSelected();
            }
        });
        
        currentFileItem.bind(tv.getSelectionModel().selectedItemProperty());
        
        stack.getChildren().addAll(tv, masker);
        setCenter(stack);
        masker.setVisible(false);
        
        ls(p);
    }

    public FilePaneMain() {
        this(Paths.get(System.getProperty("user.home")));
    }
    
    public FilePaneMain(URI uri) {
        this(Paths.get(uri));
    }
    
    
    public void ls(Path p) {
        ls(p, null);
    }
    
    public void ls(Path p, FileItem select) {//throws IOException { // with task
        int oldSelectIndex = tv.getSelectionModel().getSelectedIndex();
        FileItem old = currentLocationFileItem.get(); 
        TaskLs task = new TaskLs(p, list);
        
        task.setOnSucceeded((WorkerStateEvent event) -> {
            
            long start = System.currentTimeMillis();
            
            currentLocationFileItem.set(new FileItem(p));
            System.out.println("Successed End : " + (System.currentTimeMillis() - start) + " " + p);
            tv.updateData(list);

            masker.setVisible(false);
            
            if (select != null) {
                cursorTo(select);
            } else {
                smartSelect(old, currentLocationFileItem.get(), oldSelectIndex);
            }
            
            
        });
        
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("ls if failed");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        task.getException().printStackTrace();
                        FXDialogs.showError(getWindow(), "Error", null, task.getException().toString());
                    }
                });
                masker.setVisible(false);
                cbr.back(currentLocationFileItem.get().getPath());
            }
        });
        
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        
    }
/*
    class TaskLs extends Task<Void>{
        private Path p;
        private ObservableList<FileItem> list = FXCollections.observableArrayList();
        private long num, size;
        private long timeStart;

        public TaskLs(Path p, ObservableList<FileItem> list) {
            this.p = p;
            this.list = list;
        }
        
        
        @Override
        public Void call() throws Exception {
//            FileItem fi;

            try (DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
                
                timeStart = System.currentTimeMillis();
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        selectedTotalItems.set(0);
                        selectedTotalItemsSize.set(0);
                        list.clear();

                        if (p.getParent() != null) {
                           list.add(new FileItem(p.resolve("..")));

                           num++;
                        }
                    }
                });
                

                Iterator<Path> it = ds.iterator();
                while (it.hasNext()) {
                    if (((System.currentTimeMillis() - timeStart) >= 500) && (!masker.isVisible())) {
                        System.out.println("!!!");
                        masker.setVisible(true);
                    }
                    
                    Path pp = it.next();
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
                            FileItem fi = new FileItem(p);
                            if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                                size = size + fi.getSize();
                            }
//                        }
//                    });
                    
                    
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            list.add(new FileItem(pp));
                            totalItems.set(num);
                            totalItemsSize.set(size);
                            
                        }
                    });
                    
                    
                    
                    num++;
//                    updateMessage(String.valueOf(num));
                    updateMessage("" + num);

//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            totalItems.set(num);
//                            totalItemsSize.set(size);
//                        }
//                    });
//                    Thread.sleep(5);
                }
                
                System.out.println("TaskLs time : " + (System.currentTimeMillis() - timeStart)  + " " + p);
//                masker.setVisible(false);
//                return null;
            }
//            System.out.println("End TaskLs: " + p + " num : " + num + " size : " + size);
            return null;
        }
    }
    
*/

   class TaskLs extends Task<Void>{
        private Path p;
        private ObservableList<FileItem> list;
        private long num, size;
        private long timeStart;

        public TaskLs(Path p, ObservableList<FileItem> list) {
            this.p = p;
            this.list = list;
        }
        
        
        @Override
        public Void call() throws Exception {
            FileItem fi;
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
                
                
                timeStart = System.currentTimeMillis();
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        selectedTotalItems.set(0);
                        selectedTotalItemsSize.set(0);
                        list.clear();
                    }
                });
                
                if (p.getParent() != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            list.add(new FileItem(p.resolve("..")));
                        }
                    });
                    
                    num++;
                }
                Iterator<Path> it = ds.iterator();
                while (it.hasNext()) {
                    if (((System.currentTimeMillis() - timeStart) >= 500) && (!masker.isVisible())){
                        masker.setVisible(true);
                    }
//                    fi = new FileItem(it.next());
//                    Path p = fi.getPath();
//                    if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
//                        size = size + fi.getSize();
//                    }
                    Path p = it.next();
                    if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) {
                        try {
                            size = size + Files.size(p);
                        } catch (IOException ex) {
                            
                        }
                        
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            list.add(new FileItem(p));
                        }
                    });
                    
                    num++;
                    updateMessage(String.valueOf(num));
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            totalItems.set(num);
                            totalItemsSize.set(size);
                        }
                    });
                    
                }
                
                System.out.println("TaskLs time : " + (System.currentTimeMillis() - timeStart)  + " " + p);
//                masker.setVisible(false);
                return null;
            }
//            System.out.println("End TaskLs: " + p + " num : " + num + " size : " + size);
//            return null;
        }
    }
   
    private void enter(FileItem fi) {
        if (fi != null) {
            
            if (Files.isDirectory(fi.getPath())) {
                String name = fi.getName();
                if ("..".equals(name)) {
                    ls(fi.getPath().normalize());
                } else { // not ..
                    ls(fi.getPath());
                }
            } else { // not Directory
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            try {
                                desktop.open(fi.getPath().toFile());
                            } catch (IOException ex) {
                                Logger.getLogger(FilePaneMain.class.getName()).log(Level.SEVERE, null, ex);
                                FXDialogs.showError(getWindow(), "Error", null, ex.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    private void up() {
        if (currentLocationFileItem == null) {
            return;
        }
        
        FileItem fi = new FileItem(currentLocationFileItem.get().getPath().resolve(".."));
        int i = list.indexOf(fi);
        if ( i >= 0) {
            enter(fi);
        }
    }
    
    private void smartSelect(FileItem oldFI, FileItem newFI, int oldIndex) {
        
        if ( (oldFI == null) || (newFI == null) ) {
            tv.getSelectionModel().selectFirst();
            tv.scrollTo(0);
            return;
        }

        if (oldFI.equals(newFI)) {
            if (oldIndex <= tv.getItems().size()-1) {
                tv.getSelectionModel().select(oldIndex);
                tv.scrollTo(oldIndex);
            } else {
                tv.getSelectionModel().selectFirst();
                tv.scrollTo(0);
            }
            return;
        }
        
        Path oldP = oldFI.getPath().normalize();
        Path newP = newFI.getPath().normalize();
        
        if (!oldP.getRoot().equals(newP.getRoot())) {
                tv.getSelectionModel().selectFirst();
                tv.scrollTo(0);
                return;
        }
        
        if (newP.getNameCount() < oldP.getNameCount()) { // up ???
            tv.getSelectionModel().select(oldFI);
            tv.scrollTo(oldFI);
        } else {
            tv.getSelectionModel().selectFirst();
            tv.scrollTo(0);
        }
    }
    
    private void initOver() {
        tv.setRowFactory(new Callback<TableView<FileItem>, TableRow<FileItem>>() {
            
            @Override
            public TableRow<FileItem> call(TableView<FileItem> param) {
                
                TableRow<FileItem> row = 
                new TableRow<FileItem>() {
                    @Override
                    protected void updateItem(FileItem item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        if ( (item != null) && !empty) {
                            setStyle("");
                            
                            if ( item.isHidden() && (!"..".equals(item.getName()))) {
                                
                                //TO DO на выделенной
                                setStyle("-fx-text-background-color: darkgrey");
                            }
                            
                            if (item.isSelected() && (!"..".equals(item.getName())) ) {
                                setStyle("-fx-text-background-color: red;");
                            }
                            
                        }
                    }
                };
                

                row.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.isPrimaryButtonDown() && (event.getClickCount() == 2) ) {
                            FileItem fi = tv.getSelectionModel().getSelectedItem();
                            if (fi != null)  {
                                enter(fi);
                            }
                            event.consume();
                        }
                    }
                    
                });
                return row;
            }
        });
    }

    
    
    
    private void initHandle() {
        tv.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                
                //select current file
                KeyCombination kcInsert = new KeyCodeCombination(KeyCode.INSERT);
                KeyCombination kcSpace = new KeyCodeCombination(KeyCode.SPACE);
                
                //invert select
                KeyCombination kcMultiply = new KeyCodeCombination(KeyCode.MULTIPLY);
                
                KeyCombination kcCtrlH = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
                
                KeyCombination kcBackSpace = new KeyCodeCombination(KeyCode.BACK_SPACE);
                KeyCombination kcCtrlPageUp = new KeyCodeCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN);
                
                KeyCombination kcEnter = new KeyCodeCombination(KeyCode.ENTER);
                KeyCombination kcCtrlPageDown = new KeyCodeCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN);

                
                if (kcCtrlH.match(event)) {
                    tv.showHiddenProperty().set(!tv.showHiddenProperty().get());
                    event.consume();
                }
                
                if ( kcBackSpace.match(event) || kcCtrlPageUp.match(event)) {
                    up();
                    event.consume();
                }
                
                
                if ( kcEnter.match(event) || kcCtrlPageDown.match(event)) {
                    if (tv.getSelectionModel().getSelectedItem() != null) {
                        enter(tv.getSelectionModel().getSelectedItem());
                        event.consume();
                    }
                }
                
                
            }
        });
        
    }
    
    
    private void initTopStatusBar() {

        ToolBar tsb = new ToolBar();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        
        cbr.setFocusTraversable(false);
        cbr.indexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!oldValue.equals(newValue) && (oldValue.intValue() != -1)) {
                    if (newValue.intValue() >= 0) {
                        Path p = cbr.getItem(newValue.intValue());
                        if (p != null) {
                            ls(p);
                        }
                    }
                }
            }
        });
        
        
        HBox.setHgrow(cbr, Priority.NEVER);
        hBox.getChildren().add(cbr);

        
        
        TextField tfCurrentFileItem = new TextField();
        tfCurrentFileItem.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tfCurrentFileItem, Priority.ALWAYS);
        tfCurrentFileItem.setFocusTraversable(false);
        tfCurrentFileItem.setEditable(false);
        hBox.getChildren().add(tfCurrentFileItem);
        
        currentLocationFileItem.addListener(new ChangeListener<FileItem>() {
            @Override
            public void changed(ObservableValue<? extends FileItem> observable, FileItem oldValue, FileItem newValue) {
                if (newValue != null) {
                    URI uri = newValue.getPath().toUri();
                    String s;
                    if ("file".equals(uri.getScheme())) {
                        s = newValue.getPath().toString();
                    } else {
                        s = uri.getScheme().toString() + "://" + uri.getHost() + "/" + uri.getPath();
                    }
                    tfCurrentFileItem.setText(s);
                }
                
            }
        });
        
        
        Button bHome = new Button("", IconManager.getInstance().getIcon("home"));
        bHome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ls(Paths.get(System.getProperty("user.home")));
            }
        });
        bHome.setFocusTraversable(false);
        hBox.getChildren().add(bHome);
        
//        ToggleButton tbShowHidden = new ToggleButton(".");
//        tbShowHidden.setFocusTraversable(false);
//        tv.setShowHidden(false);
//        tbShowHidden.setSelected(tv.getShowHidden());
//        
//        Tooltip ttShow = new Tooltip("Show hidden files");
//        Tooltip ttHide = new Tooltip("Hide hidden files");
//        tbShowHidden.setTooltip( (tv.getShowHidden() ? ttHide : ttShow) );
//        tbShowHidden.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                tv.setShowHidden(tbShowHidden.isSelected());
//                tbShowHidden.setTooltip( (tv.getShowHidden() ? ttHide : ttShow) );
//                calcNumAndSizeAndSelected();
//                System.out.println("list.size() : " + list.size());
//                System.out.println("fList.size() : " + tv.getFilteredList().size());
//            }
//        });
        
        
//        hBox.getChildren().add(tbShowHidden);
        
        
        setTop(hBox);
    }
    
    private void initBottomStatusBar() {
        StatusBar tsb = new StatusBar();
        tsb.setText("");
        tsb.setFocusTraversable(false);
        
        Label lbInfoLeft = new Label();
        Label lbInfoRight = new Label();
        
        tsb.getLeftItems().add(lbInfoLeft);
        tsb.getRightItems().add(lbInfoRight);

        String sfLeft = "%d items / %d bytes";
        StringExpression seLeft = Bindings.format(sfLeft, totalItems, totalItemsSize)   ;
        
        String sfRight = "Selected %d items / %d bytes";
        StringExpression seRight = Bindings.format(sfRight, selectedTotalItems, selectedTotalItemsSize);
        
                
        lbInfoLeft.textProperty().bind(seLeft);
        
        lbInfoRight.textProperty().bind(seRight);
        
        setBottom(tsb);
        
    }
    
    public Window getWindow() {
        return ((Node)getParent()).getScene().getWindow();
    }
    
//    public void updateTable(List<FileItem> list) {
//        tv.updateData(list);
////        tv.getSelectionModel().selectFirst();
//    }

    
    private ArrayList<FileItem> getSelectedItems() {
        ArrayList<FileItem> l = new ArrayList<>();
        if (selectedTotalItems.get() > 0) {
            for (FileItem fi : tv.getItems()) {
                if (fi.isSelected()) {
                    l.add(fi);
                }
            }
        } else {
//            if (tv.getSelectionModel().getSelectedItem() != null) {
//                l.add(tv.getSelectionModel().getSelectedItem());
//            }
        }
        return l;
    }
    
    public FileItem getCurrentItem() {
        return tv.getSelectionModel().getSelectedItem();
    }
    
    public void activateChoiceBoxRoots() {
        cbr.activate();
//        KeyEvent ke = new KeyEvent(null, cbr, KeyEvent.KEY_RELEASED, "", "", KeyCode.SPACE, false, false, false, false);
//        cbr.fireEvent(ke);
    }
    
//    private void refreshTable() {
//        if (currentLocationFileItem.get() != null) {
//            tv.saveSelectionRow();
//            enter(currentLocationFileItem.get());
//            tv.restoreSelectionRow();
//        }
//    }
    
    private void selectFileItem(FileItem fi) {
        if ((fi != null) && (!"..".equals(fi.getPath().getFileName().toString()))) {

            if (fi.isSelected()) {
                selectedTotalItems.set(selectedTotalItems.get() - 1);
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    selectedTotalItemsSize.set(selectedTotalItemsSize.get() - fi.getSize());
                }
            } else {
                selectedTotalItems.set(selectedTotalItems.get() + 1);
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    selectedTotalItemsSize.set(selectedTotalItemsSize.get() + fi.getSize());
                }
            }

            fi.setSelected(!fi.isSelected());
        }        
    }
    
   
    
    
//    public void invertSelection() {
//        for (FileItem fi : list) {
//            selectFileItem(fi);
//        }
//        tv.refresh();
//        refreshTable();
//    }
    
    public void selectAll() {
        for (FileItem fi : list) {
            fi.setSelected(false);
        }
        tv.refresh();
        selectedTotalItems.set(totalItems.get());
        selectedTotalItemsSize.set(totalItemsSize.get());
    }
    
    public void unselectAll() {
        for (FileItem fi : list) {
            fi.setSelected(false);
        }
        tv.refresh();
        selectedTotalItems.set(0);
        selectedTotalItemsSize.set(0);
    }
    
    public void setShowHide(boolean b) {
        tv.setShowHidden(b);
    }
    
    public void getShowHide() {
        tv.showHiddenProperty().get();
    }
    
    public AbstractTableViewFileItem getTV() {
        return tv;
    }

    public void sendKeyEvent(KeyCode code, boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown) {
        KeyEvent ke = new KeyEvent(null, tv, KeyEvent.KEY_PRESSED, "", "", code, shiftDown, controlDown, altDown, metaDown);
        tv.fireEvent(ke);
    }

    public ChoiceBoxRootsPane getCBR() {
        return cbr;
    }

    
    
    public void calcNumAndSizeAndSelected() {
        long num = 0;
        long size = 0;
        long numSelected = 0;
        long sizeSelected = 0;
        boolean calcHidden = tv.getShowHidden();
        long startTime = System.currentTimeMillis();
        for (FileItem fi : list) {
            if (!fi.isHidden() || (fi.isHidden() && calcHidden) ) { //show hidden
                num++;
                if (fi.isSelected()) {
                    numSelected = numSelected + 1;
                }
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    size = size + fi.getSize();
                    if (fi.isSelected()) {
                        sizeSelected = sizeSelected + fi.getSize();
                    }
                }
            } else {
                if (fi.isSelected()) {
                    fi.setSelected(false);
                }
            }
        }
        
        totalItems.set(num);
        totalItemsSize.set(size);
        selectedTotalItems.set(numSelected);
        selectedTotalItemsSize.set(sizeSelected);
        System.out.println("calcNumAndSizeAndSelected() : " + (System.currentTimeMillis() - startTime));
    }

    @Override
    public boolean isActive() {
//        return tv.focusedProperty().get();
        return isFocused();
    }

    @Override
    public ReadOnlyBooleanProperty activeProperty() {
        return tv.focusedProperty();
    }
    
    

    @Override
    public void setActive() {
        tv.requestFocus();
    }

    @Override
    public FileItem getSelectedFileItem() {
        return tv.getSelectionModel().getSelectedItem();
    }

    @Override
    public List<FileItem> getSelectedFileItems() {
        ArrayList<FileItem> l = new ArrayList<>();
        if (selectedTotalItems.get() > 0) {
            for (FileItem fi : tv.getFilteredList()) {
                if (fi.isSelected()) {
                    l.add(fi);
                }
            }
        } else {
//            if (tv.getSelectionModel().getSelectedItem() != null) {
//                l.add(tv.getSelectionModel().getSelectedItem());
//            }
        }
        return l;
    }

    @Override
    public void selectCurrentFileItem() {
        FileItem fi = tv.getSelectionModel().getSelectedItem();
        selectFileItem(fi);
          // ??? WTF 
          // we send code THIS IS COOL
          // что бы был скролл и на следующую строку переходил
        KeyEvent ke = new KeyEvent(null, tv, KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        tv.fireEvent(ke);
        tv.refreshTable();
    }
    
    @Override
    public void cursorTo(FileItem fi) {
        tv.getSelectionModel().select(fi);
        int i = tv.getSelectionModel().getSelectedIndex();
        tv.scrollTo(i);
    }
    
    @Override
    public void cursorTo(int i) {
        tv.getSelectionModel().select(i);
        tv.scrollTo(i);
    }
    @Override
    public void selectFileItems(boolean isDirSelect, String mask) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unselectFileItems(boolean isDirSelect, String mask) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void invertSelectionAll(boolean isDirSelect) {
        for (FileItem fi : list) {
            if (Files.isDirectory(fi.getPath(), LinkOption.NOFOLLOW_LINKS) && isDirSelect || 
                    !Files.isDirectory(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                selectFileItem(fi);
            }
            
        }
        refresh();
    }

    @Override
    public void refresh() {
        if (currentLocationFileItem.get() != null) {
            tv.saveSelectionRow();
            enter(currentLocationFileItem.get());
            tv.restoreSelectionRow();
        }
    }

    @Override
    public void refresh(FileItem select) {
        ls(currentLocationFileItem.get().getPath(), select);
//        if (select != null) {
//            cursorTo(select);
//        }
    }
    


    @Override
    public int getCursorIndex() {
        return tv.getSelectionModel().getSelectedIndex();
    }
    
    @Override
    public boolean getShowHidden() {
        return tv.getShowHidden();
    }
    
    @Override
    public void setShowHidden(boolean b) {
        tv.setShowHidden(b);
    }
    
    @Override
    public BooleanProperty showHiddenProperty() {
        return tv.showHiddenProperty();
    }
    
    @Override
    public FileItem getCurrentLocationFileItem() {
        return currentLocationFileItem.get();
    }
    
    @Override
    public ObjectProperty<FileItem> currentLocationFileItemProperty() {
        return currentLocationFileItem;
    }
    
    public void goHome() {
        ls(Paths.get(System.getProperty("user.home")));
    }

    @Override
    public FileItem searchFileItemByName(String s) {
        FilteredList<FileItem> fl = tv.getFilteredList();
        for (FileItem fi : fl) {
            if (fi.getName().startsWith(s)) {
                return fi;
            }
        }
        return null;
    }
}
