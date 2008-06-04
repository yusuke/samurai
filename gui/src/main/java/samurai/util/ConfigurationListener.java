package samurai.util;


/**
 * an interface that indicate a object which needs to be notified the application's configuration is changed
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */

public interface ConfigurationListener {
    void onConfigurationChanged(Configuration config);
}
