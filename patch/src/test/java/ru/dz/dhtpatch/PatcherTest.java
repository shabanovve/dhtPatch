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

        Path pathOftemp = Paths.get(fileName + ".tmp");
        try (
                FileChannel originChannel = FileChannel.open(path, StandardOpenOption.READ);
                FileChannel tempChannel = FileChannel.open(pathOftemp, StandardOpenOption.READ);
        ) {
            assert originChannel.size() == tempChannel.size();

            ByteBuffer originByteBuffer = ByteBuffer.allocate(10);
            ByteBuffer tempByteBuffer = ByteBuffer.allocate(10);

            int nReadOrgin, nReadTemp;
            byte[] origin,temp;
            do {
                nReadOrgin = originChannel.read(originByteBuffer);
                nReadTemp = tempChannel.read(tempByteBuffer);

                origin = originByteBuffer.array();
                temp = tempByteBuffer.array();

                for (int i = 0; i < originByteBuffer.array().length; i++) {
                    if (i != 8 && origin[i] != temp[i]) {
                        assert false;//String to place breakpoint
                    }
                }
                originByteBuffer.clear();
                tempByteBuffer.clear();
            } while (nReadOrgin > 0 && nReadTemp > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Path createTestFile() throws IOException {
        Path path = Paths.get(fileName);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            fillWithStrings(fileChannel, 0);
            fileChannel.write(ByteBuffer.wrap(Constant.PATTERN));
            fillWithStrings(fileChannel, MAX);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private void fillWithStrings(FileChannel fileChannel, int start) throws IOException {
        for (Integer i = start; i < start + MAX; i++) {
            String text = i.toString() + "test ";
            fileChannel.write(ByteBuffer.wrap(text.getBytes()));
        }
    }


}