package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.*;

/**
 * @author aman.jha
 */
public class BufferedReaderCopy implements Copy{


    @Override
    public void copyFile(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new BufferedReader(new FileReader(source));
            fout = new BufferedWriter(new FileWriter(target));

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
