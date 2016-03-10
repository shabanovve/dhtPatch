package ru.dz.dhtpatch;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Vladimir Shabanov on 09/03/16.
 */
public class FileUtilsTest {

    @Test
    public void testFindFile() throws Exception {
        String path = "test";
        File file = new File(path);
        if (file != null && !file.exists()) {
            file.createNewFile();
        }

        boolean testResult = false;
        try {
            FileUtils.findFileInCurrentDirectory(path);
        } catch (Exception e) {
            testResult = true;
        }
        assert testResult;

        if (file.exists())
            file.delete();

        testResult = false;
        try {
            FileUtils.findFileInCurrentDirectory(path);
        } catch (FileNotFoundException e) {
            testResult = true;
        }

        assert testResult;

    }

    @Test
    public void testCopy() throws Exception {
        String path = "test";
        String pathCopy = "testCopy";

        File origin = new File(path);
        if (origin != null && !origin.exists())
            origin.createNewFile();

        File copy = new File(pathCopy);
        if (copy != null && copy.exists())
            copy.delete();

        FileUtils.copy(origin, copy);

        assert copy.exists();

        copy.delete();
        origin.delete();
    }

    @Test
    public void testFindTargetFile() throws Exception {
        String path = "uTorrent";

        //cannot create uTorrent file in temp directory
        File file = File.createTempFile(path,"");
        if (file != null && !file.exists()) {
            assert false;
        }

        File directory = file.getParentFile();
        //create uTorrent file in temp directory
        File targetFile = new File(directory.getAbsolutePath() + "/" + path);
        if (targetFile != null && !targetFile.exists()) {
            targetFile.createNewFile();
        }

        File checkFile = FileUtils.findTargetFile(targetFile.getAbsolutePath());
        boolean testResult = checkFile.exists();

        file.delete();
        targetFile.delete();

        assert testResult;


    }
}