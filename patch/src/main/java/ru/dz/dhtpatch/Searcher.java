package ru.dz.dhtpatch;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Vladimir Shabanov on 12/02/16.
 */
public class Searcher {

    public SearchResult findPosition(ByteBuffer byteBuffer, FileChannel fileChannel, byte[] pattern) throws IOException {
        SearchResult searchResult = new SearchResult();
        ReadFromFileHandler handler = new ReadFromFileHandler();
        int nread;
        do {
            nread = fileChannel.read(byteBuffer);
            if (nread > 0){
                byteBuffer.flip();
                byteBuffer.clear();
                handler.processReadedText(byteBuffer.array(), searchResult, fileChannel.position(),pattern);
            }
        } while (nread > 0  && !searchResult.isPatternWasFound());
        return searchResult;
    }

}
