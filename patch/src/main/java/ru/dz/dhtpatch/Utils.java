package ru.dz.dhtpatch;

/**
 * Created by Vladimir Shabanov on 25/02/16.
 */
public class Utils {
    protected static byte[] cute(byte[] byteBuffer, int nread) {
        byte[] result = new byte[nread];
        for (int i = 0; i < nread; i++) {
            result[i] = byteBuffer[i];
        }
        return result;
    }

}
