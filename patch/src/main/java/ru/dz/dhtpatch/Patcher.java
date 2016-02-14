package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
        replaceWord(path, tempPath);
        replaceOriginFile(path, tempPath);
    }

    private void replaceOriginFile(Path path, Path tempPath) {
        try {
            Files.delete(path);
            Files.move(tempPath, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceWord(Path path, Path tempPath) {
        SearchResult searchResult = makeSearch(path, Constant.PATTERN);

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

    private SearchResult makeSearch(Path path, byte[] pattern) {
        SearchResult searchResult = null;
        try (
                FileChannel originChanel = FileChannel.open(path, StandardOpenOption.READ);
        ) {
            Searcher searcher = new Searcher();
            searchResult = searcher.findPatternPosition(originChanel, pattern);

            if (!searchResult.isPatternWasFound()) {
                String msg = "Replacement pattern was not found";
                log.severe(msg);
                throw new RuntimeException(msg);
            } else {
                correctToTargetWord(searchResult, searcher);
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
        return searchResult;
    }

    private void correctToTargetWord(SearchResult searchResult, Searcher searcher) {
        long targetWordOffset = searcher.findTargetWordInPattern(Constant.PATTERN, Constant.TARGET_WORD);
        long patternOffset = searchResult.getPosition();
        searchResult.setPosition(patternOffset + targetWordOffset);
    }

    public void createTmpFile(Path tempPath) {
        try {
            Files.deleteIfExists(tempPath);
            Files.createFile(tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void revertFromBackup(Path path) {
        System.out.println("Revert from backup");
        try {
            Path backupPath = Paths.get(Constant.fileName);
            Files.delete(path);
            Files.move(backupPath, path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Path findFile() {
        File f = new File(System.getProperty("java.class.path"));
        String dir = f.getAbsoluteFile().getParentFile().toString();
        String pathToFile = dir + "/" + Constant.fileName;
        Path path = Paths.get(pathToFile);
        if (!Files.exists(path)) {
            throw new RuntimeException("File " + pathToFile + "not found");
        }
        return path;
    }

    public boolean isFilePatched(Path path) {
        SearchResult searchResult = makeSearch(path, Constant.PATCHED_PATTERN);
        return searchResult.isPatternWasFound();
    }
}
