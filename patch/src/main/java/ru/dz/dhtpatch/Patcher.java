package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
@Log
public class Patcher {

    public void start() {
        try {
            Path path = findFile();
            if (isFilePatched(path)) {
                revertFromBackup(path);
            } else {
                makePatch(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void makePatch(Path path) throws IOException {
        new Backuper().backup(path);
        replaceWord(path);
    }

    public void replaceWord(Path path) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(50);
        Path tempPath = Paths.get(path.getFileName().toString() + ".tmp");

        createTmpFile(path, tempPath);

        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
                FileChannel tempChanel = FileChannel.open(tempPath, StandardOpenOption.WRITE);
        ) {
            LinkedList<String> threeFragments = new LinkedList<>();
            SearchResult searchResult = findPosition(byteBuffer, originChanel);

            if (searchResult.isPatternWasFound()) {
                originChanel.position(searchResult.getPosition());
            } else {
                String msg = "Replacement pattern was not found";
                log.severe(msg);
                throw new RuntimeException(msg);
            }

            tempChanel.truncate(searchResult.getPosition());
            tempChanel.write(ByteBuffer.wrap(Constant.REPLACEMENT.getBytes()));
            while (byteBuffer.hasRemaining()){
                originChanel.read(byteBuffer);
                tempChanel.write(byteBuffer);
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    public void createTmpFile(Path path, Path tempPath) {
        try {
            Files.deleteIfExists(tempPath);
            Files.copy(path,tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SearchResult findPosition(ByteBuffer byteBuffer, FileChannel fc) throws IOException {
        SearchResult searchResult = new SearchResult();
        ReadFromFileHandler handler = new ReadFromFileHandler();
        int nread;
        do {
            nread = fc.read(byteBuffer);
            byteBuffer.flip();
            String text = new String(byteBuffer.array(),Charset.forName("UTF-8" ));
            byteBuffer.clear();
            handler.processReadedText(text,searchResult);
        } while (nread != -1 && byteBuffer.hasRemaining() && !searchResult.isPatternWasFound());
        return searchResult;
    }

    public void revertFromBackup(Path file) {

    }

    public Path findFile() {
        return null;
    }

    public boolean isFilePatched(Path file) {
        return false;
    }
}
