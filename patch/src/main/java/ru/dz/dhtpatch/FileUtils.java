package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.*;
import java.net.URISyntaxException;

/**
 * Created by Vladimir Shabanov on 15/02/16.
 */
@Log
public class FileUtils {

    public static File findFile(String fileName) throws URISyntaxException {
        String dir = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
        String pathToFile = dir + "/" + fileName;
        File file = new File(pathToFile);
        if (!file.exists()) {
            throw new RuntimeException("File " + pathToFile + " not found");
        } else {
            log.info("File " + pathToFile + " is found");
        }
        return file;
    }


    public static void copy(File originFile, File backupFile) {
        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            inStream = new FileInputStream(originFile);
            outStream = new FileOutputStream(backupFile);

            byte[] buffer = new byte[Constant.BUFFER_SIZE];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }

            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }

            }
        }
    }
}
