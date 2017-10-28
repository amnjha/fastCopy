package code.project.fastCopy.processor;

import code.project.fastCopy.copyStrategies.Copy;
import code.project.fastCopy.copyStrategies.CopyStrategySelector;
import code.project.fastCopy.data.DiskType;
import code.project.fastCopy.data.FileCategory;
import code.project.fastCopy.data.FileType;
import code.project.fastCopy.utils.CopyUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class CopyJob extends Job<Void> {

    private FileCategory fileCategory;
    private DiskType diskType;
    private File sourceFile;
    private File targetFile;

    /**
     * @param jobName String
     */
    public CopyJob(String jobName, DiskType diskType, FileCategory fileCategory, File sourceFile, File targetFile) {
        super(jobName);
        this.diskType=diskType;
        this.fileCategory = fileCategory;
        this.sourceFile=sourceFile;
        this.targetFile=targetFile;
    }

    /**
     * @return Return Value for the Job Done
     * @throws Exception - When the Implementation of Job causes an exception
     */
    @Override
    public Void doJob() throws Exception {
        if(!sourceFile.exists())
            throw new RuntimeException("Source File not found", new FileNotFoundException());

        long fileSize=(sourceFile.length()/1024); //file size in KB
        FileType fileType= CopyUtils.isBinaryFile(sourceFile)?FileType.BIN:FileType.TXT;
        FileCategory category= CopyUtils.categorizeFileByFileSize(fileSize,fileType);
        Copy copy= CopyStrategySelector.getCopyObjectByCopyStrategy(CopyUtils.decideCopyStrategy(category, diskType));
        copy.copyFile(sourceFile,targetFile);
        return null;
    }

}
