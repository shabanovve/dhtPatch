package ru.dz.dhtpatch;

import org.junit.Before;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by Vladimir Shabanov on 09/02/16.
 */
public class PatcherTest {

    private static final int MAX = 3;
    private Path path;
    private String fileName = "test";


    @Before
    public void init() throws IOException {
        path = createTestFile();
    }

    @org.junit.Test
    public void testMakePatch() throws Exception {

    }

    @org.junit.Test
    public void testReplaceWord() throws Exception {
        new Patcher().makePatch(path);

    }

    private Path createTestFile() throws IOException {
        Path path = Paths.get(fileName);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE)){
            fillWithStrings(fileChannel,0);
            fileChannel.write(ByteBuffer.wrap(Constant.PATTERN));
            fillWithStrings(fileChannel,MAX);
        } catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }

    private void fillWithStrings(FileChannel fileChannel, int start) throws IOException {
        for (Integer i = start; i < start + MAX; i++){
            String text = i.toString() + "test ";
            fileChannel.write(ByteBuffer.wrap(text.getBytes()));
        }
    }


}