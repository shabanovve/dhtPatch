package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Vladimir Shabanov on 15/02/16.
 */
@Log
public class FileUtils {

    public static Path findFile(String fileName) throws URISyntaxException {
        String dir = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
        String pathToFile = dir + "/" + fileName;
        Path path = Paths.get(pathToFile);
        if (!Files.exists(path)) {
            throw new RuntimeException("File " + pathToFile + " not found");
        } else {
            log.info("File " + pathToFile + " is found");
        }
        return path;
    }


}
