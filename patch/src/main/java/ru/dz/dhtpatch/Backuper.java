package ru.dz.dhtpatch;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
public class Backuper {
    public void backup(Path pathToOriginFile) throws IOException {
        Path pathToBackup = Paths.get(pathToOriginFile.getFileName().toString() + ".backup");
        Files.deleteIfExists(pathToBackup);
        Files.copy(pathToOriginFile,pathToBackup);
    }
}
