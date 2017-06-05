/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.ImageView;
import org.dy.fc.util.Os;

/**
 *
 * @author dubin
 */
public class FileItem {
    
    private final Path p;
    private final String name;
    private final String type;
    private long size;
    private FileTime dateTime;
    private boolean hidden;
    private final ImageView icon;
    private boolean selected = false;
    
    public FileItem(String s){
        this(Paths.get(s));
    }

    public FileItem(URI uri) {
        this(Paths.get(uri));
    }
    
    public FileItem(Path p) {
        this.p = p;
        
        name = (p.getFileName() != null ? p.getFileName().toString() : p.toString());

        
        try {
            size = Files.size(p);
        } catch (IOException ex) {
            size = -1L;
            Logger.getLogger(FileItem.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        try {
            dateTime = Files.getLastModifiedTime(p, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException ex) {
            dateTime = null;
            Logger.getLogger(FileItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            hidden = Files.isHidden(p);
        } catch (IOException ex) {
            hidden = false;
            Logger.getLogger(FileItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        type = _getType(p);
        
//        icon = getImageView(p);
        
//        if (Os.isWindows()) {
//            if ("..".equals(this.getName().toString())) {
//                icon = IconManager.getInstance().getIcon("parent");
//            } else {
//                icon = IconManager.getInstance().getIconfromSystemBySwing(p);
//            }
//        } else {
            icon = getImageView(p);
//        }


        
    }
    
    public String getName() {
        return name;
    }
    
    
    public Path getPath() {
        return p;
    }
    
    private String getExt(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');
        if (i > 0) {
            ext = s.substring(i+1);
        }        
        return ext;
    }
    
    public String getType() {
        return type;
    }
    
    private String _getType(Path p) {
        final String DIR = "DIR";
        final String FILE = "FILE";
        final String LINK = "LINK";
        final String UP = "UP";
        final String UNKNOWN = "UNKNOWN";
        
        String result = "";
        if (Files.isSymbolicLink(p)) {
            result = result + LINK;
            if (Files.isDirectory(p)) {
                result = result + "/" + DIR;
            } else {
                if (Files.isRegularFile(p)) {
                    result = result + "/" + FILE;
                }
            }
            
        } else {
            if (Files.isDirectory(p)) {
                String name = (p.getFileName() != null ? p.getFileName().toString() : "");
                if ("..".equals(name)) {
                    result = result + UP;
                } else {
                    result = result + DIR;
                }
                
            }
            
            if (Files.isRegularFile(p)) {
                result = result + getExt(p.getFileName().toString());
            }
            
        }
        
        return result;
    }
    
    private String toNumInUnits(long bytes) {
        int u = 0;
        for (;bytes > 1024*1024; bytes >>= 10) {
            u++;
        }
        if (bytes > 1024)
            u++;
        return String.format("%.1f %cB", bytes/1024f, " kMGTPE".charAt(u));
    }    
    
    public long getSize() {
        return size;
    }
    
    public FileTime getDateTime() {
        return dateTime;
    }
    

    public boolean isHidden() {
        return hidden;
    }
    
    public ImageView getIcon() {
        return icon;
    }
    
    public FileItem getThis() {
        return this;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean b) {
        selected = b;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof FileItem) {
            FileItem fi = (FileItem) obj;
            try {
                if ( (Files.isSameFile(fi.getPath(), this.getPath())) && (fi.getName().equals(this.getName())) ) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException ex) {
                result = false;
            }
        }
        return result;
    }
    
    private ImageView getImageView(Path f) {
        IconManager im = IconManager.getInstance();
        
        ImageView imageView = null;
        
        if (Files.isSymbolicLink(f)) {
            if (Files.isDirectory(f)) {
                return (im.getCompositeIcon("folder", "link"));
            } else {
                if (Files.isRegularFile(f)) {
                    return (im.getCompositeIcon("file", "link"));
                } else {
                    return (im.getIcon("link"));
                }
            }
        }

        if (Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS)) {
            if ("..".equals( (f.getFileName() != null ? f.getFileName().toString() : f.toString()) )) {
                //imageView = im.getIcon("parent");
                return im.getIcon("parent");
            } else {
                //imageView = im.getIcon("folder");
                return im.getIcon("folder");
            }
        }
        
        if (Files.isRegularFile(f, LinkOption.NOFOLLOW_LINKS)) {
            imageView = im.getIcon(getExt(f.getFileName().toString()));
            if (imageView == null) {
                //imageView = im.getIcon("file");
//                System.out.println("File not found!!!!!!!!!!!!!!!!!!!!!!"+f);
                imageView = im.getIcon("file");
//                System.out.println("File not found!!!!!!!!!!!!!!!!!!!!!!" + imageView );
                //return im.getIcon("file");
                return imageView;
            }
        }
        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return imageView;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 89 * hash + Objects.hashCode(this.dateTime);
        hash = 89 * hash + (this.hidden ? 1 : 0);
//        hash = 89 * hash + (this.selected ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.icon);
        return hash;
    }
    
@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(" name : ");
        sb.append(getName());
        sb.append(", size : ");
        sb.append(getSize());
        sb.append(", hidden : ");
        sb.append(isHidden());
        sb.append(", dateTime : ");
        sb.append(getDateTime());
        sb.append(", icon : ");
        sb.append(getIcon());
        sb.append(" ]");
        return sb.toString();
//        return p.toString();
    }    
    
    
}
