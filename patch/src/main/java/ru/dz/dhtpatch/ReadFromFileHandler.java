package ru.dz.dhtpatch;

import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
public class ReadFromFileHandler {
    LinkedList<byte[]> threeFragments = new LinkedList<byte[]>();

    public void processReadedText(byte[] text, SearchResult searchResult, long position, byte[] pattern) {
        boolean shouldFillCollection = threeFragments.size() < 3;
        if (shouldFillCollection) {
            threeFragments.add(text);
        } else {
            byte[] threeFragmentsBytes;
            threeFragments.removeFirst();
            threeFragments.add(text);
            threeFragmentsBytes = buildString(threeFragments);
            checkPattenr(searchResult, threeFragmentsBytes, position, pattern);
        }
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

    private void checkPattenr(SearchResult searchResult, byte[] threeFragmentsBytes, long position, byte[] pattern) {
        SearchResult searchingIntoFragments = findWordInThreeFragments(threeFragmentsBytes,pattern);
        if (searchingIntoFragments.isPatternWasFound()) {
            searchResult.setPosition(position - threeFragmentsBytes.length + searchingIntoFragments.getPosition());
            searchResult.setPatternWasFound(true);
        }
    }

    public SearchResult findWordInThreeFragments(byte[] threeFragmentsBytes, byte[] pattern) {
//        long offset = threeFragmentsString.indexOf(new String(Constant.TARGET_WORD));
        int offset = 0;
        boolean mutch = true;
        for (; offset < threeFragmentsBytes.length - pattern.length; offset++) {
            mutch = true;
            for (int j = 0; j < pattern.length; j++) {
                if (threeFragmentsBytes[offset + j] != pattern[j]) {
                    mutch = false;
                    break;
                }
            }
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setPosition(offset);
        searchResult.setPatternWasFound(mutch);
        return searchResult;
    }

}
