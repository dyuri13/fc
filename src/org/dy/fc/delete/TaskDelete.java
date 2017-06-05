/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.delete;

import org.dy.fc.TaskExt;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystemException;
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
import org.dy.fc.FileUtils;
import org.dy.fc.operations.ScanResult;

/**
 *
 * @author dubin
 */
public class TaskDelete extends TaskExt<ScanResult>{

    private final List<FileItem> list;
    
    private long count;
    private boolean skipAll = false;
    
    public TaskDelete(List<FileItem> list, Window window, long numItems) {
        super(window);
        this.list = list;
        maxProgress = numItems;
        
    }

    public TaskDelete(List<FileItem> list, long count, Window window) {
        super(window, 0);
        this.list = list;
        this.count = count;
    }
    
    
    
    private void updateInfo(Path p) {
        updateTitle("Delete : " + p.getFileName().toString());
        updateMessage("Delete : " + count + " of " + maxProgress);
        updateProgress(count, maxProgress);
    }
    
    @Override
    protected void succeeded() {
        updateTitle("Succeeded delete");
        updateProgress(0, 0);
//        sr.setComplete(true);
//        updateValue(sr);
//        System.out.println("sr in saccessed task : " + sr);
    }

    @Override
    protected void cancelled() {
        updateTitle("Cancelled delete");
//        System.out.println("Cancelled in task");
        updateProgress(0, 0);
//        sr.setComplete(false);
//        updateValue(sr);
    }

    @Override
    protected void failed() {
        updateTitle("Failed delete");
        updateProgress(0, 0);
//        sr.setComplete(false);
//        updateValue(sr);
    }
    
    
    @Override
    protected ScanResult call() throws Exception {
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        Visitor visitor = new Visitor();
        
        for (FileItem fi : list) {
            if (isCancelled()) {
                break;
            }
            
            if (halt) {
                halt();
            }
            
            Files.walkFileTree(fi.getPath(), new Visitor());
            
        }
        return null;
    }

   
    
    
    class Visitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            count++;
            updateInfo(file);
//            System.out.println("visetFile : " + file);
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }

//            System.out.println("delete file : " + file);
            try {
                if (FileUtils.isReadOnly(file)) {
                    FileUtils.setReadOnly(file, false);
                }
                Files.delete(file);
            } catch (IOException ex) {
                
                if (skipAll) {
                    return FileVisitResult.CONTINUE;
                } 
    
               
//                FutureTask<String> ft = new FutureTask(new askException(ex));
                FutureTask<FileVisitResult> ft = new FutureTask(new askAction(ex, file));
                Platform.runLater(ft);
                try {
                    updateProgress(0, 0);
                    FileVisitResult r  = ft.get();
//                    updateProgress(-1, -1);
    //                System.out.println("askException : " + s);
                    return r;

                } catch (InterruptedException ex1) {
                    Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex1) {
                    Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
//            System.out.println("after delete file : " + file);
            
            
            return FileVisitResult.CONTINUE;
        }

/*        
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            count++;
            updateInfo(file);
            
            System.out.println("visetFileFailed : " + file);
            System.out.println("IOException : " + exc);
            
            
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }
            
            if (skipAll) {
                return FileVisitResult.CONTINUE;
            }
            
            FutureTask<String> ft = new FutureTask(new askException(exc));
            Platform.runLater(ft);
            try {
                String s = ft.get();
                System.out.println(s);
                
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
                Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return FileVisitResult.CONTINUE;
        }
*/        
        
        
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            count++;
            updateInfo(dir);
            
//            System.out.println("postVisetDirectory : " + dir);
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            if (halt) {
                halt();
            }
            
//            System.out.println("delete dir : " + dir);
            try {
                if (FileUtils.isReadOnly(dir)) {
                    FileUtils.setReadOnly(dir, false);
                }
                Files.delete(dir);
            } catch (DirectoryNotEmptyException ex3) {
//                System.out.println("DirectoryNotEmptyException ex3");
                Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex3);
                
            } catch (FileSystemException ex4) {
//                System.out.println("FileSystemException ex4");
                Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex4);
                
            } catch (IOException ex) {
                
                if (skipAll) {
                    return FileVisitResult.CONTINUE;
                }
                
                FutureTask<FileVisitResult> ft = new FutureTask(new askException(exc));
                Platform.runLater(ft);

                try {
                    FileVisitResult r = ft.get();
//                    System.out.println(r);

                    return r;

                } catch (InterruptedException ex1) {
                    Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex1) {
                    Logger.getLogger(TaskDelete.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
//            System.out.println("after delete dir : " + dir);
            if (exc == null) {
                return FileVisitResult.CONTINUE;
            } else {
//                System.out.println("exception in postVisitDerectory : " + exc);
                throw exc;
            }
        }

        
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

    class askAction implements Callable<FileVisitResult> {
        private IOException ex;
        private Path p;

        public askAction(IOException ex, Path p) {
            this.ex = ex;
            this.p = p;
        }
        
        @Override
        public FileVisitResult call() throws Exception {
//            FXDialogs.showException(owner, "Exception", "Exception in scan", "Exception in scan", ex);
//            FXDialogs.showError(owner, "Error", null, ex.toString());

            String result = FXDialogs.showConfirm(owner, Alert.AlertType.ERROR, "Error", null, ex.toString(), "Abort", "Skip", "Skip all");
            
            if ("Abort".equals(result)) {
                return FileVisitResult.TERMINATE;
            }
            
            if ("Skip all".equals(result)) {
                skipAll = true;
            }

            if (Files.isDirectory(p)) {
                return FileVisitResult.SKIP_SUBTREE;
            } else {
                return FileVisitResult.CONTINUE;
            }
        }
        
    }

    class askActionIfReadOnly implements Callable<FileVisitResult> {
        private IOException ex;
        private Path p;

        public askActionIfReadOnly(IOException ex, Path p) {
            this.ex = ex;
            this.p = p;
        }
        
        @Override
        public FileVisitResult call() throws Exception {

            String result = FXDialogs.showConfirm(owner, Alert.AlertType.ERROR, 
                    "Error", "Do you wish to delete it?", 
                    "The file ", "Abort", "Delete", "Delete all", "Skip", "Skip all");
            
            if ("Abort".equals(result)) {
                return FileVisitResult.TERMINATE;
            }
            
            if ("Skip all".equals(result)) {
                skipAll = true;
            }

            if (Files.isDirectory(p)) {
                return FileVisitResult.SKIP_SUBTREE;
            } else {
                return FileVisitResult.CONTINUE;
            }
        }
        
    }
    
}
