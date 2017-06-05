/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.net.URI;
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
public class Explorer {
    public List<FileItem> ls(String path) throws URISyntaxException, IOException {
        long num = 0;
        long size = 0;
        FileItem fi;
        List<FileItem> list = new ArrayList<>();
        
        URI uri = new URI(path);
        Path p = Paths.get(uri);
        
            
        if (p.getParent() != null) {
            list.add(new FileItem(p.resolve("..")));
            num++;
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
            Iterator<Path> it = ds.iterator();
            while (it.hasNext()) {
                fi = new FileItem(it.next());
                if (Files.isRegularFile(fi.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                    size = size + fi.getSize();
                }
                list.add(fi);
                num++;
            }            
        return list;
        }
    }
    
}
