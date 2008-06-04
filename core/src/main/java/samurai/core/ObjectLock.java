package samurai.core;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class ObjectLock implements Serializable {
    private SunThreadDump threadDump;
    private List objects;
    private static final long serialVersionUID = -1899274398012153019L;

    public ObjectLock(SunThreadDump threadDump, List objects) {
        this.threadDump = threadDump;
        this.objects = objects;
    }

    public SunThreadDump getThreadDump() {
        return this.threadDump;
    }

    public List getObject() {
        return this.objects;
    }
}
