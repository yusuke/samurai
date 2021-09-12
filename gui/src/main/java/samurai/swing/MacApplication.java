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

import java.awt.*;
import java.awt.desktop.*;

public class MacApplication implements AboutHandler, PreferencesHandler, QuitHandler {
    MainFrame frame;

    public MacApplication(MainFrame frame) {
        this.frame = frame;
        Desktop desktop = Desktop.getDesktop();

        desktop.setAboutHandler(this);
        desktop.setPreferencesHandler(this);
        desktop.setQuitHandler(this);
    }

    @Override
    public void handleAbout(AboutEvent e) {
        frame.handleAbout();
    }

    @Override
    public void handlePreferences(PreferencesEvent e) {
        frame.handlePreferences();
    }

    @Override
    public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
        frame.handleQuit();
    }
}
