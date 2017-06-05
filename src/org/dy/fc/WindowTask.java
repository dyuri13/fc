/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.dy.fc.operations.ScanResult;
import org.dy.fc.operations.TaskScan;
import org.dy.fc.pane.FilePane;


/**
 *
 * @author dubin
 */
public class WindowTask {
    private final Window owner;
    TaskExt<?> taskExt;
    String title;
    private List<FileItem> list;
    private FilePane fpmActive;
    private FilePane fpmInactive;
    private boolean updateActive;
    private boolean updateInactive;
    private boolean clearSelectionActive;

    final private int LABEL_WIDTH = 400;
    final private int BUTTON_WIDTH = 70;
    
    private boolean closeAfterEnd = true;
    
    private Stage dialog;
    private GridPane grid;
    private Label lTitle;
    private Label lMessage;
    private ProgressBar pb;
    private Button bCancel;
    
//    private TaskExt task;
    private TaskScan taskScan;
    
    private Thread thread;
    
    

    public WindowTask(Window owner, TaskExt taskExt, String title, 
            List<FileItem> list, boolean closeAfterEnd, 
            FilePane fpmActive, FilePane fpmInactive, 
            boolean updateActive, boolean updateInactive, boolean clearSelectionActive) {
        this.owner = owner;
        this.taskExt = taskExt;
        this.title = title;
        this.list = list;
        this.closeAfterEnd = closeAfterEnd;
        this.fpmActive = fpmActive;
        this.fpmInactive = fpmInactive;
        this.updateActive = updateActive;
        this.updateInactive = updateInactive;
        this.clearSelectionActive = clearSelectionActive;

        taskScan = new TaskScan(owner, list);
//        task = taskScan;

        thread = new Thread(taskScan);
        thread.setDaemon(true);
        
        initControls();
        initEvents();
        
        
    }
    
    private void initControls() {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(false);
        dialog.setTitle(title);
        grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5));
        
        lTitle = new Label();
//        lTitle.textProperty().bind(task.titleProperty());
        lTitle.setPrefWidth(LABEL_WIDTH);
        
        lMessage = new Label();
//        lMessage.textProperty().bind(task.messageProperty());
        lMessage.setPrefWidth(LABEL_WIDTH);

        pb = new ProgressBar();
//        pb.progressProperty().bind(task.progressProperty());
        pb.setPrefWidth(LABEL_WIDTH);
        
        bindAll(taskScan);
        
        bCancel = new Button("Cancel");
        bCancel.setPrefWidth(BUTTON_WIDTH);
        bCancel.setDefaultButton(true);

        grid.add(lTitle, 0, 0, 2, 1);
        grid.add(lMessage, 0, 1, 2, 2);
        grid.add(pb, 0, 3, 2, 1);
        grid.add(bCancel, 1, 4, 1, 1);

        // need refactor
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(cc1, cc2);
        
        Bounds mainBounds = owner.getScene().getRoot().getLayoutBounds();
        Scene scene = new Scene(grid, 400, 105);
        dialog.setScene(scene);
        dialog.initOwner(this.owner);        
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        Bounds dialogBounds = scene.getRoot().getLayoutBounds();
        dialog.setX( owner.getX() + (mainBounds.getWidth() - dialogBounds.getWidth()) /2 );
        dialog.setY( owner.getY() + (mainBounds.getHeight()- dialogBounds.getHeight()) /2 );
    }
    
    private void bindAll(TaskExt task) {
        lTitle.textProperty().unbind();
        lTitle.textProperty().bind(task.titleProperty());
        lMessage.textProperty().unbind();
        lMessage.textProperty().bind(task.messageProperty());
        pb.progressProperty().unbind();
        pb.progressProperty().bind(task.progressProperty());
    }
    
    private void initEvents() {
        
        dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                doCancel();
            }
        });
        
        bCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doCancel();
            }
        });
        
        
        taskScan.setOnSucceeded(new EventHandler() {
            @Override
            public void handle(Event event) {
                ScanResult sr = taskScan.getValue();
                System.out.println("ScanResult : " + sr);
                
                
                
//                bCancel.setText("Close");
//                if (closeAfterEnd) {
//                    dialog.close();
//                }
                
                taskExt.setMaxProgress(sr.getNumItems());
//                task = taskExt;
                bindAll(taskExt);
                
                
//                    task = taskExt;
                    
                thread = new Thread(taskExt);
                thread.setDaemon(true);
                thread.start();
                
            }
        });
        
        taskScan.setOnCancelled(new EventHandler() {
            @Override
            public void handle(Event event) {
                ScanResult sr = taskScan.getValue();
//                System.out.println("ScanResult : " + sr);
                bCancel.setText("Close");
                if (closeAfterEnd) {
                    dialog.close();
                }
                
                updatePanels();
            }
        });
        
        taskScan.setOnFailed(new EventHandler() {
            @Override
            public void handle(Event event) {
                ScanResult sr = taskScan.getValue();
//                System.out.println("ScanResult : " + sr);
                bCancel.setText("Close");
                if (closeAfterEnd) {
                    dialog.close();
                }
                
                updatePanels();
            }
        });

            taskExt.setOnSucceeded(new EventHandler() {
                @Override
                public void handle(Event event) {
    //                System.out.println("ScanResult : " + sr);
                    bCancel.setText("Close");
                    if (closeAfterEnd) {
                        dialog.close();
                    }
                    updatePanels();
                }
            });

            taskExt.setOnCancelled(new EventHandler() {
                @Override
                public void handle(Event event) {
    //                System.out.println("ScanResult : " + sr);
                    bCancel.setText("Close");
                    if (closeAfterEnd) {
                        dialog.close();
                    }

                    updatePanels();
                }
            });

            taskExt.setOnFailed(new EventHandler() {
                @Override
                public void handle(Event event) {
    //                System.out.println("ScanResult : " + sr);
                    bCancel.setText("Close");
                    if (closeAfterEnd) {
                        dialog.close();
                    }

                    updatePanels();
                }
            });
        
    }
    
    public void show() {
        dialog.show();
        thread.start();
    }
    
    private void doCancel() {
        TaskExt task = null;
        
        if (taskScan.isRunning()) {
            task = taskScan;
        } else if (taskExt.isRunning()) {
            task = taskExt;
        }
        
        if (task != null && task.isRunning()) {
            System.out.println("windowTask task.suspend()");
            task.suspend();

            // wait forn task stop
            try {
                task.getCDL().await();
            } catch (InterruptedException ex) {
                Logger.getLogger(WindowTask.class.getName()).log(Level.SEVERE, null, ex);
            }
                
                if (FXDialogs.showConfirm(owner, "", null, "Cancel operation ?", ButtonType.YES, ButtonType.NO)) {
                    
                    task.resume();
                    task.cancel();
                    
                    
                    System.out.println("windowTask task.cancel");
                    bCancel.setText("Close");
                    if (closeAfterEnd) {
                        dialog.close();
                    }
                } else {
                    System.out.println("windowTask task.resume");
                    task.resume();
                }
        } else {
            dialog.close();
        }
    }
    
    private void updatePanels() {
        if (fpmActive != null) {
            if (updateActive) {
                fpmActive.refresh();
            }
            if (clearSelectionActive) {
                fpmActive.unselectAll();
            }
        }
        
        if (fpmInactive != null) {
            if (updateInactive) {
                fpmInactive.refresh();
            }
            if (updateInactive) {
                fpmInactive.unselectAll();
            }
        }
    }

    
}
