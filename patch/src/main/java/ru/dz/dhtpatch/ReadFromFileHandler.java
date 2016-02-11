package ru.dz.dhtpatch;

import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
public class ReadFromFileHandler {
    LinkedList<String> threeFragments = new LinkedList<String>();
    StringBuilder stringBuilder = new StringBuilder();

    public void processReadedText(String text, SearchResult searchResult, long position) {
        boolean shouldFillCollection = threeFragments.size() < 3;
        if (shouldFillCollection){
            threeFragments.add(text);
        } else {
            String threeFragmentsString;
            threeFragments.removeFirst();
            threeFragments.add(text);
            threeFragmentsString = buildString();
            checkPattenr(searchResult, threeFragmentsString,position);
        }
    }

    private void checkPattenr(SearchResult searchResult, String threeFragmentsString, long position) {
        if (threeFragmentsString.contains(new String(Constant.PATTERN))) {
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
        Long pointToTargetWord = 0l;
        int offset = threeFragmentsString.indexOf(new String(Constant.TARGET_WORD));
        pointToTargetWord = searchResult.getPosition() - threeFragments.size() + offset;
        return pointToTargetWord;
    }

}
