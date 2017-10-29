package code.project.fastCopy.main;

import code.project.fastCopy.data.DiskType;
import code.project.fastCopy.processor.*;
import code.project.fastCopy.utils.CopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

public class Copier {
    private static final JobProcessor JOB_PROCESSOR= ParallelJobProcessor.getInstance();
    private static final Logger LOGGER= LoggerFactory.getLogger(Copier.class);
    private static long totalFileSize= 0L;
    private static boolean isInitialized=false;

    /**
     * Initializes the copy instance. Calling this method twice during a JVM running has no impact
     * if this method is called, you must call {@link Copier}.destroy()
     * otherwise your JVM wouldn't shutdown.
     */
    public static void initialize(){
    	if(isInitialized)
    		return;

    	ParallelJobProcessor.getInstance().initialize();
    	isInitialized=true;
    }

    /**
     * This method must be called when JVM is shutting down, otherwise you cannot use Copier anymore until JVM is restarted.
     * This method will halt your JVM shutdown until all copy tasks submitted is completed.
     */
    public static void destroy(){
        ParallelJobProcessor.getInstance().cleanUp(false);
    }
    
    /**
     * This method must be called when JVM is shutting down, otherwise you cannot use Copier anymore until JVM is restarted.
     * This method will interrupt all running tasks, causing them to fail and raise {@link Exception}.
     */
    public static void destroyNow() {
    	ParallelJobProcessor.getInstance().cleanUp(true);
    }

    public static void copyFiles(String sourcePath, String destPath, DiskType diskType) throws Exception {
        if (!isInitialized)
            throw new RuntimeException("Copier not initialized. Call Copier.initialize() to use this method") ;
        File sourceFile= new File(sourcePath);
        if(!sourceFile.exists())
            throw new FileNotFoundException();

        if (sourceFile.isDirectory()){
            CopyUtils.createDirectoryIfNotExists(destPath);
            copyDirectory(sourceFile,destPath,diskType);
        }
        else {
            Job copyJob = new CopyJob(sourceFile.getName(), diskType, sourceFile, new File(destPath));
            copyJob.doJob();
            totalFileSize+=sourceFile.length()/1024;
        }
        if(totalFileSize!=0)
            CopyStatus.setTotalSourceFileSize(totalFileSize);
        else
        {
            CopyStatus.setTotalSourceFileSize(1L);
            CopyStatus.updateCopiedBytes(1L);
        }
    }

    private static void copyDirectory(File source, String destinationPath, DiskType diskType){
        if(source.isFile())
            return;

        String  destinationFilePath=null;
        for(File file: source.listFiles()){
            destinationFilePath=destinationPath+"/"+file.getName();
            if(file.isDirectory()){
                LOGGER.info("Creating Directory: "+ destinationFilePath);
                CopyUtils.createDirectoryIfNotExists(destinationFilePath);
                copyDirectory(file, destinationFilePath,diskType);
            }
            else {
                totalFileSize+=file.length()/1024;
                Job copyJob = new CopyJob(file.getName(), diskType, file, new File(destinationFilePath));
                JOB_PROCESSOR.executeJob(copyJob);
            }
        }
    }
}
