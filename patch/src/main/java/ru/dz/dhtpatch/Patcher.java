package ru.dz.dhtpatch;

import lombok.extern.java.Log;

import java.io.*;
import java.net.URISyntaxException;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
@Log
public class Patcher {

    public void start() {
        try {
            File file = FileUtils.findFile(Constant.FILE_NAME);
            if (isFilePatched(file)) {
                log.info("File already is patched");
                revertFromBackup(file);
            } else {
                makePatch(file);
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (URISyntaxException e) {
            log.severe(e.getMessage());
        }

    }

    public void makePatch(File file) throws IOException {
        new Backuper().backup(file);

        File tempFile = new File(file.toURI().toString() + ".tmp");
        createTmpFile(tempFile);
        replaceWord(file, tempFile);
        replaceOriginFile(file, tempFile);
    }

    private void replaceOriginFile(File file, File tempFile) {
        try {
            file.delete();
            FileUtils.copy(tempFile, file);
            tempFile.delete();
            Runtime.getRuntime().exec("chmod +x " + file.toURI().toString());
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    public void replaceWord(File file, File tempFile) {
        SearchResult searchResult = makeSearch(file, Constant.PATTERN);
        if (!searchResult.isPatternWasFound())
            throw new RuntimeException("Replacement pattern was not found");

        FileInputStream originalStream = null;
        FileOutputStream tempStream = null;
        try {
            originalStream = new FileInputStream(file);
            tempStream = new FileOutputStream(tempFile);

            writeBeforeReplacement(originalStream, searchResult, tempStream);
            writeReplacement(tempStream);
            writeAfterReplacement(originalStream, searchResult, tempStream);
        } catch (FileNotFoundException e) {
            log.severe(e.getMessage());
        } finally {
            if (originalStream != null) {
                try {
                    originalStream.close();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
            if (tempStream != null) {
                try {
                    tempStream.close();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
        }

    }

    private void writeAfterReplacement(FileInputStream originalStream, SearchResult searchResult, FileOutputStream tempStream) {
        try {
            byte[] buffer = new byte[Constant.BUFFER_SIZE];
            int length;
            while ((length = originalStream.read(buffer)) > 0) {
                tempStream.write(buffer, 0, length);
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private void writeReplacement(FileOutputStream tempStream) {
        try {
            tempStream.write(Constant.REPLACEMENT);
            tempStream.flush();
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private void writeBeforeReplacement(FileInputStream originalStream, SearchResult searchResult, FileOutputStream tempStream) {
        try {
            byte[] buffer = new byte[Constant.BUFFER_SIZE];

            int length;
            //copy the file content in bytes
            int positionCount = 0;
            while ((length = originalStream.read(buffer)) > 0) {
                positionCount = positionCount + length;
                boolean isItReadMoreThenNeed = positionCount > searchResult.getPosition();
                if (isItReadMoreThenNeed) {
                    int bufferFragment = (int) (positionCount - searchResult.getPosition());
                    tempStream.write(buffer, 0, bufferFragment);
                    tempStream.flush();
                    break;
                } else {
                    tempStream.write(buffer, 0, length);
                }
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        }
    }

    private SearchResult makeSearch(File file, byte[] pattern) {
        SearchResult searchResult = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            Searcher searcher = new Searcher();
            searchResult = searcher.findPatternPosition(inputStream, pattern);

            if (searchResult.isPatternWasFound()) {
                correctToTargetWord(searchResult, searcher);
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
        }
        return searchResult;
    }

    private void correctToTargetWord(SearchResult searchResult, Searcher searcher) {
        long targetWordOffset = searcher.findTargetWordInPattern(Constant.PATTERN, Constant.TARGET_WORD);
        long patternOffset = searchResult.getPosition();
        searchResult.setPosition(patternOffset + targetWordOffset);
    }

    public void createTmpFile(File tempFile) {
        try {
            if (!tempFile.exists()) {
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }
                tempFile.createNewFile();
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void revertFromBackup(File file) {
        new Backuper().revert(file);
    }

    public boolean isFilePatched(File file) {
        SearchResult searchResult = makeSearch(file, Constant.PATCHED_PATTERN);
        return searchResult.isPatternWasFound();
    }
}
