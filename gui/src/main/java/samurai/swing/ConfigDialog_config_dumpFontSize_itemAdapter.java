package samurai.swing;

/**
 * Created by IntelliJ IDEA.
 * User: yusukey
 * Date: May 9, 2008
 * Time: 9:39:23 AM
 * To change this template use File | Settings | File Templates.
 */
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
