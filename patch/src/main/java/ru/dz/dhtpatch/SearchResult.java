package ru.dz.dhtpatch;

import lombok.Data;

/**
 * Created by Vladimir Shabanov on 10/02/16.
 */
@Data
public class SearchResult {
    private long position = 0l;
    private boolean patternWasFound = false;
}
