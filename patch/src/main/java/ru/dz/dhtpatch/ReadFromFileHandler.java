package ru.dz.dhtpatch;

import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
public class ReadFromFileHandler {
    LinkedList<byte[]> threeFragments = new LinkedList<byte[]>();

    public void processReadedText(byte[] text, SearchResult searchResult, long position, byte[] pattern) {
        boolean isCollectionFilled = threeFragments.size() == 3;
        threeFragments.add(text);
        if (isCollectionFilled) {
            threeFragments.removeFirst();
        }
        byte[] threeFragmentsBytes;
        threeFragmentsBytes = buildString(threeFragments);
        checkPattenr(searchResult, threeFragmentsBytes, position, pattern);
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

    public void checkPattenr(SearchResult searchResult, byte[] threeFragmentsBytes, long position, byte[] pattern) {
        SearchResult searchingIntoFragments = findWordInThreeFragments(threeFragmentsBytes,pattern);
        if (searchingIntoFragments.isPatternWasFound()) {
            searchResult.setPosition(position - threeFragmentsBytes.length + searchingIntoFragments.getPosition());
            searchResult.setPatternWasFound(true);
        }
    }

    public SearchResult findWordInThreeFragments(byte[] threeFragmentsBytes, byte[] pattern) {
        int offset = 0;
        boolean mutch = true;
        for (; offset < threeFragmentsBytes.length - pattern.length; offset++) {
            for (int j = 0; j < pattern.length; j++) {
                if (threeFragmentsBytes[offset + j] != pattern[j]) {
                    mutch = false;
                    break;
                } else {
                    mutch = true;
                }
            }
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setPosition(offset);
        searchResult.setPatternWasFound(mutch);
        return searchResult;
    }

}
