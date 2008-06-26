/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

public class SearchResult {
    final int start;
    final int end;
    final boolean found;

    public SearchResult(int start, int end) {
        this.start = start;
        this.end = end;
        this.found = true;
    }

    public SearchResult() {
        this.start = 0;
        this.end = 0;
        this.found = false;
    }

    public boolean found() {
        return this.found;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

}
