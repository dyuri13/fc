/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.copy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
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
import javafx.scene.control.Alert;
import javafx.stage.Window;
import org.dy.fc.AskActionInTask;
import org.dy.fc.FileItem;
import org.dy.fc.TaskExt;
import org.dy.fc.Utils;
import org.dy.fc.operations.ScanResult;

/**
 *
 * @author dubin
 */
public class TaskCopy extends TaskExt<ScanResult>{

    private final List<FileItem> list;
    
    private long count;
    
    private Path target;
    private boolean skipAllIfExist = false;
    private boolean replaceAllIfExist = false;
    private boolean skipAllIfException = false;
    private boolean replaceAllIfException = false;
    private boolean copyAttr = true;
    
    private long sizeAll = -1L;
    private long sizeCopy = 0L;
    
    public TaskCopy(List<FileItem> list, Window window, long numItems, Path target) {
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
    
    
    private void updateInfo(Path source, Path target) {
//        long size = -1;
//        if (Files.isRegularFile(p)) {
//            try {
//                size = Files.size(p);
//            } catch (IOException ex) {
//                Logger.getLogger(TaskCopy.class.getName()).log(Level.SEVERE, null, ex);
//                size = -1;
//            }
//        }
//        updateTitle("Copy : " + p.getFileName().toString() + " Size : " + Utils.toNumInUnits(sizeAll));
        updateTitle(String.valueOf(maxProgress) + " / " + count);
        updateMessage("Copying : " + source.getFileName() + "\n" + "To : " + target + "\n" + "1111111111111111");
        updateProgress(sizeCopy, sizeAll);
    }
    
    @Override
    protected void succeeded() {
        updateTitle("Succeeded copy");
        updateProgress(0, 0);
//        sr.setComplete(true);
//        updateValue(sr);
//        System.out.println("sr in saccessed task : " + sr);
    }

    @Override
    protected void cancelled() {
        updateTitle("Cancelled copy");
//        System.out.println("Cancelled in task");
        updateProgress(0, 0);
//        sr.setComplete(false);
//        updateValue(sr);
    }

    @Override
    protected void failed() {
        updateTitle("Failed copy");
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
            
            
        }
        return null;
    }

    
    private void copyFileUsingChannel(Path source, Path target, int block_size) throws IOException {
        boolean done = false;
        long countRead, countWrite;
        
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            System.out.println("in copyFileUsingChannel : source : " + source);
            sourceChannel = new FileInputStream(source.toFile()).getChannel();
            destChannel = new FileOutputStream(target.toFile()).getChannel();

            ByteBuffer buff = ByteBuffer.allocate(block_size);
        
            countRead = 0;
            countWrite = 0;
            sizeAll = sourceChannel.size();
            
            while ( (sourceChannel.read(buff) != -1) || (buff.position() > 0) ) {
//                System.err.println("countRead : " + countRead++);
//                System.out.println("copyFileUs... befor read halt is : " + halt);

                countRead = countRead + buff.position();
                
                if (halt) {
                    System.out.println("copyFileUs... halt1 is : " + halt);
                    halt();
                }

                if (isCancelled()) {
                    break;
                }
                
//                System.out.println("copyFileUs... before readbuff");

//                System.out.println("copyFileUs... befor write halt is : " + halt);
                
                buff.flip();
//                System.out.println("copyFileUs... before writebuff");
                
                destChannel.write(buff);
                sizeCopy = countRead;
                updateInfo(source, target);
//                System.err.println("countWrite : " + countWrite++);
//                System.out.println("copyFileUs... after writebuff");
                buff.compact();
//                if (halt) {
//                    System.out.println("copyFileUs... halt2 is : " + halt);
//                    halt();
//                }
//
//                if (isCancelled()) {
//                    break;
//                }
                
            }



//           } catch (IOException ex) {
//               throw ex;
            } finally {
                if (sourceChannel != null) {
//                    System.out.println("copyFileUs... sourceChannel.close start");
                    sourceChannel.close();
//                    System.out.println("copyFileUs... sourceChannel.close start");
                }
                if (destChannel != null) {
                    destChannel.close();
                }
       }
    }    
    
    private void copyFileUsingChannel(Path source, Path target) throws IOException {
        final int BUFF_SIZE = 1024 * 4;
        copyFileUsingChannel(source, target, BUFF_SIZE);
    }
    
    
    class Visitor implements FileVisitor<Path>{
        
        private final Path source;
        private final Path target;

        public Visitor(Path source, Path target) {
            this.source = source;
            this.target = target;
        }

        

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            CopyOption[] options = new CopyOption[] {StandardCopyOption.COPY_ATTRIBUTES};
            
            Path newDir = target.resolve(source.relativize(dir));
//            Path newDir = target.resolve(source.getFileName());
            
            count++;
            updateInfo(dir, target);
            
            if (halt) {
                halt();
            }

            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            try {
                System.out.println("preVisitDirectory: ");
                System.out.println("!!! Files.copy : " + dir + " to: " + newDir);
                Files.copy(dir, newDir, options);
            } catch (FileAlreadyExistsException ex){
                // ignore
                System.out.println("FileAlreadyExistsException: " + newDir);
                
            } catch (IOException ex) {
                
                System.err.format("Unable to create: %s: %s%n", newDir, ex);
                if (!skipAllIfException) {
                
                    AskActionInTask ask = new AskActionInTask(owner, Alert.AlertType.ERROR, ex.toString(), "Skip", "Skip all");
                    String s = ask.get();

                    if ("Skip".equals(s)) {
                        return FileVisitResult.CONTINUE;
                    }

                    if ("Skip all".equals(s)) {
                        skipAllIfException = true;
                        return FileVisitResult.CONTINUE;
                    }

                    if ("Abort".equals(s)) {
                        skipAllIfException = true;
                        cancel(true);
                        return FileVisitResult.TERMINATE;
                    }
                    
                }
                
            } catch (Exception ex) {
                System.out.println(ex);
            }
            return FileVisitResult.CONTINUE;
        }

        
        
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            count++;
            updateInfo(file, target);
            System.out.println("visetFile : " + file);
            
            if (halt) {
                halt();
            }
            
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }

            System.out.println("copy file : " + file + " to : " + target.resolve(source.relativize(file)));
            
//            CopyFile(file, target.resolve(source.relativize(file)));
            try {
                if (Files.exists(target.resolve(source.relativize(file)))) {
                    
                    if (skipAllIfExist) {
                        return FileVisitResult.CONTINUE;
                    }
                    
                    if (replaceAllIfExist) {
                        copyFileUsingChannel(file, target.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                    
                    updateProgress(0, 0);
//                        updateProgress(-1, -1);

                    AskActionInTask ask = new AskActionInTask(owner, Alert.AlertType.CONFIRMATION, "File " +
                            target.resolve(source.relativize(file)).toString() +" allready exist!", "Skip", "Skip all", "Replace", "Replace all");
                        
                    String s = ask.get();

                    if ("Skip".equals(s)) {
                        return FileVisitResult.CONTINUE;
                    }

                    if ("Skip all".equals(s)) {
                        skipAllIfExist = true;
                        return FileVisitResult.CONTINUE;
                    }

                    if ("Abort".equals(s)) {
                        skipAllIfExist = true;
                        cancel(true);
                        return FileVisitResult.TERMINATE;
                    }
                        
                    if ("Replace".equals(s)) {
                        copyFileUsingChannel(file, target.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                        
                    if ("Replace all".equals(s)) {
                        replaceAllIfExist = true;
                        copyFileUsingChannel(file, target.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                } else { // file not exist
                    copyFileUsingChannel(file, target.resolve(source.relativize(file)));
                    return FileVisitResult.CONTINUE;
                }
            } catch (ClosedByInterruptException ex) {
                System.err.println("???" + ex);

            } catch (IOException ex) {
                if (!skipAllIfException) {
                    
                    
                    updateProgress(0, 0);
//                    updateProgress(-1, -1);
                    
                    AskActionInTask ask = new AskActionInTask(owner, Alert.AlertType.ERROR, ex.toString(), "Skip", "Skip all");
                    String s = ask.get();
                    System.out.println("ask is : " + s);

                    if ("Skip".equals(s)) {
                        return FileVisitResult.CONTINUE;
                    }

                    if ("Skip all".equals(s)) {
                        skipAllIfException = true;
                        return FileVisitResult.CONTINUE;
                    }

                    if ("Abort".equals(s)) {
                        skipAllIfException = true;
                        cancel(true);
                        return FileVisitResult.TERMINATE;
                    }
                    
                }

            }
            
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//            count++;
//            updateInfo(dir);
            
            System.out.println("postVisetDirectory : " + dir);
            
            if (halt) {
                halt();
            }
            
            if (isCancelled()) {
                return FileVisitResult.TERMINATE;
            }
            
            
//            System.out.println("postViditDerectoty dir : " + dir);
            if (exc == null) {
                
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
                System .err.format("Unable to copy: %s: %s%n", file, exc);
            }
                
            return FileVisitResult.CONTINUE;
        }
    }
    
}
