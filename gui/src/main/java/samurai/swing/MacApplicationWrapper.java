package samurai.swing;

/*
 * MacApplicaionWrapper.java
 *
 * Created on 2005/12/31, 23:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


/**
 * @author yusukey
 */
class MacApplicationWrapper {
    MacApplication macApp;

    /**
     * Creates a new instance of MacApplicaionWrapper
     */
    MacApplicationWrapper(MainFrame mainFrame) {
        macApp = new MacApplication(mainFrame);
    }
}
