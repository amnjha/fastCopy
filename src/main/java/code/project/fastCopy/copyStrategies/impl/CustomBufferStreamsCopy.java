package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.*;

/**
 * @author aman.jha
 */
public class CustomBufferStreamsCopy implements Copy {

    private static final int BUFFER = 8192;

    @Override
    public void copyFile(File source, File target) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);

            byte[] buf = new byte[BUFFER];

            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Copy.close(fis);
            Copy.close(fos);
        }
    }
}
