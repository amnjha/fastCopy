package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.*;

/**
 * @author aman.jha
 */
public class NativeReaderCopy implements Copy {
    @Override
    public void copyFile(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new FileReader(source);
            fout = new FileWriter(target);

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
