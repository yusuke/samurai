/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class MacApplication extends ApplicationAdapter {
    MainFrame frame;

    public MacApplication(MainFrame frame) {
        this.frame = frame;
        Application application = Application.getApplication();
        application.addApplicationListener(this);
        application.addAboutMenuItem();
        application.setEnabledAboutMenu(true);
        application.addPreferencesMenuItem();
        application.setEnabledPreferencesMenu(true);
    }

    public void handleAbout(ApplicationEvent event) {
        event.setHandled(true);
        frame.handleAbout();
    }

    public void handleQuit(ApplicationEvent event) {
        event.setHandled(true);
        frame.handleQuit();
    }

    public void handlePreferences(ApplicationEvent event) {
        event.setHandled(true);
        frame.handlePreferences();
    }
}
