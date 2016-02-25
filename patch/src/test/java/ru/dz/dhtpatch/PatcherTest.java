package ru.dz.dhtpatch;

import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
public class PatcherTest {

    private static final int MAX = 3;
    private File file;
    private String fileName = "test";
    private File tempFile = null;


    @Before
    public void init() throws IOException {
        file = createTestFile();
        tempFile = new File(file.getAbsolutePath() + ".tmp");
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @org.junit.Test
    public void testOfBuffer() {
        assert Constant.TREE_FRAGMENTS * Constant.BUFFER_SIZE > Constant.PATTERN.length;
    }

    @org.junit.Test
    public void testReplaceWord() throws Exception {
        Patcher patcher = new Patcher();
        patcher.createTmpFile(tempFile);
        patcher.replaceWord(file, tempFile);

        FileInputStream originStream = null;
        FileInputStream tempStream = null;
        try {
            originStream = new FileInputStream(file);
            tempStream = new FileInputStream(tempFile);
            assert file.length() == tempFile.length();

            byte[] originByteBuffer = new byte[1024];
            byte[] tempByteBuffer = new byte[1024];

            int nReadOrgin, nReadTemp;
            do {
                nReadOrgin = originStream.read(originByteBuffer);
                nReadTemp = tempStream.read(tempByteBuffer);


                for (int i = 0; i < nReadOrgin; i++) {
                    if (i != 28 && originByteBuffer[i] != tempByteBuffer[i]) {
                        assert false;//String to place breakpoint
                    }
                }
            } while (nReadOrgin > 0 && nReadTemp > 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (originStream != null) {
                originStream.close();
            }
            if (tempStream != null) {
                tempStream.close();
            }
        }
    }

    private File createTestFile() throws IOException {
        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(file);
        try {
            fillWithStrings(outputStream, 0);
            outputStream.write(Constant.PATTERN);
            fillWithStrings(outputStream, MAX);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return file;
    }

    private void fillWithStrings(FileOutputStream outputStream, int start) throws IOException {
        for (Integer i = start; i < start + MAX; i++) {
            String text = i.toString() + "test ";
            outputStream.write(text.getBytes());
        }
    }


}