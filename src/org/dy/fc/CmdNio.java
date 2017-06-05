/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author yuri
 */
public class CmdNio implements Cmd{

    private long allSize = 0;
    private long allNum = 0;
    
    @Override
    public List<FileItem> ls(Path p) throws IOException {
        long num = 0;
        long size = 0;
        FileItem fi;
        List<FileItem> list = new ArrayList<>();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
            
            if (p.getParent() != null) {
                list.add(new FileItem(p.resolve("..")));
                num++;
            }
            
            Iterator<Path> it = ds.iterator();
            while (it.hasNext()) {
                fi = new FileItem(it.next());
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    size = size + fi.getSize();
                }
                list.add(fi);
                num++;
            }
        allSize = size;
        allNum = num;
        return list;
        }
    }

    @Override
    public void ls(Path p, List<FileItem> list) throws IOException {
        long num = 0;
        long size = 0;
        FileItem fi;
        long start = 0;
            

        start = System.currentTimeMillis();
        
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
            list.clear();
            if (p.getParent() != null) {
                list.add(new FileItem(p.resolve("..")));
                num++;
            }
            
            Iterator<Path> it = ds.iterator();
            while (it.hasNext()) {
                fi = new FileItem(it.next());
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    size = size + fi.getSize();
                }
                list.add(fi);
                num++;
            }
        allSize = size;
        allNum = num;
        
        System.out.println("ls nio time : " + (System.currentTimeMillis() - start));
        }
    }

    
    @Override
    public long getNum() {
        return allNum;
    }

    
    @Override
    public long getSize() {
        return allSize;
    }
    
    public static void main(String[] args) throws URISyntaxException, IOException {
        
        Cmd cmd = new CmdNio();
        
//        List<FileItem> l = cmd.ls("jar:file:/Users/yuri/Downloads/1.zip");
        List<FileItem> l = cmd.ls(Paths.get("C://Users"));
        
        for (FileItem fi : l) {
            System.out.println(fi.toString());
        }
        
    }


    
}
