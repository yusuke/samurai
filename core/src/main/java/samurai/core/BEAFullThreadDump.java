/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

public class BEAFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = -1445294393744936922L;

    /*package*/ BEAFullThreadDump(String header) {
        super(header);
    }

    /*package*/ boolean isThreadHeader(String line) {
        return (line.startsWith("\"") || line.startsWith("Thread-")) && -1 != line.indexOf("prio");
    }

    /*package*/ boolean isThreadFooter(String line) {
        return "".equals(line) || -1 != line.indexOf("}");
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return !line.startsWith("======================================");
    }

}
