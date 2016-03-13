package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
@Log
public class Backuper {
    public void backup(Path pathToOriginFile) throws IOException {
        Path pathToBackup = Paths.get(pathToOriginFile.toString() + ".backup");
        Files.deleteIfExists(pathToBackup);
        Files.copy(pathToOriginFile,pathToBackup);
    }

    public void revert(Path pathToPatchedFile) {
        log.info("Revert from backup");
        try {
            Path backupPath = Paths.get(pathToPatchedFile.toString() + ".backup");
            Files.delete(pathToPatchedFile);
            Files.copy(backupPath, pathToPatchedFile);
            Files.delete(backupPath);
        } catch (IOException e) {
            log.severe(e.getMessage());
        }

    }
}
