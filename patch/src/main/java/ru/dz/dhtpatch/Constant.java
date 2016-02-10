package ru.dz.dhtpatch;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
public class Constant {
    public static final String TARGET_WORD = "private";
    public static final String PATTERN = "length\u0000" + TARGET_WORD;
    public static final String REPLACEMENT = "priuate";
}
