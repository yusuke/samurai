/*
 * Copyright 2003-2012 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
