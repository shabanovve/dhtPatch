package ru.dz.dhtpatch;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 12/02/16.
 */
public class SearcherTest {

    @Test
    public void testBuildString() throws Exception {
        LinkedList<byte[]> fragments = new LinkedList<>();

        byte[] bytesA = {0x1, 0x2, 0x3};
        fragments.add(bytesA);
        byte[] bytesB = {0x4, 0x5, 0x6};
        fragments.add(bytesB);
        byte[] bytesC = {0x7, 0x8, 0x9};
        fragments.add(bytesC);

        byte[] result = new Searcher().buildString(fragments);
        byte[] sample = {0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9};
        assert Arrays.equals(result, sample);
    }

    @Test
    public void testFindWordInThreeFragments() throws Exception {
        Searcher searcher = new Searcher();
        SearchResult searchResult = searcher.findByteSequence(Constant.PATTERN, Constant.PATTERN);
        assert searchResult.isPatternWasFound();
    }

    @Test
    public void testProcessReadedText() throws Exception {
        Searcher searcher = new Searcher();

        final long position = (long) Constant.PATTERN.length;
        SearchResult searchResult = searcher.processReadedText(Constant.PATTERN, position, Constant.PATTERN);
        assert searchResult.isPatternWasFound();

        searchResult = searcher.processReadedText(Constant.PATTERN, position, Constant.PATCHED_PATTERN);
        assert !searchResult.isPatternWasFound();
    }

    @Test
    public void testProcessReadedTextUnmutch() throws Exception {
        Searcher searcher = new Searcher();

        final long position = (long) Constant.PATTERN.length;
        SearchResult searchResult = searcher.processReadedText(Constant.PATTERN, position, Constant.PATCHED_PATTERN);
        assert !searchResult.isPatternWasFound();
    }

    @Test
    public void testCheckPattenr() throws Exception {
        Searcher searcher = new Searcher();
        final long position = (long) Constant.PATTERN.length;
        SearchResult searchResult = searcher.checkPattenr(Constant.PATTERN, position, Constant.PATTERN);
        assert searchResult.isPatternWasFound();
    }

    @Test
    public void testFindByteSequence() throws Exception {
        SearchResult searchResult = new Searcher().findByteSequence(Constant.PATTERN,Constant.TARGET_WORD);
        assert 7 == searchResult.getPosition();
    }
}