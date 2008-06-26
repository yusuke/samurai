/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

public class IBMFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = 9102293976017908812L;

    /*package*/ IBMFullThreadDump(String header) {
        super(header);
    }

    /*package*/ boolean isThreadHeader(String line) {
        return line.startsWith("3XMTHREADINFO");
    }

    /*package*/ boolean isThreadFooter(String line) {
        return false;
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return !line.startsWith("NULL");
    }
}
