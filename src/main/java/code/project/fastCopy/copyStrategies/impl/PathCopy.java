package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author aman.jha
 */
public class PathCopy implements Copy {
    @Override
    public void copyFile(File source, File target) {
        try {
            Files.copy(source.toPath(), target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
