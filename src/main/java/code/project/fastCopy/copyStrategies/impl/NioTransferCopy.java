package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class NioTransferCopy implements Copy {
    @Override
    public void copyFile(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;

        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(target).getChannel();

            long size = in.size();
            long transferred = in.transferTo(0, size, out);

            while (transferred != size) {
                transferred += in.transferTo(transferred, size - transferred, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Copy.close(in);
            Copy.close(out);
        }
    }
}
