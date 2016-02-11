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
        SearchResult searchResult = makeSearch(path);

        Path tempPath = Paths.get(path.getFileName().toString() + ".tmp");
        createTmpFile(tempPath);

        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
                FileChannel tempChanel = FileChannel.open(tempPath, StandardOpenOption.WRITE);
        ) {
            tempChanel.transferFrom(originChanel, 0, searchResult.getPosition());
            tempChanel.position(tempChanel.size());
            tempChanel.write(ByteBuffer.wrap(Constant.REPLACEMENT));
            tempChanel.transferFrom(originChanel, searchResult.getPosition() + Constant.TARGET_WORD.length, originChanel.size());
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private SearchResult makeSearch(Path path) {
        SearchResult searchResult = null;
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
        ) {
            searchResult = findPosition(byteBuffer, originChanel);

            if (!searchResult.isPatternWasFound()) {
                String msg = "Replacement pattern was not found";
                log.severe(msg);
                throw new RuntimeException(msg);
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
        return searchResult;
    }

    public void createTmpFile(Path tempPath) {
        try {
            Files.deleteIfExists(tempPath);
            Files.createFile(tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SearchResult findPosition(ByteBuffer byteBuffer, FileChannel fileChannel) throws IOException {
        SearchResult searchResult = new SearchResult();
        ReadFromFileHandler handler = new ReadFromFileHandler();
        int nread;
        do {
            nread = fileChannel.read(byteBuffer);
            if (nread > 0){
                String text = new String(byteBuffer.array(), Charset.forName("UTF-8"));
                byteBuffer.clear();
                handler.processReadedText(text, searchResult, fileChannel.position());
            }
        } while (nread > 0  && !searchResult.isPatternWasFound());
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
