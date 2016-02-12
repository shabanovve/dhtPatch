package ru.dz.dhtpatch;

import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
public class ReadFromFileHandler {
    LinkedList<String> threeFragments = new LinkedList<String>();
    StringBuilder stringBuilder = new StringBuilder();

    public void processReadedText(String text, SearchResult searchResult, long position, String pattern) {
        boolean shouldFillCollection = threeFragments.size() < 3;
        if (shouldFillCollection){
            threeFragments.add(text);
        } else {
            String threeFragmentsString;
            threeFragments.removeFirst();
            threeFragments.add(text);
            threeFragmentsString = buildString();
            checkPattenr(searchResult, threeFragmentsString,position,pattern);
        }
    }

    private void checkPattenr(SearchResult searchResult, String threeFragmentsString, long position, String pattern) {
        if (threeFragmentsString.contains(pattern)) {
            Long offset = findWordInThreeFragments(searchResult, threeFragments, threeFragmentsString);
            searchResult.setPosition(position - threeFragmentsString.length() + offset);
            searchResult.setPatternWasFound(true);
        }
    }

    private String buildString() {
        String threeFragmentsString;
        stringBuilder.setLength(0);
        for (String fragment : threeFragments) {
            stringBuilder.append(fragment);
        }
        threeFragmentsString = stringBuilder.toString();
        return threeFragmentsString;
    }

    private Long findWordInThreeFragments(SearchResult searchResult, LinkedList<String> threeFragments, String threeFragmentsString) {
        long offset = threeFragmentsString.indexOf(new String(Constant.TARGET_WORD));
        return offset;
    }

}
