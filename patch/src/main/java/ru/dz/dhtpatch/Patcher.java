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
                System.out.println("File already is patched");
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

        Path tempPath = Paths.get(path.getFileName().toString() + ".tmp");
        createTmpFile(tempPath);
        replaceWord(path,tempPath);
        replaceOriginFile(path, tempPath);
    }

    private void replaceOriginFile(Path path, Path tempPath) {
        try {
            Files.delete(path);
            Files.move(tempPath,path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceWord(Path path,Path tempPath) {
        SearchResult searchResult = makeSearch(path, new String(Constant.PATTERN));

        writeBeforeReplacement(path, searchResult, tempPath);
        writeReplacement(tempPath);
        writeAfterReplacement(path, searchResult, tempPath);
    }

    private void writeAfterReplacement(Path path, SearchResult searchResult, Path tempPath) {
        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
                FileChannel tempChanel = FileChannel.open(tempPath, StandardOpenOption.WRITE);
        ) {
            long afterTargetWord = searchResult.getPosition() + Constant.TARGET_WORD.length;
            long count = originChanel.size() - afterTargetWord;
            originChanel.position(afterTargetWord);
            tempChanel.transferFrom(originChanel, afterTargetWord, count);
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private void writeReplacement(Path tempPath) {
        try (
                FileChannel tempChanel = FileChannel.open(tempPath, StandardOpenOption.WRITE);
        ) {
            tempChanel.position(tempChanel.size());
            tempChanel.write(ByteBuffer.wrap(Constant.REPLACEMENT));
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private void writeBeforeReplacement(Path path, SearchResult searchResult, Path tempPath) {
        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
                FileChannel tempChanel = FileChannel.open(tempPath, StandardOpenOption.WRITE);
        ) {
            tempChanel.transferFrom(originChanel, 0, searchResult.getPosition());
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private SearchResult makeSearch(Path path, String pattern) {
        SearchResult searchResult = null;
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
        ) {
            searchResult = findPosition(byteBuffer, originChanel, pattern);

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

    public SearchResult findPosition(ByteBuffer byteBuffer, FileChannel fileChannel, String pattern) throws IOException {
        SearchResult searchResult = new SearchResult();
        ReadFromFileHandler handler = new ReadFromFileHandler();
        int nread;
        do {
            nread = fileChannel.read(byteBuffer);
            if (nread > 0){
                byteBuffer.flip();
                String text = new String(byteBuffer.array(), Charset.forName("UTF-8"));
                byteBuffer.clear();
                handler.processReadedText(text, searchResult, fileChannel.position(),pattern);
            }
        } while (nread > 0  && !searchResult.isPatternWasFound());
        return searchResult;
    }

    public void revertFromBackup(Path path) {
        System.out.println("Revert from backup");
        try {
            Path backupPath = Paths.get(Constant.fileName);
            Files.delete(path);
            Files.move(backupPath,path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Path findFile() {
        return Paths.get(Constant.fileName);
    }

    public boolean isFilePatched(Path path) {
        SearchResult searchResult = makeSearch(path,new String(Constant.PATCHED_PATTERN));
        return searchResult.isPatternWasFound();
    }
}
