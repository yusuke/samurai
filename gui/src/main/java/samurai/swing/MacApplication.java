/*
 * MRJHandlerImpl.java
 *
 * Created on 2005/12/30, 22:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package samurai.swing;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * @author yusukey
 */
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
