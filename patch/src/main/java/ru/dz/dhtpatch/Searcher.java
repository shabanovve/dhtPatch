package ru.dz.dhtpatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 12/02/16.
 */
public class Searcher {

    LinkedList<byte[]> threeFragments = new LinkedList<byte[]>();

    public SearchResult findPatternPosition(FileInputStream inputStream, byte[] pattern) throws IOException {
        byte[] byteBuffer = new byte[Constant.BUFFER_SIZE];
        SearchResult searchResult = new SearchResult();
        int nread;
        long positionCount = 0;
        do {
            nread = inputStream.read(byteBuffer);
            positionCount = positionCount + nread;
            if (nread > 0) {
                searchResult = processReadedText(byteBuffer, positionCount, pattern);
            }
        } while (nread > 0 && !searchResult.isPatternWasFound());
        return searchResult;
    }

    public long findTargetWordInPattern(byte[] pattern, byte[] targetWord) {
        SearchResult searchResult = findByteSequence(pattern, targetWord);
        return searchResult.getPosition();
    }

    public SearchResult processReadedText(byte[] text, long position, byte[] pattern) {
        boolean isCollectionFilled = threeFragments.size() == 3;
        if (isCollectionFilled) {
            threeFragments.removeFirst();
        }
        threeFragments.add(text.clone());
        byte[] threeFragmentsBytes;
        threeFragmentsBytes = buildString(threeFragments);
        return checkPattenr(threeFragmentsBytes, position, pattern);
    }

    public byte[] buildString(LinkedList<byte[]> threeFragments) {
        int length = 0;
        for (byte[] fragment : threeFragments) {
            length = length + fragment.length;
        }
        byte[] result = new byte[length];

        int j = 0;
        for (byte[] fragment : threeFragments) {
            for (int i = 0; i < fragment.length; i++) {
                result[j] = fragment[i];
                j++;
            }
        }
        return result;
    }

    public SearchResult checkPattenr(byte[] threeFragmentsBytes, long position, byte[] pattern) {
        SearchResult searchPatternResult = new SearchResult();
        if (threeFragmentsBytes.length < pattern.length) {
            return searchPatternResult;
        }
        SearchResult searchingIntoFragments = findByteSequence(threeFragmentsBytes, pattern);
        if (searchingIntoFragments.isPatternWasFound()) {
            searchPatternResult.setPosition(position - threeFragmentsBytes.length + searchingIntoFragments.getPosition());
            searchPatternResult.setPatternWasFound(true);
        }
        return searchPatternResult;
    }

    public SearchResult findByteSequence(byte[] threeFragmentsBytes, byte[] pattern) {
        int offset = 0;
        boolean mutch = true;
        for (; offset <= threeFragmentsBytes.length - pattern.length; offset++) {
            int mutchCount = 0;
            for (int j = 0; j < pattern.length; j++) {
                mutch = true;
                if (threeFragmentsBytes[offset + j] != pattern[j]) {
                    mutch = false;
                    break;
                } else {
                    mutchCount++;
                }
            }
            if (mutchCount == pattern.length) break;
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setPosition(offset);
        searchResult.setPatternWasFound(mutch);
        return searchResult;
    }

}
