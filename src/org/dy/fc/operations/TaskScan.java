/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.operations;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Window;
import org.dy.fc.FXDialogs;
import org.dy.fc.FileItem;
import org.dy.fc.TaskExt;

/**
 *
 * @author dubin
 */
public class TaskScan extends TaskExt<ScanResult>{

    private final ScanResult sr = new ScanResult();
    private final List<FileItem> list;
    

    public TaskScan(Window owner, List<FileItem> list) {
        super(owner);
        this.list = list;
    }
    
    
    private void updateInfo(Path p) {
//        updateTitle("Preparing...");
        updateMessage("" + sr.getNumItems()); // + " Size : " + sr.getSize()
//        updateProgress(-1, -1);
        updateValue(sr);
    }

    @Override
    protected void succeeded() {
        updateTitle("Succeeded scan");
        updateProgress(0, 0);
        sr.setComplete(true);
        updateValue(sr);
//        System.out.println("sr in saccessed task : " + sr);
    }

    @Override
    protected void cancelled() {
        updateTitle("Cancelled scan");
//        System.out.println("Cancelled in task");
        updateProgress(0, 0);
        sr.setComplete(false);
        updateValue(sr);
    }

    @Override
    protected void failed() {
        updateTitle("Failed scan");
        updateProgress(0, 0);
        sr.setComplete(false);
        updateValue(sr);
    }
    
    
    
    
    
    @Override
    protected ScanResult call() throws Exception {
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        Visitor visitor = new Visitor();
        updateTitle("Preparing...");
        for (FileItem fi : list) {
            if (halt) {
                halt();
            }
            if (isCancelled()) {
                sr.setComplete(false);
                break;
            }
        
//        System.out.println("Files.walkFileTree : " + fi.getName());    
        Files.walkFileTree(fi.getPath(), opts, Integer.MAX_VALUE, visitor);
            
            
        }
        return sr;
    }

    class Visitor extends SimpleFileVisitor<Path> {
        private boolean skipAll = false;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            sr.setNumItems(sr.getNumItems() + 1);
//            System.out.println(sr.numItemsProperty());
            
            if (Files.isRegularFile(file)) {
                sr.setSize(sr.getSize() + Files.size(file));
            }
            
            updateInfo(file);
            
//            System.out.println("visetFile : " + file);
            if (isCancelled()) {
                sr.setComplete(false);
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }
            
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            sr.setNumItems(sr.getNumItems() + 1);
            updateInfo(file);
            
//            System.out.println("visetFileFailed : " + file);
//            System.out.println("IOException : " + exc);

            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }
            
            if (skipAll) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            
            FutureTask<String> ft = new FutureTask(new askException(exc));
            Platform.runLater(ft);
            try {
                updateProgress(0, 0);
                String s = ft.get();
                updateProgress(-1, -1);
//                System.out.println("askException : " + s);
                
                if ("Skip".equals(s)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                
                if ("Skip all".equals(s)) {
                    skipAll = true;
                    return FileVisitResult.SKIP_SUBTREE;
                }
                
                if ("Abort".equals(s)) {
                    skipAll = true;
                    cancel(true);
                    return FileVisitResult.TERMINATE;
                }
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(TaskScan.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(TaskScan.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return FileVisitResult.CONTINUE;
        }
        
        

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            sr.setNumItems(sr.getNumItems() + 1);
            updateInfo(dir);
            
//            System.out.println("preVisetDirectory : " + dir);
            if (isCancelled()) {
                sr.setComplete(false);
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }
            
            
            return FileVisitResult.CONTINUE;
        }

/*        
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//            updateTitle(dir.toString());
//            updateMessage(" items : " + items + " size : " + size);
            updateInfo(dir);
            
//            System.out.println("postVisetDirectory : " + dir);
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }
            
            if (exc == null) {
                return FileVisitResult.CONTINUE;
            } else {
                throw exc;
            }
        }
*/
        
    }
    
    class askException implements Callable<String> {
        private IOException ex;

        public askException(IOException ex) {
            this.ex = ex;
        }
        
        @Override
        public String call() throws Exception {
//            FXDialogs.showException(owner, "Exception", "Exception in scan", "Exception in scan", ex);
//            FXDialogs.showError(owner, "Error", null, ex.toString());

            String result = FXDialogs.showConfirm(owner, Alert.AlertType.ERROR, "Error", null, ex.toString(), "Abort", "Skip", "Skip all");
            return result;
        }
        
    }
    
}
