/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

class ConfigDialog_config_dumpFontSize_itemAdapter implements java.awt.event.
        ItemListener {
    ConfigDialog adaptee;

    ConfigDialog_config_dumpFontSize_itemAdapter(ConfigDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(java.awt.event.ItemEvent e) {
        adaptee.config_dumpFontSize_itemStateChanged(e);
    }
}
