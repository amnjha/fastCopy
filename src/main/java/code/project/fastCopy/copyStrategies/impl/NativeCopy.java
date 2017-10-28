package code.project.fastCopy.copyStrategies.impl;

import code.project.fastCopy.copyStrategies.Copy;

import java.io.File;
import java.io.IOException;

/**
 *  Uses cp command from linux os, other environments are currently not supported.
 *
 *  @author aman.jha
 */
public class NativeCopy implements Copy {

    @Override
    public void copyFile(File source, File target) {
        final String OS_NAME=System.getProperty("os.name").toLowerCase();

        if(!(OS_NAME.indexOf("nix")>=0))
            throw new UnsupportedOperationException("This Method is only Supported for Unix Operating System!");

        Process p = null;
        try {
            p = Runtime.getRuntime().exec(
                    new String[]{
                            "/bin/cp",
                            source.getAbsolutePath(),
                            target.getAbsolutePath()
                    });

            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                Copy.close(p.getInputStream());
                Copy.close(p.getErrorStream());
                Copy.close(p.getOutputStream());

                p.destroy();
            }
        }
    }
}
