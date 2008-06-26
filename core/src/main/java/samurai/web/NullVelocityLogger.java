/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.web;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

public class NullVelocityLogger implements LogSystem {
    public NullVelocityLogger() {
    }

    public void init(RuntimeServices runtimeServices) throws Exception {
    }

    public void logVelocityMessage(int _int, String string) {
//    System.out.println(string);
    }

}
