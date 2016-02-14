package ru.dz.dhtpatch;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 12/02/16.
 */
public class ReadFromFileHandlerTest {

    @Test
    public void testBuildString() throws Exception {
        LinkedList<byte[]> fragments = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            byte[] bytes = {0x1, 0x2, 0x3};
            fragments.add(bytes);
        }
        byte[] result = new ReadFromFileHandler().buildString(fragments);
        byte[] sample = {0x1, 0x2, 0x3, 0x1, 0x2, 0x3, 0x1, 0x2, 0x3};
        assert Arrays.equals(result, sample);
    }

    @Test
    public void testFindWordInThreeFragments() throws Exception {
        ReadFromFileHandler handler = new ReadFromFileHandler();
        SearchResult searchResult = handler.findWordInThreeFragments(Constant.PATTERN, Constant.PATTERN);
        assert searchResult.isPatternWasFound();
    }

    @Test
    public void testProcessReadedText() throws Exception {
        ReadFromFileHandler handler = new ReadFromFileHandler();
        SearchResult searchResult = new SearchResult();
        final long position = (long) Constant.PATTERN.length;
        handler.processReadedText(Constant.PATTERN, searchResult, position, Constant.PATTERN);
        assert searchResult.isPatternWasFound();
        handler.processReadedText(Constant.PATTERN, searchResult, position, Constant.PATCHED_PATTERN);
        assert !searchResult.isPatternWasFound();
    }

    @Test
    public void testCheckPattenr() throws Exception {
        ReadFromFileHandler handler = new ReadFromFileHandler();
        SearchResult searchResult = new SearchResult();
        final long position = (long) Constant.PATTERN.length;
        handler.checkPattenr(searchResult, Constant.PATTERN, position, Constant.PATTERN);
        assert searchResult.isPatternWasFound();
    }
}