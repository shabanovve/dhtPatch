package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
@Log
public class Backuper {
    public void backup(Path pathToOriginFile) throws IOException {
        Path pathToBackup = Paths.get(pathToOriginFile.getFileName().toString() + ".backup");
        Files.deleteIfExists(pathToBackup);
        Files.copy(pathToOriginFile,pathToBackup);
    }

    public void revert(Path path) {
        log.info("Revert from backup");
        try {
            Path backupPath = FileUtils.findFile(Constant.BACKUP_FILE_NAME);
            Files.delete(path);
            Files.copy(backupPath, path);
            Files.delete(backupPath);
        } catch (IOException | URISyntaxException e) {
            log.severe(e.getMessage());
        }

    }
}
