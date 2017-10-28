package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.*;

/**
 * @author aman.jha
 */
public class NativeStreamsCopy implements Copy {
    @Override
    public void copyFile(File source, File target) {
        InputStream fin = null;
        OutputStream fout = null;
        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);

            int c;
            while ((c = fin.read()) != -1) {
                fout.write(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Copy.close(fin);
            Copy.close(fout);
        }
    }
}
