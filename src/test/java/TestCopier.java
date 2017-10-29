import code.project.fastCopy.data.DiskType;
import code.project.fastCopy.main.Copier;
import code.project.fastCopy.processor.CopyStatus;

public class TestCopier {
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        Copier.initialize();
        //Copier.copyFiles("D:\\Programming\\Elastic\\kibana-5.6.3-windows-x86","D:\\Programming\\Elastic2\\kibana-5.6.3-windows-x86", DiskType.SINGLE_DISK);
        Copier.copyFiles("D:\\Programming\\eclipse-workspace\\ocr","D:\\Programming2\\eclipse-workspace\\ocr", DiskType.SINGLE_DISK);
        System.out.println("Done");

        new Thread(new Runnable() {
            @Override
            public void run() {
                double percentComplete= CopyStatus.getPercentComplete();
                while (percentComplete<100){
                    System.out.println("Percent Complete: "+ percentComplete+ " Total File Size: "+CopyStatus.getTotalSourceFileSize() +" Copied: "+CopyStatus.getCopiedBytes());
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){

                    }
                    percentComplete= CopyStatus.getPercentComplete();
                }
                System.out.println("Completed!!");
                Copier.destroy();
            }
        }).start();
    }
}
