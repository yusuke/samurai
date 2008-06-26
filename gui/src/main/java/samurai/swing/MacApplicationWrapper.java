/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

class MacApplicationWrapper {
    MacApplication macApp;

    /**
     * Creates a new instance of MacApplicaionWrapper
     */
    MacApplicationWrapper(MainFrame mainFrame) {
        macApp = new MacApplication(mainFrame);
    }
}
