package ru.dz.dhtpatch;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
public class Constant {
    public static final byte[] TARGET_WORD = {0x70,0x72,0x69,0x76,0x61,0x74,0x65};
    public static final byte[] PATTERN = {0x6C,0x65,0x6E,0x67,0x74,0x68,0x00,0x70,0x72,0x69,0x76,0x61,0x74,0x65};
    public static final byte[] PATCHED_PATTERN = {0x6C,0x65,0x6E,0x67,0x74,0x68,0x00,0x70,0x72,0x69,0x75,0x61,0x74,0x65};
    public static final byte[] REPLACEMENT = {0x70,0x72,0x69,0x75,0x61,0x74,0x65};
    public static final String FILE_NAME = "uTorrent";
    public static final String DIRECTORY_NAME = "/Applications/uTorrent.app/Contents/MacOS";
    public static final String BACKUP_FILE_NAME = FILE_NAME + ".backup";
    public static final int BUFFER_SIZE = 1024;
    public static final int TREE_FRAGMENTS = 3;
    public static final int CHANGED_SYMBOL_POSITION = 84;
}
