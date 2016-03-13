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

    public static Path findFile(String pathToFile) throws URISyntaxException {
        Path path = Paths.get(pathToFile);
        if (!Files.exists(path)) {
            throw new RuntimeException("File " + pathToFile + " not found");
        } else {
            log.info("File " + pathToFile + " is found");
        }
        return path;
    }

    public static String returnCurrectDirectory() throws URISyntaxException {
        return new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
    }


}
