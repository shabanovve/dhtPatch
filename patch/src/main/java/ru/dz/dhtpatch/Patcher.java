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

        File tempFile = new File(file.getAbsolutePath().toString() + ".tmp");
        createTmpFile(tempFile);
        replaceWord(file, tempFile);
        replaceOriginFile(file, tempFile);
    }

    private void replaceOriginFile(File file, File tempFile) {
        try {
            file.delete();
            FileUtils.copy(tempFile, file);
            tempFile.delete();
            Runtime.getRuntime().exec("chmod +x " + file.getAbsolutePath().toString());
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    public void replaceWord(File file, File tempFile) {
        SearchResult searchResult = makeSearch(file, Constant.PATTERN);
        if (!searchResult.isPatternWasFound())
            throw new RuntimeException("Replacement pattern was not found");

            writeBeforeReplacement(file, searchResult, tempFile);
            writeReplacement(tempFile);
            writeAfterReplacement(file, searchResult, tempFile);
    }

    private void writeAfterReplacement(File file, SearchResult searchResult, File tempFile) {
        FileInputStream originalStream = null;
        BufferedWriter bufferWritter = null;
        try {
            originalStream = new FileInputStream(file);

            originalStream.skip(searchResult.getPosition() + Constant.TARGET_WORD.length);
            byte[] buffer = new byte[Constant.BUFFER_SIZE];

            FileWriter fileWritter = new FileWriter(tempFile.getName(),true);
            bufferWritter = new BufferedWriter(fileWritter);

            int length = 0;
            while ((length = originalStream.read(buffer)) > 0) {
                byte[] cuttedText = Utils.cute(buffer,length);
                bufferWritter.write(new String(cuttedText));
                bufferWritter.flush();
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        } finally {
            closeStream(originalStream);
            try {
                bufferWritter.close();
            } catch (IOException e) {
                log.severe(e.getMessage());
                throw new RuntimeException();
            }
        }
    }

    private void writeReplacement(File tempFile) {
        FileOutputStream tempStream = null;
        try {
            FileWriter fileWritter = new FileWriter(tempFile.getName(),true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(new String(Constant.REPLACEMENT));
            bufferWritter.close();
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        } finally {
            closeStream(tempStream);
        }
    }

    private void writeBeforeReplacement(File file, SearchResult searchResult, File tempFile) {
        FileInputStream originalStream = null;
        FileOutputStream tempStream = null;
        try {
            byte[] buffer = new byte[Constant.BUFFER_SIZE];

            int length = 0;
            int positionCount = 0;

            originalStream = new FileInputStream(file);
            tempStream = new FileOutputStream(tempFile);

            while ((length = originalStream.read(buffer)) > 0) {
                positionCount = positionCount + length;
                boolean isItReadMoreThenNeed = positionCount > searchResult.getPosition();
                if (isItReadMoreThenNeed) {
                    int bufferFragment = length - (int) (positionCount - searchResult.getPosition());
                    tempStream.write(buffer, 0, bufferFragment);
                    tempStream.flush();
                    break;
                } else {
                    tempStream.write(buffer, 0, length);
                }
            }
        } catch (IOException x) {
            log.severe("I/O Exception: " + x);
        } finally {
            closeStream(originalStream);
            closeStream(tempStream);
        }
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                log.severe(e.getMessage());
                throw new RuntimeException();
            }
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
            if (tempFile.exists()) {
                tempFile.delete();
            }
            tempFile.createNewFile();
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
