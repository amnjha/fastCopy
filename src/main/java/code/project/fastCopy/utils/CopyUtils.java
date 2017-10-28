package code.project.fastCopy.utils;

import code.project.fastCopy.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class CopyUtils {

    private static final OperatingSystem OPERATING_SYSTEM = findOperatingSystem();

    public static CopyStrategy decideCopyStrategy(FileCategory fileCategory, DiskType diskType){
        Objects.requireNonNull(fileCategory,"FileCategory cannot be null");
        Objects.requireNonNull(diskType,"DiskType cannot be null");
        if(DiskType.SINGLE_DISK.equals(diskType)){
            switch (fileCategory){
                case ENORMOUS_BIN_FILE:
                    if (OperatingSystem.UNIX.equals(getOperatingSystem()))
                        return CopyStrategy.NATIVE_COPY;
                    else
                        return CopyStrategy.NIO_BUFFER;
                default:
                    return CopyStrategy.NIO_TRANSFER;
            }
        }
        else{
            switch (fileCategory){
                case ENORMOUS_BIN_FILE:
                    return CopyStrategy.NIO_BUFFER;
                default:
                    return CopyStrategy.NIO_TRANSFER;
            }
        }
    }

    public static FileCategory categorizeFileByFileSize(long fileSizeInKB, FileType fileType){
        if(FileType.BIN.equals(fileType)){
            if(fileSizeInKB<50)
                return FileCategory.SMALL_BIN_FILE;
            else if(fileSizeInKB/1024<5)
                    return FileCategory.MEDIUM_BIN_FILE;
            else if((fileSizeInKB/1024)<250)
                return FileCategory.LARGE_BIN_FILE;
            else
                return FileCategory.ENORMOUS_BIN_FILE;
        }
        else if(FileType.TXT.equals(fileType)){
            if(fileSizeInKB<50)
                return FileCategory.SMALL_TXT_FILE;
            else if(fileSizeInKB/1024<5)
                return FileCategory.MEDIUM_TXT_FILE;
            else if((fileSizeInKB/1024)<250)
                return FileCategory.LARGE_TXT_FILE;
            else
                return FileCategory.ENORMOUS_TXT_FILE;
        }
        return FileCategory.DEFAULT;
    }

    private static OperatingSystem findOperatingSystem(){
        String OS=System.getProperty("os.name").toLowerCase();

        if(OS.indexOf("win") >= 0)
            return OperatingSystem.WINDOWS;
        else if (OS.indexOf("mac") >= 0)
            return OperatingSystem.MAC;
        else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 )
            return OperatingSystem.UNIX;
        else if (OS.indexOf("sunos") >= 0)
            return OperatingSystem.SOLARIS;
        else
            return OperatingSystem.OTHER;
    }

    public static OperatingSystem getOperatingSystem(){
        return OPERATING_SYSTEM;
    }

    public static boolean isBinaryFile(File f) throws IOException {
        String type = Files.probeContentType(f.toPath());
        if (type == null) {
            //type couldn't be determined, assume binary
            return true;
        } else if (type.startsWith("text")) {
            return false;
        } else {
            //type isn't text
            return true;
        }
    }
}
