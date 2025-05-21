package in.batur.eksiclone.fileservice.util;

import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

@Component
public class FileUtils {
    
    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB"};
    
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) 
                + " " + UNITS[digitGroups];
    }
    
    public String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        return System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString() + extension;
    }
}
