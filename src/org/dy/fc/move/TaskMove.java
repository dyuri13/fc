/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.move;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.stage.Window;
import org.dy.fc.FXDialogs;
import org.dy.fc.FileItem;
import org.dy.fc.TaskExt;
import org.dy.fc.operations.ScanResult;

/**
 *
 * @author dubin
 */
public class TaskMove extends TaskExt<ScanResult>{

    private final List<FileItem> list;
    
    private long count;
    
    private Path target;
    private boolean skipAllIfExist = false;
    private boolean replaceAllIfExist = false;
    private boolean skipAllIfException = false;
    private boolean replaceAllIfException = false;
    private boolean copyAttr = true;
    
    public TaskMove(List<FileItem> list, Window window, long numItems, Path target) {
        super(window);
        this.list = list;
        maxProgress = numItems;
        this.target = target;
        System.out.println("target is : " + target);
    }

/*    
    public TaskCopy(List<FileItem> list, long count, Window window) {
        super(window, 0);
        this.list = list;
        this.count = count;
    }
*/    
    
    
    private void updateInfo(Path p) {
        long size = -1;
        if (Files.isRegularFile(p)) {
            try {
                size = Files.size(p);
            } catch (IOException ex) {
                Logger.getLogger(TaskMove.class.getName()).log(Level.SEVERE, null, ex);
                size = -1;
            }
        }
        updateTitle("Move : " + p.getFileName().toString() + " Size : " + size);
        updateMessage("Move : " + count + " of " + maxProgress);
        updateProgress(count, maxProgress);
    }
    
    @Override
    protected void succeeded() {
        updateTitle("Succeeded move");
        updateProgress(0, 0);
//        sr.setComplete(true);
//        updateValue(sr);
//        System.out.println("sr in saccessed task : " + sr);
    }

    @Override
    protected void cancelled() {
        updateTitle("Cancelled move");
//        System.out.println("Cancelled in task");
        updateProgress(0, 0);
//        sr.setComplete(false);
//        updateValue(sr);
    }

    @Override
    protected void failed() {
        updateTitle("Failed move");
        updateProgress(0, 0);
//        sr.setComplete(false);
//        updateValue(sr);
    }
    
    
    @Override
    protected ScanResult call() throws Exception {
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        Path newTarget;
//        Visitor visitor = new Visitor();
        
        for (FileItem fi : list) {
            
            if (halt) {
                halt();
            }

            if (isCancelled()) {
                break;
            }
            
            Files.walkFileTree(fi.getPath(), new Visitor(fi.getPath(), target.resolve(fi.getPath().getFileName())));
//            Files.walkFileTree(fi.getPath(), new Visitor(fi.getPath(), target.resolve(fi.getPath())));
            
        }
        return null;
    }

   
    private void MoveFile(Path source, Path target) {
        CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
            if (halt) {
                halt();
            }

            if (isCancelled()) {
                return;
            }
        
        try {
            Files.move(source, target, options);
        } catch (IOException ex) {
            System.out.format("Unable to move : %s: %s%n", source, ex);
            System.out.println("!!! source : " + source + " target : " + target);
            Logger.getLogger(TaskMove.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    class Visitor implements FileVisitor<Path>{
        
        private Path source;
        private Path target;

        public Visitor(Path source, Path target) {
            this.source = source;
            this.target = target;
        }

        

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};// {StandardCopyOption.ATOMIC_MOVE};
            
            Path newDir = target.resolve(source.relativize(dir));
//            Path newDir = target.resolve(source.getFileName());
            
            count++;
            updateInfo(dir);

            if (halt) {
                halt();
            }

            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            System.out.println("preVisitDirectory: ");
            
            try {
                System.out.println("preVisitDirectory: ");
                System.out.println("!!! Files.move : " + dir + " to: " + newDir);
//                Files.move(dir, newDir, options);
                Files.copy(dir, newDir, options);
                System.out.println("preVisitDirectory after move : ");
            } catch (FileAlreadyExistsException ex){
//                 ignore
                System.out.println("FileAlreadyExistsException: " + newDir);

            } catch (AtomicMoveNotSupportedException ex) {
                System.out.println("AtomicMoveNotSupportedException : " + ex);
            } catch (IOException ex) {
                System.out.println("IOException : " + ex);
                System.err.format("Unable to create: %s: %s%n", newDir, ex);
            } catch (Exception ex) {
                System.out.println("Exception : " + ex);
            }

            return FileVisitResult.CONTINUE;
        }

        
        
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            count++;
            updateInfo(file);
            System.out.println("visetFile : " + file);
            
            if (halt) {
                halt();
            }
            
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }

            System.out.println("move file : " + file + " to : " + target.resolve(source.relativize(file)));
            
            MoveFile(file, target.resolve(source.relativize(file)));
//            CopyFile(file, target.resolve(source.getFileName()));
            
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
            count++;
            updateInfo(dir);

            Path newDir = target.resolve(source.relativize(dir));
            System.out.println("postVisetDirectory : " + dir);
            
            if (halt) {
                halt();
            }
            
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            
//            System.out.println("postViditDerectoty dir : " + dir);
            if (exc == null) {


            try {
                System.out.println("postVisitDirectory: ");
//                System.out.println("!!! Files.move : " + dir + " to: " + newDir);
//                Files.move(dir, newDir, options);
                Files.delete(dir);
            } catch (FileAlreadyExistsException ex){
                // ignore
                System.out.println("FileAlreadyExistsException: " + newDir);
                
            } catch (IOException ex) {
                System.err.format("Unable to create: %s: %s%n", newDir, ex);
            }

                
//                Path newDir = target.resolve(source.relativize(dir));
//                Path newDir = target.resolve(source.getFileName());
                
//                try {
//                    FileTime time = Files.getLastModifiedTime(dir);
//                    Files.setLastModifiedTime(newDir, time);
//                } catch (IOException ex) {
//                    System.err.format("Unable to copy all attributes to: %s: %s%n", newDir, ex);
//                }
            }
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//            count++;
//            updateInfo(file);
            
            System.out.println("visetFileFailed : " + file);
            System.out.println("IOException : " + exc);
            
            if (halt) {
                halt();
            }
            
            
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            if (exc instanceof FileSystemLoopException) { 
                System.err.println("cycle detected: " + file);
            } else {
                System .err.format("Unable to move: %s: %s%n", file, exc);
            }
                
            return FileVisitResult.CONTINUE;
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

    class askActionIfException implements Callable<FileVisitResult> {
        private IOException ex;
        private Path p;

        public askActionIfException(IOException ex, Path p) {
            this.ex = ex;
            this.p = p;
        }
        
        @Override
        public FileVisitResult call() throws Exception {

            String result = FXDialogs.showConfirm(owner, Alert.AlertType.ERROR, "Error", null, ex.toString(), "Abort", "Skip", "Skip all");
            
            if ("Abort".equals(result)) {
                return FileVisitResult.TERMINATE;
            }
            
            if ("Skip all".equals(result)) {
                skipAllIfException = true;
            }

            if (Files.isDirectory(p)) {
                return FileVisitResult.SKIP_SUBTREE;
            } else {
                return FileVisitResult.CONTINUE;
            }
        }
        
    }
    
}
