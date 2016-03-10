package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.*;
import java.net.URISyntaxException;

/**
 * Created by Vladimir Shabanov on 15/02/16.
 */
@Log
public class FileUtils {

    public static File findFileInCurrentDirectory(String fileName) throws URISyntaxException, FileNotFoundException {
        String dir = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
        String pathToFile = dir + "/" + fileName;
        File file = new File(pathToFile);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + pathToFile + " not found");
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
            log.severe("IO error");
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

    public static File findTargetFile(String path) throws URISyntaxException {
        File result = null;

        result = findInCurrentDirectory();
        if (result != null)
            return result;

        result = findFile(path);
        if (result != null)
            return result;

        if (result == null)
            throw new RuntimeException("File not found");

        return result;
    }

    public static File findFile(String path) throws URISyntaxException {
        File result = null;
        result = new File(path);

        return result.exists() ? result : null;
    }

    public static File findInCurrentDirectory() throws URISyntaxException {
        File result = null;
        try {
            result = findFileInCurrentDirectory(Constant.FILE_NAME);
        } catch (FileNotFoundException e) {
            log.warning("No " + Constant.FILE_NAME + " in current directory");
        }
        return result;
    }
}
