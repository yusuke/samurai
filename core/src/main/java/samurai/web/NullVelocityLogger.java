package samurai.web;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class NullVelocityLogger implements LogSystem {
    public NullVelocityLogger() {
    }

    public void init(RuntimeServices runtimeServices) throws Exception {
    }

    public void logVelocityMessage(int _int, String string) {
//    System.out.println(string);
    }

}
