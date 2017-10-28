package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.*;

public class CustomBufferReaderCopy implements Copy {

    private static final int BUFFER = 8192;

    @Override
    public void copyFile(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new FileReader(source);
            fout = new FileWriter(target);

            char[] buf = new char[BUFFER / 2];

            int i;
            while ((i = fin.read(buf)) != -1) {
                fout.write(buf, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Copy.close(fin);
            Copy.close(fout);
        }
    }
}
