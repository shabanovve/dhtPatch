package ru.dz.dhtpatch;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Vladimir Shabanov on 12/02/16.
 */
public class ReadFromFileHandlerTest {


    @Test
    public void testFindWordInThreeFragments() throws Exception {
        byte[] text = {0x6C, 0x65, 0x6E, 0x67, 0x74, 0x68, 0x00, 0x70, 0x72, 0x69, 0x76, 0x61, 0x74, 0x65};
        long position = new ReadFromFileHandler().findWordInThreeFragments(text,Constant.PATTERN);
        assert position > 0;
    }

    @Test
    public void testBuildString() throws Exception {
        LinkedList<byte[]> fragments = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            byte[] bytes = {0x1,0x2,0x3};
            fragments.add(bytes);
        }
        byte[] result = new ReadFromFileHandler().buildString(fragments);
        byte[] sample = {0x1,0x2,0x3,0x1,0x2,0x3,0x1,0x2,0x3};
        assert Arrays.equals(result,sample);
    }
}