/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.delete;

import org.dy.fc.TaskExt;
import java.util.List;
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
import org.dy.fc.FXDialogs;
import org.dy.fc.FileItem;
import org.dy.fc.operations.ScanResult;
import org.dy.fc.operations.TaskScan;
import org.dy.fc.pane.FilePaneMain;

/**
 *
 * @author dubin
 */
public class TaskWindowDelete {
    private final Window owner;
    private boolean closeAfterEnd = false;
    private List<FileItem> list;
    private FilePaneMain fpm;

    final private int LABEL_WIDTH = 400;
    final private int BUTTON_WIDTH = 70;
    
    private Stage dialog;
    private GridPane grid;
    private Label lTitle;
    private Label lMessage;
    private ProgressBar pb;
    private Button bCancel;
    
    private TaskExt<ScanResult> task;
    private TaskExt<ScanResult> taskDelete;
    
    private Thread thread;
    
    

    public TaskWindowDelete(Window owner, List<FileItem> list, FilePaneMain fpm) {
        this.owner = owner;
        this.list = list;
        this.fpm = fpm;

        task = new TaskScan(owner, list);

        thread = new Thread(task);
        
        initControls();
        initEvents();
        
        
    }
    
    private void initControls() {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(false);
        dialog.setTitle("Deleting...");
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
        
        bindAll(task);
        
        bCancel = new Button("Cancel");
        bCancel.setPrefWidth(BUTTON_WIDTH);

        grid.add(lTitle, 0, 0, 2, 1);
        grid.add(lMessage, 0, 1, 2, 1);
        grid.add(pb, 0, 2, 2, 1);
        grid.add(bCancel, 1, 3, 1, 1);

        // need refactor
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(cc1, cc2);
        
        Bounds mainBounds = owner.getScene().getRoot().getLayoutBounds();
        Scene scene = new Scene(grid, 400, 100);
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
        
        
        task.setOnSucceeded(new EventHandler() {
            @Override
            public void handle(Event event) {
                ScanResult sr = task.getValue();
//                System.out.println("ScanResult : " + sr);
                
                
                
//                bCancel.setText("Close");
//                if (closeAfterEnd) {
//                    dialog.close();
//                }
                
                taskDelete = new TaskDelete(list, owner, sr.getNumItems());
                bindAll(taskDelete);
                
                    taskDelete.setOnSucceeded(new EventHandler() {
                        @Override
                        public void handle(Event event) {
                            ScanResult sr = task.getValue();
            //                System.out.println("ScanResult : " + sr);
                            bCancel.setText("Close");
                            if (closeAfterEnd) {
                                dialog.close();
                            }
                            
                            fpm.refresh();
//                            mp.getInactivePanel().refreshTable();
                        }
                    });
                
                    taskDelete.setOnCancelled(new EventHandler() {
                        @Override
                        public void handle(Event event) {
                            ScanResult sr = task.getValue();
            //                System.out.println("ScanResult : " + sr);
                            bCancel.setText("Close");
                            if (closeAfterEnd) {
                                dialog.close();
                            }
                        }
                    });

                    taskDelete.setOnFailed(new EventHandler() {
                        @Override
                        public void handle(Event event) {
                            ScanResult sr = task.getValue();
            //                System.out.println("ScanResult : " + sr);
                            bCancel.setText("Close");
                            if (closeAfterEnd) {
                                dialog.close();
                            }
                        }
                    });
                
                    task = taskDelete;
                    
                    thread = new Thread(task);
                    thread.start();
                
            }
        });
        
        task.setOnCancelled(new EventHandler() {
            @Override
            public void handle(Event event) {
                ScanResult sr = task.getValue();
//                System.out.println("ScanResult : " + sr);
                bCancel.setText("Close");
                if (closeAfterEnd) {
                    dialog.close();
                }
            }
        });
        
        task.setOnFailed(new EventHandler() {
            @Override
            public void handle(Event event) {
                ScanResult sr = task.getValue();
//                System.out.println("ScanResult : " + sr);
                bCancel.setText("Close");
                if (closeAfterEnd) {
                    dialog.close();
                }
            }
        });
        
    }
    
    public void show() {
        dialog.show();
        thread.start();
    }
    
    private void doCancel() {
        if (task.isRunning()) {
            task.suspend();
                if (FXDialogs.showConfirm(owner, "", null, "Cancel operation ?", ButtonType.YES, ButtonType.NO)) {
                    task.resume();
                    task.cancel();
//                    System.out.println("task.cancel");
                    bCancel.setText("Close");
                    if (closeAfterEnd) {
                        dialog.close();
                    }
                } else {
//                    System.out.println("task.resume");
                    task.resume();
                }
        } else {
            dialog.close();
        }
    }

    
}
