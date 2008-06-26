/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import java.io.Serializable;
import java.util.List;

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
