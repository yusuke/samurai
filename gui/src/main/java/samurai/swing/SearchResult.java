package samurai.swing;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
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
