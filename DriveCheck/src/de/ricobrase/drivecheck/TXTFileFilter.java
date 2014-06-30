package de.ricobrase.drivecheck;

import java.io.File;
import java.io.FileFilter;

public class TXTFileFilter implements FileFilter{

	String description = "";
    String fileExt = "";

    public TXTFileFilter(String extension) {
        fileExt = extension;
    }

    public TXTFileFilter(String extension, String typeDescription) {
        fileExt = extension;
        this.description = typeDescription;
    }

    public boolean accept(File f) {
        if (f.isDirectory())
            return false;
        return (f.getName().toLowerCase().endsWith(fileExt));
    }

    public String getDescription() {
        return description;
    }
}