package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.*;

/**
 * @author aman.jha
 */
public class BufferedStreamsCopy implements Copy {
    @Override
    public void copyFile(File source, File target) {
        InputStream fin = null;
        OutputStream fout = null;
        try {
            fin = new BufferedInputStream(new FileInputStream(source));
            fout = new BufferedOutputStream(new FileOutputStream(target));

            int data;
            while ((data = fin.read()) != -1) {
                fout.write(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Copy.close(fin);
            Copy.close(fout);
        }
    }
}
