package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
@Log
public class Backuper {
    public void backup(File originFile) throws IOException {
        File backupFile = new File(originFile.getAbsolutePath().toString() + ".backup");
        if (backupFile.exists()) {
            backupFile.delete();
        }
        FileUtils.copy(originFile, backupFile);
    }

    public void revert(File file) {
        log.info("Revert from backup");
        try {
            File backupFile = FileUtils.findFile(Constant.BACKUP_FILE_NAME);
            file.delete();
            FileUtils.copy(backupFile, file);
            backupFile.delete();
        } catch (URISyntaxException e) {
            log.severe(e.getMessage());
        }

    }
}
