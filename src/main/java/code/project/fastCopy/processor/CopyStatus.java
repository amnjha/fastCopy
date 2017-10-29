package code.project.fastCopy.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CopyStatus {
    private static final BlockingQueue<Long> COPIED_BYTES = new LinkedBlockingQueue<>();
    private static final Thread COPY_STATUS_THREAD = new CopyStatusUpdater();
    private static long copiedBytes = 0l;
    private static double percentComplete = 0.0;
    private static long totalSourceFileSize = 0l;

    public static void updateCopiedBytes(long bytes) {
        COPIED_BYTES.add(bytes);
    }

    public static void setTotalSourceFileSize(long sourceFileSize){
        CopyStatus.totalSourceFileSize=sourceFileSize;
    }

    public static double getPercentComplete() {
        return percentComplete;
    }

    public static long getTotalSourceFileSize() {
        return totalSourceFileSize;
    }

    public static long getCopiedBytes(){
        return copiedBytes;
    }

    @Override
    protected void finalize() throws Throwable {
        COPY_STATUS_THREAD.interrupt();
        super.finalize();
    }

    static class CopyStatusUpdater extends Thread {

        public CopyStatusUpdater() {
            this.start();
            this.setName("COPY_STATUS_THREAD");
        }

        @Override
        public void run() {
            try {
                while (percentComplete < 100) {
                    long bytes = COPIED_BYTES.take();
                    copiedBytes += bytes;
                    if(totalSourceFileSize==0){
                        percentComplete=0.0;
                        continue;
                    }
                    percentComplete = (new Double(copiedBytes) / totalSourceFileSize) * 100;
                }
                setTotalSourceFileSize(0l);
            } catch (InterruptedException e) {

            }

        }
    }
}
